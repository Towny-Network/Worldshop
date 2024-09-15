package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeViewerScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeViewerScreenListener extends ScreenListener {
    @Override
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
