package dev.onebiteaidan.worldshop;

import com.google.common.base.Joiner;
import dev.onebiteaidan.worldshop.DataManagement.SQLite;
import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import io.netty.resolver.dns.BiDnsQueryLifecycleObserver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.sql.*;
import java.util.*;

public class StoreManager {

    private static class Trade {
        ItemStack forSale;
        ItemStack wanted;
        ItemStack displayItem;
        int amountWanted;

        boolean completed;
        Player seller;
        Player buyer;
        int tradeID;
        long timeListed;// Unix time


        /**
         * Constructor for when a player creates a new trade via the gui.
         * @param forSale Item player is trading away
         * @param wanted Item player is trading for
         * @param amountWanted Amount they want
         * @param seller Person selling the forSale item
         */
        private Trade(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller, int tradeID) {
            this.forSale = forSale;
            this.wanted = wanted;
            this.amountWanted = amountWanted;
            this.seller = seller;

            this.completed = false;
            this.buyer = null;
            this.tradeID = tradeID;
            this.timeListed = System.currentTimeMillis();

            ItemStack displayItem = new ItemStack(forSale.getType(), forSale.getAmount());
            ItemMeta displayItemMeta = displayItem.getItemMeta();

            // Items that have displaynames are items that have been renamed to something other than their original title.
            // Therefore, we have to implement this shit system
            if (forSale.getItemMeta().hasDisplayName()) {
                displayItemMeta.setDisplayName(ChatColor.YELLOW + forSale.getItemMeta().getDisplayName() + " x" + this.forSale.getAmount());
            } else {
                String s = forSale.getType().toString();
                String[] parts = s.split("_");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].toLowerCase();
                    parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
                }

                displayItemMeta.setDisplayName(ChatColor.YELLOW + String.join(" ", parts) + " x" + this.forSale.getAmount());
            }

            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Being Sold By: " + this.seller.getDisplayName());

