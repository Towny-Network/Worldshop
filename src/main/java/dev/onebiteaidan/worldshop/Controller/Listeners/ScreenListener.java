package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class ScreenListener implements Listener {

    @EventHandler
    public abstract void onClick(InventoryClickEvent e);

    /**
     * Default behavior is to add player to the update list after opening a Screen.
     * Certain screens don't need to be updated and should override this method.
     * @param event called by an opened inventory
     */
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Screen) {
            WorldShop.getStoreManager().addToUpdateList((Player) event.getPlayer());
        }
    }

    /**
     * Default behavior is to remove player from the update list after closing a Screen.
     * Certain screens don't need to be updated and should override this method.
     * @param event called by a closed inventory
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Screen) {
            WorldShop.getStoreManager().removeFromUpdateList((Player) event.getPlayer());
        }
    }
}
