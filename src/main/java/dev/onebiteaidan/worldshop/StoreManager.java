package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.*;

public class StoreManager {
    ArrayList<Player> playersWithStoreOpen;

    public StoreManager() {
        playersWithStoreOpen = new ArrayList<>();
    }

    public void createTrade(Trade t) {
        WorldShop.getDatabase().update("INSERT INTO trades (seller_uuid, buyer_uuid, for_sale, in_return, status, time_listed, time_completed) VALUES (?,?,?,?,?,?,?);",
                new Object[]{t.getSeller().getUniqueId().toString(), null, t.getForSale(), t.getInReturn(), t.getStatus().ordinal(), t.getTimeListed(), 0L},
                new int[]{Types.VARCHAR, Types.NULL, Types.BLOB, Types.BLOB, Types.INTEGER, Types.BIGINT, Types.BIGINT}
        );
    }

    public void completeTrade(Player buyer, int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.COMPLETE);
        t.setBuyer(buyer);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup buyerPickup = new Pickup(buyer, t.getForSale(), tradeID, false, 0L);
        Pickup sellerPickup = new Pickup(t.getSeller(), t.getInReturn(), tradeID, false, 0L);

        WorldShop.getDatabase().update("INSERT INTO pickups (player_uuid, pickup_item, trade_id, collected, time_collected) VALUES (?,?,?,?,?);",
                new Object[]{buyerPickup.getPlayer().getUniqueId().toString(), buyerPickup.getItem(), buyerPickup.getTradeID(), buyerPickup.isWithdrawn(), buyerPickup.getTimeWithdrawn()},
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.BOOLEAN, Types.BIGINT}
        );

        WorldShop.getDatabase().update("INSERT INTO pickups (player_uuid, pickup_item, trade_id, collected, time_collected) VALUES (?,?,?,?,?);",
                new Object[]{sellerPickup.getPlayer().getUniqueId().toString(), sellerPickup.getItem(), sellerPickup.getTradeID(), sellerPickup.isWithdrawn(), sellerPickup.getTimeWithdrawn()},
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.BOOLEAN, Types.BIGINT}
        );

        //Todo: Add something here to update all players with the store currently open.
        // Also a one size fits all gui creator would be really sweet.
        updateAllPlayers(buyer);

    }

    /**
     * Update the store pages of all players to prevent accidental duplication
     * @param ignorePlayer The player who just completed a trade and doesn't need to have their stuff updated
     */
    public void updateAllPlayers(Player ignorePlayer) {
        for (Player player : new ArrayList<>(playersWithStoreOpen)) {
            if (player.equals(ignorePlayer)) {
                continue;
            }

            Inventory i =  player.getOpenInventory().getTopInventory();
            switch (player.getOpenInventory().getTopInventory().getSize()) {
                case 54:
                    // Main shop screen
                    System.out.println("HIT");
                    if (i.getItem(49) != null && i.getItem(49).getItemMeta().hasLocalizedName() && i.getItem(49).getItemMeta().getLocalizedName().equals("WorldShopHomeScreen")) {
                        // Get players current shop page
                        int currentShopPage = Integer.parseInt(i.getItem(45).getItemMeta().getLocalizedName());
                        openShop(player, currentShopPage);
                    }

                    break;

                case 36:
                    // Current listings screen
                    if (i.getItem(31) != null && i.getItem(31).getItemMeta().hasLocalizedName() && i.getItem(31).getItemMeta().getLocalizedName().equals("ViewCurrentListingsScreen")) {
                        // Get player's current open listings page
                        int currentListingsPage = Integer.parseInt(i.getItem(29).getItemMeta().getLocalizedName());
                        viewCurrentListings(player, currentListingsPage);
                    }

                    // Completed trades screen
                    if (i.getItem(31) != null && i.getItem(31).getItemMeta().hasLocalizedName() && i.getItem(31).getItemMeta().getLocalizedName().equals("ViewCompletedTradesScreen"))  {
                        // Get player's current completed trades page
                        int currentTradesPage = Integer.parseInt(player.getOpenInventory().getTopInventory().getItem(29).getItemMeta().getLocalizedName());
                        viewCompletedTrades(player, currentTradesPage);
                    }

                    break;

                case 27:
                    // View trade screen
                    if (i.getItem(22) != null && i.getItem(22).getItemMeta().hasLocalizedName() && i.getItem(22).getItemMeta().getLocalizedName().equals("ViewTradeScreen")) {
                        openSorryItUpdatedScreen(player);
                    }

                    break;

                case 18:
                    // Remove trade
                    if (i.getItem(11).getItemMeta().hasLocalizedName() && i.getItem(11).getItemMeta().getLocalizedName().equals("RemoveTradeScreen")) {
                        System.out.println("HIT2");
                        openSorryItUpdatedScreen(player);
                    }

                    break;

                case 9:
                    // Buy item
                    if (i.getItem(0) != null && i.getItem(0).getItemMeta().hasLocalizedName() && i.getItem(0).getItemMeta().getLocalizedName().equals("BuyItemScreen")) {
                        System.out.println("HIT3");
                        openSorryItUpdatedScreen(player);
                    }

                    break;
            }
        }
    }

    public void openSorryItUpdatedScreen(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Oh no...");

        // Home Menu Button
        ItemStack homeMenuButton;
        ItemMeta homeMenuButtonMeta;

        homeMenuButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        homeMenuButtonMeta = homeMenuButton.getItemMeta();

        homeMenuButtonMeta.setDisplayName("Oh No!");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("The item you were viewing is no longer available!");
        lore.add("Click here to go back to the home screen.");
        homeMenuButtonMeta.setLore(lore);
        homeMenuButtonMeta.setLocalizedName("SorryItUpdatedScreen");
        homeMenuButton.setItemMeta(homeMenuButtonMeta);
        gui.setItem(13, homeMenuButton);

        player.openInventory(gui);
    }

    public void expireTrade(int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.EXPIRED);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup forSalePickup = new Pickup(t.getSeller(), t.getForSale(), tradeID, false, 0L);

        WorldShop.getDatabase().update("INSERT INTO pickups (player_uuid, pickup_item, trade_id, collected, time_collected) VALUES (?,?,?,?,?);",
                new Object[]{forSalePickup.getPlayer().getUniqueId().toString(), forSalePickup.getItem(), forSalePickup.getTradeID(), forSalePickup.isWithdrawn(), forSalePickup.getTimeWithdrawn()},
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.BOOLEAN, Types.BIGINT}
        );
    }

    public void deleteTrade(int tradeID) {
        Trade t = getTradeFromTradeID(tradeID);
        t.setStatus(TradeStatus.REMOVED);
        t.setTimeCompleted(System.currentTimeMillis());

        Pickup forSalePickup = new Pickup(t.getSeller(), t.getForSale(), tradeID, false, 0L);

        WorldShop.getDatabase().update("INSERT INTO pickups (player_uuid, pickup_item, trade_id, collected, time_collected) VALUES (?,?,?,?,?);",
                new Object[]{forSalePickup.getPlayer().getUniqueId().toString(), forSalePickup.getItem(), forSalePickup.getTradeID(), forSalePickup.isWithdrawn(), forSalePickup.getTimeWithdrawn()},
                new int[]{Types.VARCHAR, Types.BLOB, Types.INTEGER, Types.BOOLEAN, Types.BIGINT}
        );
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
    public void viewCurrentListings (Player player, int page) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 36, "Current Listings");

        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewCurrentListingsScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(31, backButton);

        // Prev Page Button
        ItemStack prevPage;
        ItemMeta prevPageMeta;

        if (PageUtils.isPageValid(getAllCurrentTradesDisplayItems(player), page - 1, 27)) {
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
        gui.setItem(29, prevPage);

        // Next Page Button
        ItemStack nextPage;
        ItemMeta nextPageMeta;

        if (PageUtils.isPageValid(getAllCurrentTradesDisplayItems(player), page + 1, 27)) {
            nextPage = new ItemStack(Material.ARROW);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "Next Page");
        } else {
            nextPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "Next Page");
        }
        nextPage.setItemMeta(nextPageMeta);
        gui.setItem(33, nextPage);

        for (ItemStack item: PageUtils.getPageItems(getAllCurrentTradesDisplayItems(player), page, 27)) {
            gui.addItem(item);
        }

        player.openInventory(gui);
    }

    public void nextCurrentListingsPage(Player player, int currentPage) {
        viewCurrentListings(player, currentPage + 1);
    }

    public void prevCurrentListingsPage(Player player, int currentPage) {
        viewCurrentListings(player, currentPage - 1);
    }

    public void viewTrade(Trade trade, Player player) {
        playersWithStoreOpen.add(player);
        Inventory gui = Bukkit.createInventory(null, 27, "Trade Viewer");

        // Item Being Sold
        ItemStack beingSold = trade.getForSale();
        gui.setItem(2, beingSold);

        // Item Being Sold Marker
        ItemStack beingSoldMarker = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIyMWRhNDQxOGJkM2JmYjQyZWI2NGQyYWI0MjljNjFkZWNiOGY0YmY3ZDRjZmI3N2ExNjJiZTNkY2IwYjkyNyJ9fX0=");
        ItemMeta beingSoldMarkerMeta = beingSoldMarker.getItemMeta();
        beingSoldMarkerMeta.setDisplayName("You are selling this item!");
        ArrayList<String> beingSoldLore = new ArrayList<>();
        beingSoldLore.add("This will go to the player who buys the item from you.");
        beingSoldLore.add("In return, you will receive the payment item(s) you specified.");
        beingSoldMarkerMeta.setLore(beingSoldLore);
        beingSoldMarker.setItemMeta(beingSoldMarkerMeta);
        gui.setItem(11, beingSoldMarker);

        // Payment Item
        ItemStack paymentItem = trade.getInReturn();
        gui.setItem(6, paymentItem);

        // Payment Item Marker
        ItemStack paymentItemMarker = Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0ZjJmOTY5OGMzZjE4NmZlNDRjYzYzZDJmM2M0ZjlhMjQxMjIzYWNmMDU4MTc3NWQ5Y2VjZDcwNzUifX19");
        ItemMeta paymentItemMarkerMeta = beingSoldMarker.getItemMeta();
        beingSoldMarkerMeta.setDisplayName("This is the item you requested as payment!");
        ArrayList<String> paymentItemLore = new ArrayList<>();
        paymentItemLore.add("This will go to you after another player buys from you.");
        paymentItemLore.add("In return, the buyer will receive the item you are selling.");
        beingSoldMarkerMeta.setLore(paymentItemLore);
        beingSoldMarker.setItemMeta(beingSoldMarkerMeta);
        gui.setItem(15, beingSoldMarker);

        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewTradeScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(22, backButton);


        player.openInventory(gui);
    }

    public void removeTradeScreen(Trade trade, Player player) {
        playersWithStoreOpen.add(player);
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
        gui.setItem(4, trade.generateDisplayItem());

        player.openInventory(gui);
    }

    /**
     * Opens the sell items screen for a player.
     * LOCALIZED ITEM IDENTIFIER: backButton "ViewCompletedTradesScreen"
     * @param player player you want to open the item selling interface.
     */
    public void viewCompletedTrades (Player player, int page) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 36, "Completed Trades");

        // Back Button
        ItemStack backButton;
        ItemMeta backButtonMeta;

        backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("ViewCompletedTradesScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(31, backButton);

        // Prev Page Button
        ItemStack prevPage;
        ItemMeta prevPageMeta;

        if (PageUtils.isPageValid(getAllCompletedTradesItems(player), page - 1, 27)) {
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
        gui.setItem(29, prevPage);

        // Next Page Button
        ItemStack nextPage;
        ItemMeta nextPageMeta;

        if (PageUtils.isPageValid(getAllCompletedTradesItems(player), page + 1, 27)) {
            nextPage = new ItemStack(Material.ARROW);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "Next Page");
        } else {
            nextPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "Next Page");
        }
        nextPage.setItemMeta(nextPageMeta);
        gui.setItem(33, nextPage);

        //Todo: Populate remaining slots w/ completed trades posted by player
        // This may have to be pageable to fit rewards on multiple pages
        // Also there should also probably be an expire time on the rewards

        for (ItemStack item: PageUtils.getPageItems(getAllCompletedTradesItems(player), page, 27)) {
            gui.addItem(item);
        }

        player.openInventory(gui);
    }

    public void nextCompletedTradesPage(Player player, int currentPage) {
        viewCompletedTrades(player, currentPage + 1);
    }

    public void prevCompletedTradesPage(Player player, int currentPage) {
        viewCompletedTrades(player, currentPage - 1);
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
        Inventory gui = Bukkit.createInventory(null, 9, "WorldShop - Buy" + t.generateDisplayItem().getItemMeta().getDisplayName());

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
        ItemStack payItem = t.getInReturn();
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
        Connection connection = WorldShop.getDatabase().getConnection();
        List<ItemStack> items = new ArrayList<>();

        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE status = ?;",
                new Object[]{TradeStatus.OPEN.ordinal()},
                new int[]{Types.INTEGER}, connection);

        try {
            while (rs.next()) {
                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                items.add(new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                ).generateDisplayItem());
            }

            rs.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Gets all display items from the store manager for display in the main shop page
     * @param player Removes any trades with this player as the seller
     * @return returns and arraylist of the display itemstacks
     */
    private List<ItemStack> getAllDisplayItems(Player player) {
        Connection connection = WorldShop.getDatabase().getConnection();
        List<ItemStack> items = new ArrayList<>();

        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE status = ? AND seller_uuid <> ?;",
                new Object[]{TradeStatus.OPEN.ordinal(), player.getUniqueId().toString()},
                new int[]{Types.INTEGER, Types.VARCHAR}, connection);

        try {
            while (rs.next()) {
                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                items.add(new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                ).generateDisplayItem());
            }

            rs.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Gets the trade from the passed in display item.
     * @param displayItem item to find trade of.
     * @return returns the itemstack that has the same trade. Returns NULL of no matching itemstack is found
     */
    public Trade getTradeFromDisplayItem(ItemStack displayItem) {
        if (displayItem.getItemMeta().hasLocalizedName()) {
            Connection connection = WorldShop.getDatabase().getConnection();
            String id = displayItem.getItemMeta().getLocalizedName();

            ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE trade_id = ?;",
                    new Object[]{Integer.parseInt(id)},
                    new int[]{Types.INTEGER}, connection);

            try {
                rs.next();

                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                Trade t = new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                );

                rs.close();
                connection.close();

                return t;

            } catch (SQLException e) {
                e.printStackTrace();
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
        Connection connection = WorldShop.getDatabase().getConnection();
        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE trade_id = ?;",
                new Object[]{Integer.parseInt(id)},
                new int[]{Types.INTEGER}, connection);

        try {
            rs.next();

            // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
            OfflinePlayer buyer = null;
            String buyerUUID = rs.getString("buyer_uuid");
            if (!rs.wasNull()) {
                buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
            }

            Trade t = new Trade(rs.getInt("trade_id"),
                    TradeStatus.values()[rs.getInt("status")],
                    Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                    buyer,
                    ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                    ItemStack.deserializeBytes(rs.getBytes("in_return")),
                    rs.getLong("time_listed"),
                    rs.getLong("time_completed")
            );

            rs.close();
            connection.close();

            return t;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the trade from the associated integer trade ID
     * @param id integer ID associated with the trade.
     * @return returns the trade associated w/ the ID. Returns null of no trade is found
     */
    public Trade getTradeFromTradeID(int id) {
        Connection connection = WorldShop.getDatabase().getConnection();
        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE trade_id = ?;",
                new Object[]{id},
                new int[]{Types.INTEGER}, connection);

        try {
            rs.next();

            // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
            OfflinePlayer buyer = null;
            String buyerUUID = rs.getString("buyer_uuid");
            if (!rs.wasNull()) {
                buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
            }

            Trade t = new Trade(rs.getInt("trade_id"),
                    TradeStatus.values()[rs.getInt("status")],
                    Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                    buyer,
                    ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                    ItemStack.deserializeBytes(rs.getBytes("in_return")),
                    rs.getLong("time_listed"),
                    rs.getLong("time_completed")
            );

            rs.close();
            connection.close();

            return t;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<ItemStack> getAllCurrentTradesDisplayItems(Player player) {
        Connection connection = WorldShop.getDatabase().getConnection();
        List<ItemStack> items = new ArrayList<>();

        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM trades WHERE status = ? AND seller_uuid = ?;",
                new Object[]{TradeStatus.OPEN.ordinal(), player.getUniqueId().toString()},
                new int[]{Types.INTEGER, Types.VARCHAR}, connection);

        try {
            while (rs.next()) {
                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                items.add(new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                ).generateCurrentTradeDisplayItem());
            }

            rs.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<ItemStack> getAllCompletedTradesItems(Player player) {
        Connection connection = WorldShop.getDatabase().getConnection();
        ArrayList<ItemStack> pickups = new ArrayList<>();

        ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM pickups WHERE player_uuid = ? AND collected = ?;",
                new Object[]{player.getUniqueId().toString(), false},
                new int[]{Types.VARCHAR, Types.BOOLEAN}, connection);

        try {
            while (rs.next()) {

                Pickup p = new Pickup(Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid"))),
                        ItemStack.deserializeBytes(rs.getBytes("pickup_item")),
                        rs.getInt("trade_id"), rs.getBoolean("collected"),
                        rs.getLong("time_collected")
                );

                ItemStack item = p.getItem();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setLocalizedName(String.valueOf(p.getTradeID()));
                item.setItemMeta(itemMeta);

                pickups.add(item);
            }

            rs.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pickups;
    }

    // endregion
}