package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.*;

public class StoreManager {

    ArrayList<Trade> trades;
    ArrayList<Player> playersWithStoreOpen;
    int mostRecentTradeID;
    HashMap<Player, ArrayList<Pickup>> itemPickup;


    public StoreManager() {
        // todo: Grabs all the trades from the database
        //  all trades greater than 30 days old should be removed from the market and returned to the owner

        // Populate the trades table
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM trades WHERE status = ?;");
            ps.setInt(1, TradeStatus.OPEN.ordinal());
            ResultSet rs = ps.executeQuery();

            trades = new ArrayList<>();

            while (rs.next()) {

                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                Player buyer = null;
                String buyerID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getPlayer(UUID.fromString(buyerID));
                }

                trades.add(new Trade(
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("wanted")),
                        ItemStack.deserializeBytes(rs.getBytes("display_item")),
                        rs.getInt("num_wanted"),
                        Bukkit.getPlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        rs.getInt("trade_id"),
                        rs.getLong("time_listed"),
                        TradeStatus.values()[rs.getInt("status")]
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try { // Get the most recent trade ID from the database
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM trades ORDER BY trade_id DESC LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            mostRecentTradeID = rs.getInt("trade_id");

        } catch (SQLException e) {
            WorldShop.getPlugin(WorldShop.class).getLogger().warning("No database found or data in database, setting most recent trade ID to 0.");
            mostRecentTradeID = 0;
        }

        // Populate the itemPickup table
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM pickup WHERE collected = ?;");
            ps.setBoolean(1, false);
            ResultSet rs = ps.executeQuery();

            itemPickup = new HashMap<>();

            while (rs.next()) {
                // Player to store value in
                Player player = Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid")));
                // Itemstack list to add to
                ArrayList<Pickup> pickups = itemPickup.get(player);
                if (pickups == null) {
                    pickups = new ArrayList<>();
                }

                // Add the item to the list of trades the player has to collect from
                pickups.add(new Pickup(player, ItemStack.deserializeBytes(rs.getBytes("pickup_item")), rs.getInt("trade_id"), false, 0L));
                // Update the itemPickup table
                itemPickup.put(player, pickups);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        playersWithStoreOpen = new ArrayList<>();
    }

    public void addToStore(Trade trade) {
        trades.add(trade);

        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO trades (trade_id, seller_uuid, display_item, for_sale, wanted, num_wanted, completed, buyer_uuid, time_listed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setInt(1, trade.getTradeID());
            ps.setString(2, String.valueOf(trade.getSeller().getUniqueId()));
            ps.setBytes(3, trade.getDisplayItem().serializeAsBytes());
            ps.setBytes(4, trade.getForSale().serializeAsBytes());
            ps.setBytes(5, trade.getWanted().serializeAsBytes());
            ps.setInt(6, trade.getAmountWanted());
            ps.setInt(7, trade.getStatus().ordinal());

            if ((trade.getBuyer() != null)) {
                ps.setString(8, String.valueOf(trade.getSeller().getUniqueId()));
            } else {
                ps.setNull(8, Types.NULL);
            }

            ps.setLong(9, trade.getTimeListed());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToStore(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller) {
        Trade trade = new Trade(forSale, wanted, amountWanted, seller, getNextTradeID());
        trades.add(trade);

        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO trades (trade_id, seller_uuid, display_item, for_sale, wanted, num_wanted, status, buyer_uuid, time_listed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setInt(1, trade.getTradeID());
            ps.setString(2, String.valueOf(trade.getSeller().getUniqueId()));
            ps.setBytes(3, trade.getDisplayItem().serializeAsBytes());
            ps.setBytes(4, trade.getForSale().serializeAsBytes());
            ps.setBytes(5, trade.getWanted().serializeAsBytes());
            ps.setInt(6, trade.getAmountWanted());
            ps.setInt(7, trade.getStatus().ordinal());

            if ((trade.getBuyer() != null)) {
                ps.setString(8, String.valueOf(trade.getSeller().getUniqueId()));
            } else {
                ps.setNull(8, Types.NULL);
            }

            ps.setLong(9, trade.getTimeListed());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromStore(Trade trade, Player player, TradeStatus status) {
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("UPDATE trades SET status = ?, buyer_uuid = ? WHERE trade_id = ?;");
            trade.setStatus(status);
            trade.setBuyer(player);
            ps.setInt(1, trade.getStatus().ordinal());
            ps.setString(2, String.valueOf(trade.getBuyer().getUniqueId()));
            ps.setInt(3, trade.getTradeID());

            ps.executeUpdate();

            trades.remove(trade);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Add the buyer and seller pickups to the pickup database.
        Pickup buyerPickup = new Pickup(player, trade.getForSale(), trade.getTradeID(), false, 0L);
        Pickup sellerPickup = new Pickup(trade.getSeller(), trade.getWanted(), trade.getTradeID(), false, 0L);

        // buyer
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO pickup (player_uuid, trade_id, pickup_item, collected, time_collected) VALUES (?, ?, ?, ?, ?); ");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ps.setInt(2, trade.getTradeID());
            ps.setBytes(3, trade.getWanted().serializeAsBytes());
            ps.setBoolean(4, buyerPickup.isWithdrawn());
            ps.setLong(5, buyerPickup.getTimeWithdrawn());

            itemPickup.computeIfAbsent(player, k -> new ArrayList<>());

            itemPickup.get(player).add(buyerPickup);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // seller
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO pickup (player_uuid, trade_id, pickup_item, collected, time_collected) VALUES (?, ?, ?, ?, ?); ");
            ps.setString(1, String.valueOf(trade.getSeller().getUniqueId()));
            ps.setInt(2, trade.getTradeID());
            ps.setBytes(3, trade.getForSale().serializeAsBytes());
            ps.setBoolean(4, sellerPickup.isWithdrawn());
            ps.setLong(5, sellerPickup.getTimeWithdrawn());

            itemPickup.computeIfAbsent(trade.getSeller(), k -> new ArrayList<>());

            itemPickup.get(player).add(sellerPickup);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //region Shop GUIS
    /**
     * Opens the WorldShop gui for a player.
     * LOCALIZED ITEM IDENTIFIER: statsHead "WorldShopHomeScreen"
     * @param player player you want to open the shop GUI.
     * @param page sets the page of items you want the player to see.
     */
    public void openShop (Player player,int page) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 54, "WorldShop - " + page); //Todo: make the title of the store change based on nation it's in

        // Prev Page Button
        ItemStack prevPage;
        ItemMeta prevPageMeta;

        if (PageUtils.isPageValid(getAllDisplayItems(), page - 1, 45)) {
            prevPage = new ItemStack(Material.ARROW);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "Previous Page");
        } else {
            prevPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "Previous Page");
        }
        prevPageMeta.setLocalizedName(page + "");
        prevPage.setItemMeta(prevPageMeta);
        gui.setItem(45, prevPage);

        // Next Page Button
        ItemStack nextPage;
        ItemMeta nextPageMeta;

        if (PageUtils.isPageValid(getAllDisplayItems(), page + 1, 45)) {
            nextPage = new ItemStack(Material.ARROW);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "Next Page");
        } else {
            nextPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "Next Page");
        }
        nextPage.setItemMeta(nextPageMeta);
        gui.setItem(53, nextPage);

        // View Trades Button
        ItemStack viewTradesButton;
        ItemMeta viewTradesButtonMeta;

        viewTradesButton = new ItemStack(Material.CHEST);
        viewTradesButtonMeta = viewTradesButton.getItemMeta();

        viewTradesButtonMeta.setDisplayName(ChatColor.YELLOW + "Manage Trades");
        viewTradesButton.setItemMeta(viewTradesButtonMeta);
        gui.setItem(51, viewTradesButton);

        // Sell Item Button
        ItemStack sellButton;
        ItemMeta sellButtonMeta;

        sellButton = new ItemStack(Material.WRITABLE_BOOK);
        sellButtonMeta = sellButton.getItemMeta();

        sellButtonMeta.setDisplayName(ChatColor.GREEN + "Sell Item");
        sellButton.setItemMeta(sellButtonMeta);
        gui.setItem(50, sellButton);

        // Player head with stats
        ItemStack statsHead = Utils.createSkull(player);
        ItemMeta statsHeadMeta = statsHead.getItemMeta();

        statsHeadMeta.setDisplayName(ChatColor.DARK_AQUA + player.getDisplayName() + "'s Stats");
        statsHeadMeta.setLocalizedName("WorldShopHomeScreen");
        statsHead.setItemMeta(statsHeadMeta);
        gui.setItem(49, statsHead);

        // Sort Trades Button
        ItemStack sortTrades = new ItemStack(Material.HOPPER);
        ItemMeta sortTradesMeta = sortTrades.getItemMeta();

        sortTradesMeta.setDisplayName(ChatColor.BLUE + "Sort");
        sortTrades.setItemMeta(sortTradesMeta);
        gui.setItem(48, sortTrades);

        // Search Items
        ItemStack searchItems = new ItemStack(Material.SPYGLASS);
        ItemMeta searchItemsMeta = searchItems.getItemMeta();

        searchItemsMeta.setDisplayName(ChatColor.AQUA + "Search");
        searchItems.setItemMeta(searchItemsMeta);
        gui.setItem(47, searchItems);

        // Add stored trades for the first page
        List<ItemStack> items = PageUtils.getPageItems(getAllDisplayItems(player), page, 45);
        for (int i = 0; i < items.size(); i++) {
            // Cannot use gui.addItem(item) here because it combines identical listings
            gui.setItem(i, items.get(i));
        }

        player.openInventory(gui);
    }

    public void nextPage (Player player,int currentPage){
        openShop(player, currentPage + 1);
    }

    public void prevPage (Player player,int currentPage){
        openShop(player, currentPage - 1);
    }

    public void search (Player player){

    }

    public void filter (Player player){

    }

    /**
     * Opens the sell items screen for a player.
     * LOCALIZED ITEM IDENTIFIER: backButton "SellItemScreen"
     * @param player player you want to open the item selling interface.
     */
    public void sellItem (Player player) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 27, "What would you like to sell?");

