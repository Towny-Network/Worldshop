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

            e.setCancelled(true); //Todo: this needs error checking to make sure it's not just a check with the name "WorlsShop"

            int page = Integer.parseInt(e.getInventory().getItem(46).getItemMeta().getLocalizedName());

            if (e.getRawSlot() == 45) {
                if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                    WorldShop.getStoreManager().prevPage((Player) e.getWhoClicked(), page - 1);
                } else {
                    return;
                }
            } else if (e.getRawSlot() == 53) {
                if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                    WorldShop.getStoreManager().nextPage((Player) e.getWhoClicked(),  + 1);
                } else {
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ArrayList<Player> playersInStore = WorldShop.getStoreManager().playersWithStoreOpen;
        playersInStore.remove(e.getPlayer());
    }
}
