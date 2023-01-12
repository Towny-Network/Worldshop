package dev.onebiteaidan.worldshop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.checkerframework.checker.calledmethods.qual.EnsuresCalledMethodsVarArgs;

import java.util.ArrayList;

public class StoreListener implements Listener {

    @EventHandler
    public void onWorldShopScreenClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("WorldShop")) {

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a chest with the name "WorldsShop"

            int page = Integer.parseInt(e.getInventory().getItem(46).getItemMeta().getLocalizedName());

            switch(e.getRawSlot()) {
                case 45: // Previous Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().prevPage((Player) e.getWhoClicked(), page - 1);
                    }
                    break;

                case 47: // Search
                    break;

                case 48: // Filter
                    break;

                case 49: // Stats Head Display
                    break;

                case 50: // Sell Item
                    break;

                case 51: // View Trades
                    break;

                case 53: // Next Page
                    if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                        WorldShop.getStoreManager().nextPage((Player) e.getWhoClicked(), +1);
                    }
                    break;

                default:
                    // Open buy screen for the item that was clicked on


            }
        }
    }

    @EventHandler
    public void onSellScreenClick(InventoryClickEvent e) {

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
