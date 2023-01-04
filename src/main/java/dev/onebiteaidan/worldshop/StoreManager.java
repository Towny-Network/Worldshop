package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.Utils.PageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StoreManager implements Listener {

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

    public StoreManager() {
        // todo: Grabs all of the trades from the database

    }



    public void openShop(Player player, int page) {
        Inventory gui = Bukkit.createInventory(null, 54, "Aidan's STORE"); //Todo: make the title of the store change based on nation it's in

        ItemStack prevPage;
        ItemMeta prevPageMeta;

        if (PageUtils.isPageValid(getAllItems(), page - 1, 45)) {
            prevPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page - 1));
        } else {
            prevPage = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName(ChatColor.RED + "Go To Page " + (page + 1));
        }

        ItemStack nextPage;
        ItemMeta nextPageMeta;



    }


    public void sell(Player player) {
        // Todo: Build a gui for inputting item + amount wanted for trade
        // Build a gui that has two parts. A part for entering the name via a sign
        // A part where you can set the number of items using like (-5, -1, +1, +5)

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

    private List<ItemStack> getAllItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Trade t : trades) {
            items.add(t.forSale);
        }
        return items;
    }
}
