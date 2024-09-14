package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.View.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCompletedTradesScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeManagementScreenListener extends ScreenListener {
    @Override
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