            if (forSale.getItemMeta().hasLore()) {
                lore.add(ChatColor.DARK_GRAY + "===================");
                lore.add("");
                for (String s : forSale.getItemMeta().getLore()) {
                    lore.add(ChatColor.DARK_PURPLE + s);
                }
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "===================");
            }

            lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click " + ChatColor.RESET + "" + ChatColor.GOLD + "to buy this item");

            displayItemMeta.setLore(lore);
            // Adding in trade ID for indentification later on
            displayItemMeta.setLocalizedName(String.valueOf(tradeID));
            displayItem.setItemMeta(displayItemMeta);
            this.displayItem = displayItem;
        }

        /**
         * Constructor for rebuilding from the Database.
         * @param forSale Item player is trading away
         * @param wanted Item player is trading for
         * @param amountWanted Amount they want
         * @param displayItem The item that goes on display in the shop homepage
         * @param seller Person selling the forSale item
         * @param buyer Person who bought the item if trade is complete (null otherwise)
         * @param tradeID ID of the trade;
         * @param timeListed Time that the trade was listed (in Unix time)
         */
        private Trade(ItemStack forSale, ItemStack wanted, ItemStack displayItem, int amountWanted, Player seller, Player buyer, int tradeID, long timeListed, boolean completed) {
            this.forSale = forSale;
            this.wanted = wanted;
            this.displayItem = displayItem;
            this.amountWanted = amountWanted;
            this.seller = seller;

            this.completed = completed;
            this.tradeID = tradeID;
            this.buyer = buyer;
            this.timeListed = timeListed;

//            ItemStack temp = new ItemStack(forSale);
//            ItemMeta tempMeta = temp.getItemMeta();
//            tempMeta.setDisplayName(forSale.getItemMeta().getDisplayName());
//
//            ArrayList<String> lore = new ArrayList<>();
//            if (forSale.getItemMeta().hasLore()) {
//                lore.addAll(forSale.getItemMeta().getLore());
//            }
//            lore.add("");
//            lore.add("Price:");
//            lore.add(amountWanted + "x " + wanted.getItemMeta().getDisplayName());
//            lore.add("Sold By:");
//            lore.add(seller.getName());
//            lore.add(String.valueOf(tradeID));
//
//            tempMeta.setLore(lore);
//            temp.setItemMeta(tempMeta);
//
//            this.displayItem = temp;


        }
    }

    ArrayList<Trade> trades;
    ArrayList<Player> playersWithStoreOpen;
    int mostRecentTradeID;


    public StoreManager() {
        // todo: Grabs all of the trades from the database
        // all trades greater than 30 days old should be removed from the db

        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("SELECT * FROM trades WHERE completed = ?;");
            ps.setBoolean(1, false);
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
                        rs.getBoolean("completed")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (trades.size() != 0) {
            mostRecentTradeID = trades.get(trades.size() - 1).tradeID;
        } else {
            mostRecentTradeID = 0;
        }

        playersWithStoreOpen = new ArrayList<>();
    }

    public int getNextTradeID() {
        return ++mostRecentTradeID;
    }

    public void addToStore(Trade trade) {
        trades.add(trade);

        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO trades (trade_id, seller_uuid, display_item, for_sale, wanted, num_wanted, completed, buyer_uuid, time_listed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setInt(1, trade.tradeID);
            ps.setString(2, String.valueOf(trade.seller.getUniqueId()));
            ps.setBytes(3, trade.displayItem.serializeAsBytes());
            ps.setBytes(4, trade.forSale.serializeAsBytes());
            ps.setBytes(5, trade.wanted.serializeAsBytes());
            ps.setInt(6, trade.amountWanted);
            ps.setBoolean(7, trade.completed);

            if ((trade.buyer != null)) {
                ps.setString(8, String.valueOf(trade.seller.getUniqueId()));
            } else {
                ps.setNull(8, Types.NULL);
            }

            ps.setLong(9, trade.timeListed);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToStore(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller) {
        Trade trade = new Trade(forSale, wanted, amountWanted, seller, getNextTradeID());
        trades.add(trade);

        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("INSERT INTO trades (trade_id, seller_uuid, display_item, for_sale, wanted, num_wanted, completed, buyer_uuid, time_listed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");

            ps.setInt(1, trade.tradeID);
            ps.setString(2, String.valueOf(trade.seller.getUniqueId()));
            ps.setBytes(3, trade.displayItem.serializeAsBytes());
            ps.setBytes(4, trade.forSale.serializeAsBytes());
            ps.setBytes(5, trade.wanted.serializeAsBytes());
            ps.setInt(6, trade.amountWanted);
            ps.setBoolean(7, trade.completed);

            if ((trade.buyer != null)) {
                ps.setString(8, String.valueOf(trade.seller.getUniqueId()));
            } else {
                ps.setNull(8, Types.BLOB);
            }

            ps.setLong(9, trade.timeListed);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromStore(Trade trade) {
        try {
            PreparedStatement ps = WorldShop.getDatabase().getConnection().prepareStatement("UPDATE trades SET completed = ? WHERE trade_id = ?");
            ps.setBoolean(1, true);
            ps.setInt(2, trade.tradeID);

            ps.executeUpdate();

            trades.remove(trade);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void buy (Trade trade, Player player) {
        removeFromStore(trade);
        trade.buyer = player;
        trade.completed = true;

        openShop(player, 1);

        // Todo: figure out a good way to do storages for payment of players
        // The best way to do this would to probably store it in the player database
    }

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

        viewTradesButtonMeta.setDisplayName(ChatColor.YELLOW + "View Trades");
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
        List<ItemStack> items = PageUtils.getPageItems(getAllDisplayItems(), page, 45);
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

    public void viewCurrentTrades (Player player){
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 27, "Trades");

        // View Current Listings Button
        ItemStack currentListingsButton;
        ItemMeta currentListingsButtonMeta;




        // View Completed Trades Button
        ItemStack completedTradesButton;
        ItemMeta completedTradesButtonMeta;

        completedTradesButton = new ItemStack(Material.BARREL);
        completedTradesButtonMeta = completedTradesButton.getItemMeta();



    }

    public void viewCurrentListings (Player player) {

    }

    public void viewCompletedTrades (Player player) {

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
        Inventory gui = Bukkit.createInventory(null, 9, "WorldShop - Buy" + t.displayItem.getItemMeta().getDisplayName());

        // Back button
        ItemStack backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("BuyItemScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(0, backButton);

        // Item you're buying
        ItemStack buyItem = t.forSale;
        ItemMeta buyItemMeta = buyItem.getItemMeta();
        buyItemMeta.setLocalizedName(String.valueOf(t.tradeID));
        buyItem.setItemMeta(buyItemMeta);
        gui.setItem(4, buyItem);

        // Confirm button
        ItemStack confirmTradeButton = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=");
        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
        confirmTradeButtonMeta.setDisplayName("You do not have the required items to buy this!");
        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);
        gui.setItem(5, confirmTradeButton);

        // Item you're paying
        ItemStack payItem = t.wanted;
        gui.setItem(6, payItem);


        player.openInventory(gui);
    }



    private List<ItemStack> getAllDisplayItems () {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            items.add(t.displayItem);
        }
        return items;
    }

    private Trade getTradeFromDisplayItem(ItemStack displayItem) {
        for (Trade t : this.trades) {
            if (t.displayItem.equals(displayItem)) {
                return t;
            }
        }
        return null;
    }

    public Trade getTradeFromTradeID(String id) {
        int tradeID = Integer.parseInt(id);

        for (Trade t : trades) {
            if (t.tradeID == tradeID) {
                return t;
            }
        }
        return null;
    }
}