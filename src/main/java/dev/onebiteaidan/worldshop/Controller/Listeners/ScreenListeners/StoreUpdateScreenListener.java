package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.View.Screens.StoreUpdateScreen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StoreUpdateScreenListener extends ScreenListener {

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof StoreUpdateScreen) {
            StoreUpdateScreen holder = (StoreUpdateScreen) e.getInventory().getHolder();

            new MainShopScreen(holder.getPlayer()).openScreen(1);
        }
    }
}