        // Dividers
        ItemStack divider = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dividerMeta = divider.getItemMeta();
        dividerMeta.setDisplayName("\u200E ");
        dividerMeta.setLocalizedName("Divider");
        divider.setItemMeta(dividerMeta);
        gui.setItem(1, divider);
        gui.setItem(10, divider);
        gui.setItem(19, divider);

        // Confirm the trade button
        ItemStack confirmTradeButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=");
        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
        confirmTradeButtonMeta.setDisplayName("You cannot confirm until you have put in a sell item and a price item!");
        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);
        gui.setItem(0, confirmTradeButton);

        // Item player wants to sell
        ItemStack blankItemSpotButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta blankItemSpotButtonMeta = blankItemSpotButton.getItemMeta();
        blankItemSpotButtonMeta.setDisplayName("Left click the item in your inventory you want to sell!");
        blankItemSpotButton.setItemMeta(blankItemSpotButtonMeta);
        gui.setItem(12, blankItemSpotButton);

        // Increase the number of items the player wants in return
        ItemStack resetPriceButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
        ItemMeta resetPriceButtonMeta = resetPriceButton.getItemMeta();
        resetPriceButtonMeta.setDisplayName("Increase Price by 1");
        resetPriceButton.setItemMeta(resetPriceButtonMeta);
        gui.setItem(14, resetPriceButton);

        // Item player wants to receive
        ItemStack priceButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta priceButtonMeta = priceButton.getItemMeta();
        priceButtonMeta.setDisplayName("Right Click the item in your inventory you want to receive in trade!");
        priceButton.setItemMeta(priceButtonMeta);
        gui.setItem(15, priceButton);

        // Decrease the number of items the player wants in return
        ItemStack setPriceButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
        ItemMeta setPriceButtonMeta = setPriceButton.getItemMeta();
        setPriceButtonMeta.setDisplayName("Decrease Price by 1");
        setPriceButton.setItemMeta(setPriceButtonMeta);
        gui.setItem(16, setPriceButton);

        // Go back to the main menu button
        ItemStack backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("SellItemScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(18, backButton);


        player.openInventory(gui);
    }

    /**
     * Shows the GUI where players can collect from completed trades and manage open trades.
     * LOCALIZED ITEM IDENTIFIER: backButton "ViewCurrentTradesScreen"
     * @param player player you want to open the item selling interface.
     */
    public void manageTrades (Player player){
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 27, "Trades");

        // View Current Listings Button
        ItemStack currentListingsButton;
        ItemMeta currentListingsButtonMeta;

        currentListingsButton = new ItemStack(Material.CHEST);
        currentListingsButtonMeta = currentListingsButton.getItemMeta();
        currentListingsButtonMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GREEN + "Current Listings");
        ArrayList<String> currentListingsButtonLore = new ArrayList<>();
        currentListingsButtonLore.add("Click to view your current listings!");
        currentListingsButtonMeta.setLore(currentListingsButtonLore);
        currentListingsButton.setItemMeta(currentListingsButtonMeta);
        gui.setItem(11, currentListingsButton);


        // View Completed Trades Button
        ItemStack completedTradesButton;
        ItemMeta completedTradesButtonMeta;

        completedTradesButton = new ItemStack(Material.BARREL);
        completedTradesButtonMeta = completedTradesButton.getItemMeta();
        completedTradesButtonMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.YELLOW + "Completed Trades");
        ArrayList<String> completedTradesButtonLore  = new ArrayList<>();
        completedTradesButtonLore.add("Click to view your recently completed trades!");
        completedTradesButtonMeta.setLore(completedTradesButtonLore);
        completedTradesButton.setItemMeta(completedTradesButtonMeta);
        gui.setItem(15, completedTradesButton);


        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewCurrentTradesScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(22, backButton);


        player.openInventory(gui);
    }

    /**
     * Opens the sell items screen for a player.
     * LOCALIZED ITEM IDENTIFIER: backButton "ViewCurrentListingsScreen"
     * @param player player you want to open the item selling interface.
     */
    public void viewCurrentListings (Player player) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 27, "Current Listings");

        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewCurrentListingsScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(0, backButton);

        // Dividers
        ItemStack divider = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dividerMeta = divider.getItemMeta();

        dividerMeta.setDisplayName("\u200E ");
        dividerMeta.setLocalizedName("Divider");
        divider.setItemMeta(dividerMeta);
        gui.setItem(1, divider);
        gui.setItem(10, divider);
        gui.setItem(19, divider);


        ArrayList<Trade> openTrades = new ArrayList<>();
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM trades WHERE seller_uuid = ? AND status = ?;");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, TradeStatus.OPEN.ordinal());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                openTrades.add(getTradeFromTradeID(rs.getInt("trade_id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int count = 0;
        for (Trade t : openTrades) {
            gui.setItem((count % 9) + 2, t.getDisplayItem());
            count++;
        }

        player.openInventory(gui);

    }
    public void removeTradeScreen(Trade trade, Player player) {
        Inventory gui = Bukkit.createInventory(null, 18, "Delete This Trade?");

        // Yes Delete Button
        ItemStack yesButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=");
        ItemMeta yesButtonMeta = yesButton.getItemMeta();
        yesButtonMeta.setDisplayName("Yes");
        yesButtonMeta.setLocalizedName("RemoveTradeScreen");
        yesButton.setItemMeta(yesButtonMeta);
        gui.setItem(11, yesButton);

        // No Don't Delete Button
        ItemStack noButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=");
        ItemMeta noButtonMeta = noButton.getItemMeta();
        noButtonMeta.setDisplayName("No");
        noButton.setItemMeta(noButtonMeta);
        gui.setItem(15, noButton);

        // Trade Display Item
        gui.setItem(4, trade.getDisplayItem());

        player.openInventory(gui);
    }

    /**
     * Opens the sell items screen for a player.
     * LOCALIZED ITEM IDENTIFIER: backButton "ViewCompletedTradesScreen"
     * @param player player you want to open the item selling interface.
     */
    public void viewCompletedTrades (Player player) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 27, "Completed Trades");

        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewCompletedTradesScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(0, backButton);

        // Dividers
        ItemStack divider = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta dividerMeta = divider.getItemMeta();

        dividerMeta.setDisplayName("\u200E ");
        dividerMeta.setLocalizedName("Divider");
        divider.setItemMeta(dividerMeta);
        gui.setItem(1, divider);
        gui.setItem(10, divider);
        gui.setItem(19, divider);

        //Todo: Populate remaining slots w/ completed trades posted by player
        // This may have to be pageable to fit rewards on multiple pages
        // Also there should also probably be an expire time on the rewards
        ArrayList<Pickup> pickups = new ArrayList<>();
        try {

            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM pickup WHERE player_uuid = ? AND collected = ?;");
            ps.setString(1, String.valueOf(player.getUniqueId()));
            ps.setBoolean(2, false);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pickups.add(new Pickup(Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid"))),
                        ItemStack.deserializeBytes(rs.getBytes("pickup_item")),
                        rs.getInt("trade_id"), rs.getBoolean("collected"),
                        rs.getLong("time_collected"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        int count = 0;

        for (Pickup p : pickups) {
            ItemStack item = p.getItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLocalizedName(String.valueOf(p.getTradeID()));
            item.setItemMeta(itemMeta);

            gui.setItem((count % 9) + 2, item);
            count++;
        }

        player.openInventory(gui);
    }

    /**
     * Open the buy screen for a player.
     * LOCALIZED ITEM IDENTIFIER: backButton "BuyItemScreen"
     * @param player player you want to open the gui.
     * @param item the display item they clicked on that corresponds to the trade to pull up.
     */
    public void buyItem (Player player, ItemStack item) {
        Trade t = getTradeFromDisplayItem(item);
        if (t == null) {
            player.sendMessage(ChatColor.RED + "SOMETHING HAS GONE WRONG. Please open a ticket in our Discord. ERROR CODE: WS0003");
            return;
        }

        playersWithStoreOpen.add(player);

        // Build the buy item GUI
        Inventory gui = Bukkit.createInventory(null, 9, "WorldShop - Buy" + t.getDisplayItem().getItemMeta().getDisplayName());

        // Back button
        ItemStack backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("BuyItemScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(0, backButton);

        // Item you're buying
        ItemStack buyItem = t.getForSale();
        ItemMeta buyItemMeta = buyItem.getItemMeta();
        buyItemMeta.setLocalizedName(String.valueOf(t.getTradeID()));
        buyItem.setItemMeta(buyItemMeta);
        gui.setItem(4, buyItem);

        // Confirm button
        ItemStack confirmTradeButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=");
        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
        confirmTradeButtonMeta.setDisplayName("You do not have the required items to buy this!");
        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);
        gui.setItem(5, confirmTradeButton);

        // Item you're paying
        ItemStack payItem = t.getWanted();
        gui.setItem(6, payItem);

        player.openInventory(gui);
    }
    //endregion

    //region Utility methods

    /**
     * Gets all display items from the store manager for display in the main shop page
     * @return returns and arraylist of the display itemstacks
     */
    private List<ItemStack> getAllDisplayItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            items.add(t.getDisplayItem());
        }
        return items;
    }

    /**
     * Gets all display items from the store manager for display in the main shop page
     * @param player Removes any trades with this player as the seller
     * @return returns and arraylist of the display itemstacks
     */
    private List<ItemStack> getAllDisplayItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            if (!t.getSeller().equals(player)) {
                items.add(t.getDisplayItem());
            }
        }
        return items;
    }

    /**
     * Gets the trade from the passed in display item.
     * @param displayItem item to find trade of.
     * @return returns the itemstack that has the same trade. Returns NULL of no matching itemstack is found
     */
    public Trade getTradeFromDisplayItem(ItemStack displayItem) { // Todo: Test if duplicate itemstacks are an issue
        for (Trade t : this.trades) {
            if (t.getDisplayItem().equals(displayItem)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Get the trade from the associated trade ID
     * @param id ID associated with the trade to pass in.
     * @return returns thr trade associated w/ the ID. Returns null if no trade is found
     */
    public Trade getTradeFromTradeID(String id) {
        int tradeID = Integer.parseInt(id);

        for (Trade t : trades) {
            if (t.getTradeID() == tradeID) {
                return t;
            }
        }
        return null;
    }

    /**
     * Get the trade from the associated integer trade ID
     * @param id integer ID associated with the trade.
     * @return returns the trade associated w/ the ID. Returns null of no trade is found
     */
    public Trade getTradeFromTradeID(int id) {
        for (Trade t : trades) {
            if (t.getTradeID() == id) {
                return t;
            }
        }
        return null;
    }

    /**
     * Get the next trade ID.
     * @return the next trade ID
     */
    public int getNextTradeID() {
        mostRecentTradeID = mostRecentTradeID + 1;
        return mostRecentTradeID;
    }
    //endregion
}