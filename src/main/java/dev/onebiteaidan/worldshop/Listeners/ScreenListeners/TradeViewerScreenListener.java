package dev.onebiteaidan.worldshop.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.GUI.Screens.TradeViewerScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeViewerScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof TradeViewerScreen) {
            TradeViewerScreen holder = (TradeViewerScreen) e.getInventory().getHolder();

            e.setCancelled(true);

            if (e.getRawSlot() == 22) { // Back button
                new ViewCurrentListingsScreen(holder.getPlayer()).openScreen(1);
            }
        }
    }
}
