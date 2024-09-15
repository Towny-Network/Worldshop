package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeRemovalScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCurrentListingsScreen;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TradeRemovalScreenListener extends ScreenListener {
    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof TradeRemovalScreen) {
            TradeRemovalScreen holder = (TradeRemovalScreen) e.getInventory().getHolder();

            e.setCancelled(true);

            switch(e.getRawSlot()) {
                case 11:
                    StoreManager.getInstance().deleteTrade(holder.getTrade().getTradeID());

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
