package dev.onebiteaidan.worldshop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;

public class StoreListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getCurrentItem() != null && e.getView().getTitle().contains("WorldShop")) {
            int page = Integer.parseInt(e.getInventory().getItem(46).getItemMeta().getLocalizedName());
            if (e.getRawSlot() == 46 && e.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)) {

            } else if (e.getRawSlot() == 53 && e.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)) {

            }
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
