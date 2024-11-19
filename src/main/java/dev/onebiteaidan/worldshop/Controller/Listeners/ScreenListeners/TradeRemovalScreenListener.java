package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.TradeManager;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeRemovalScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeRemovalScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof TradeRemovalScreen) {
            TradeRemovalScreen holder = (TradeRemovalScreen) e.getInventory().getHolder();

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 11:
                    TradeManager.getInstance().deleteTrade(holder.getTrade());

                    // Put the player back on page 1 of current listings
                    new ViewCurrentListingsScreen(holder.getPlayer()).openScreen(1);
                    break;

                case 15:
                    new ViewCurrentListingsScreen(holder.getPlayer()).openScreen(1);
                    break;

                default:
                    break;
            }
        }
    }
}
