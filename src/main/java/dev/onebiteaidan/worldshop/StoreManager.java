package dev.onebiteaidan.worldshop;

import com.google.common.base.Joiner;
import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoreManager {

    private static class Trade {
        ItemStack forSale;
        ItemStack wanted;
        ItemStack displayItem;
        int amountWanted;

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

            this.buyer = null;
            this.tradeID = tradeID;
            long timeListed = System.currentTimeMillis();

            ItemStack displayItem = new ItemStack(forSale.getType(), forSale.getAmount());
            ItemMeta displayItemMeta = displayItem.getItemMeta();

            // Items that have displaynames are items that have been renamed to something other than their original title.
            // Therefore we have to implement this shit system
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
        private Trade(ItemStack forSale, ItemStack wanted, ItemStack displayItem, int amountWanted, Player seller, Player buyer, int tradeID, long timeListed) {
            this.forSale = forSale;
            this.wanted = wanted;
            this.displayItem = displayItem;
            this.amountWanted = amountWanted;
            this.seller = seller;

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

        trades = new ArrayList<>();
        playersWithStoreOpen = new ArrayList<>();
    }

    public int getNextTradeID() {
        return mostRecentTradeID++;
    }

    public void addToStore(Trade trade) {
        trades.add(trade);
    }

    public void addToStore(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller) {
        trades.add(new Trade(forSale, wanted, amountWanted, seller, getNextTradeID()));
    }

    public void removeFromStore(Trade trade) {

        trades.remove(trade);

    }



    public void buy (Player player, Trade trade) {

        playersWithStoreOpen = new ArrayList<>();
        trades = new ArrayList<>();
    }

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

    }

    public void sellItem (Player player){
        // Todo: Build a gui for inputting item + amount wanted for trade
        // Build a gui that has two parts. A part for entering the name via a sign
        // A part where you can set the number of items using like (-5, -1, +1, +5)

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
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(18, backButton);


        player.openInventory(gui);
    }

    public void buyItem (Player player, ItemStack item) {
        Trade t = getTradeFromDisplayItem(item);
        if (t == null) {
            player.sendMessage(ChatColor.RED + "SOMETHING HAS GONE WRONG. Please open a ticket in our Discord.");
            return;
        }

        playersWithStoreOpen.add(player);

        // Build the buy item GUI
        Inventory gui = Bukkit.createInventory(null, 9, t.displayItem.getItemMeta().getDisplayName());

        // Back button
        ItemStack backButton = new ItemStack(Material.RED_CONCRETE_POWDER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButtonMeta.setLocalizedName("BuyItemScreen");
        backButton.setItemMeta(backButtonMeta);
        gui.setItem(0, backButton);

        // Item you're buying
        ItemStack buyItem = t.forSale;
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
}