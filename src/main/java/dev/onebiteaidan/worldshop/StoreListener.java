package dev.onebiteaidan.worldshop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.checkerframework.checker.calledmethods.qual.EnsuresCalledMethodsVarArgs;

import java.util.ArrayList;

public class StoreListener implements Listener {

    @EventHandler
    public void onWorldShopScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("WorldShop")) {

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "WorldsShop"

            int currentPage = Integer.parseInt(e.getInventory().getItem(45).getItemMeta().getLocalizedName());

            switch(e.getRawSlot()) {
                case 45: // Previous Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().prevPage((Player) e.getWhoClicked(), currentPage);
                    }
                    break;

                case 47: // Search
                    break;

                case 48: // Filter
                    break;

                case 49: // Stats Head Display
                    break;

                case 50: // Sell Item
                    WorldShop.getStoreManager().sellItem((Player) e.getWhoClicked());
                    break;

                case 51: // View Trades
                    break;

                case 53: // Next Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().nextPage((Player) e.getWhoClicked(), currentPage);
                    }
                    break;

                default:
                    // Open buy screen for the item that was clicked on


            }
        }
    }

    @EventHandler
    public void onSellScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("What would you like to sell?")) {

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "What would you like to sell?"

            //Todo: Flush out how the player will put the items into the respective itemToSell and itemInReturn slots.
            // Current ideas include:
            // -Using left and right clicks to determine which one (the only downside to this is that the player cannot manage their local inventory
            //  while in the shop screen. What if they want to sell only half a stack. Or only 1 item out of a whole stack.)
            // -Drag and drop itemstacks into the spot they want to put them into.
            // -Click on whatever space they want to put it in (sell or inReturn) to enter a mode then have them double click the itemstack they want put in.


            switch(e.getRawSlot()) {
                case 11: // Is this necessary for our implementation
                    break;

                case 13: // Approve trade. Todo:This has to include error checking and also a double confirm
                    break;

                case 15:// Is this necessary for our implementation
                    break;

                case 18: // Back button
                    // Brings the player back to the main page of the store.
                    WorldShop.getStoreManager().openShop((Player) e.getWhoClicked(), 1);
                    break;

                case 22: // Add 5 to price
                    break;

                case 23: // Add 1 to price
                    break;

                case 24: // Reset price to 1
                    break;

                case 25: // Remove 1 from price
                    break;

                case 26: // Remove 5 from price
                    break;

                default: // Not sure what will go here or if needed

            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
