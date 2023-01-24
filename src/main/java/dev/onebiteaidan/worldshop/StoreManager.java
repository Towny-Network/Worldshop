package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Utils.PageUtils;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    private static class Trade {
        ItemStack forSale;
        ItemStack wanted;
        int amountWanted;
        ItemStack displayItem;
        Player seller;


        private Trade(ItemStack forSale, ItemStack wanted, int amountWanted, Player seller) {
            this.forSale = forSale;
            this.wanted = wanted;
            this.amountWanted = amountWanted;
            this.seller = seller;

            ItemStack temp = new ItemStack(forSale);
            ItemMeta tempMeta = temp.getItemMeta();
            tempMeta.setDisplayName(forSale.getItemMeta().getDisplayName());

            ArrayList<String> lore = new ArrayList<>();
            if (forSale.getItemMeta().hasLore()) {
                lore.addAll(forSale.getItemMeta().getLore());
            }
            lore.add("");
            lore.add("Price:");
            lore.add(amountWanted + "x " + wanted.getItemMeta().getDisplayName());
            lore.add("Sold By:");
            lore.add(seller.getName());

            tempMeta.setLore(lore);
            temp.setItemMeta(tempMeta);

            this.displayItem = temp;
        }
    }

    ArrayList<Trade> trades;
    ArrayList<Player> playersWithStoreOpen;


    public StoreManager() {
        // todo: Grabs all of the trades from the database
        trades = new ArrayList<>();
        playersWithStoreOpen = new ArrayList<>();
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
        for (ItemStack item : PageUtils.getPageItems(getAllDisplayItems(), page, 45)) {
            gui.addItem(item);
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

        // Confirm the trade button
        ItemStack confirmTradeButton = new ItemStack(Material.YELLOW_CONCRETE_POWDER);
        ItemMeta confirmTradeButtonMeta = confirmTradeButton.getItemMeta();
        confirmTradeButtonMeta.setDisplayName("Confirm");
        confirmTradeButton.setItemMeta(confirmTradeButtonMeta);
        gui.setItem(0, confirmTradeButton);

        // Item player wants to sell
        ItemStack blankItemSpotButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta blankItemSpotButtonMeta = blankItemSpotButton.getItemMeta();
        blankItemSpotButtonMeta.setDisplayName("Left click the item in your inventory you want to sell!");
        blankItemSpotButton.setItemMeta(blankItemSpotButtonMeta);
        gui.setItem(11, blankItemSpotButton);

        // Reset the number of items the player wants in return back to 1
        ItemStack resetPriceButton = new ItemStack(Material.YELLOW_CONCRETE);
        ItemMeta resetPriceButtonMeta = resetPriceButton.getItemMeta();
        resetPriceButtonMeta.setDisplayName("Reset price");
        resetPriceButton.setItemMeta(resetPriceButtonMeta);
        gui.setItem(14, resetPriceButton);

        // Item player wants to receive
        ItemStack priceButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta priceButtonMeta = priceButton.getItemMeta();
        priceButtonMeta.setDisplayName("Right Click the item in your inventory you want to receive in trade!");
        gui.setItem(15, priceButton);

        // Set the number of items the player wants in return
        ItemStack setPriceButton = new ItemStack(Material.OAK_SIGN);
        ItemMeta setPriceButtonMeta = setPriceButton.getItemMeta();
        setPriceButtonMeta.setDisplayName("Set price");
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

    public void buyItem (Player player) {

    }

    private List<ItemStack> getAllDisplayItems () {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            items.add(t.forSale);
        }
        return items;
    }
}