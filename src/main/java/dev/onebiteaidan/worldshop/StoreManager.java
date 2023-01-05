package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Utils.PageUtils;
import jdk.vm.ci.aarch64.AArch64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    private static class Trade {
        ItemStack forSale;
        ItemStack wanted;
        ItemStack displayItem;

        private Trade(ItemStack forSale, ItemStack wanted) {
            this.forSale = forSale;
            this.wanted = wanted;
        }
    }

    ArrayList<Trade> trades;
    ArrayList<Player> playersWithStoreOpen;


    public StoreManager() {
        // todo: Grabs all of the trades from the database
        playersWithStoreOpen = new ArrayList<>();
    }



    public void openShop(Player player, int page) {
        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 54, "WorldShop - " + page); //Todo: make the title of the store change based on nation it's in

        // Prev Page Button
        ItemStack prevPage;
        ItemMeta prevPageMeta;

        if (PageUtils.isPageValid(getAllDisplayItems(), page - 1, 45)) {
            prevPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page - 1));
        } else {
            prevPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page + 1));
        }
        prevPageMeta.setLocalizedName(page + "");
        prevPage.setItemMeta(prevPageMeta);
        gui.setItem(46, prevPage);

        // Next Page Button
        ItemStack nextPage;
        ItemMeta nextPageMeta;

        if (PageUtils.isPageValid(getAllDisplayItems(), page + 1, 45)) {
            nextPage = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page + 1));
        } else {
            nextPage = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page - 1));
        }
        nextPage.setItemMeta(nextPageMeta);
        gui.setItem(53, nextPage);

        for (ItemStack item : PageUtils.getPageItems(getAllDisplayItems(), page, 45)) {
            gui.addItem(item);
        }

        player.openInventory(gui);

    }


    public void sell(Player player) {
        // Todo: Build a gui for inputting item + amount wanted for trade
        // Build a gui that has two parts. A part for entering the name via a sign
        // A part where you can set the number of items using like (-5, -1, +1, +5)

        playersWithStoreOpen.add(player);

        Inventory gui = Bukkit.createInventory(null, 45, "What would you like to sell?");

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backButtonMeta = backButton.getItemMeta();
        backButtonMeta.setDisplayName("Back");
        backButton.setItemMeta(backButtonMeta);

        ItemStack blankItemSpot = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta blankItemSpotMeta = blankItemSpot.getItemMeta();
        blankItemSpotMeta.setDisplayName("Click the item in your inventory you want to sell!");
        blankItemSpot.setItemMeta(blankItemSpotMeta);

        


    }

    private List<ItemStack> getAllDisplayItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            items.add(t.forSale);
        }
        return items;
    }
}
