package dev.onebiteaidan.worldshop.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.ViewCompletedTradesScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeManagementScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof TradeManagementScreen) {
            TradeManagementScreen holder = (TradeManagementScreen) event.getInventory().getHolder();

            event.setCancelled(true);

            switch(event.getRawSlot()) {
                case 11: // View Current Listings Button
                    new ViewCurrentListingsScreen(holder.getPlayer()).openScreen(1);
                    break;

                case 15: // View Completed Trades Button
                    new ViewCompletedTradesScreen(holder.getPlayer()).openScreen(1);
                    break;

                case 22: // Back Button
                    new MainShopScreen(holder.getPlayer()).openScreen(1);
                    break;
            }
        }
    }
}
