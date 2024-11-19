package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.Events.PickupEvents.PickupEvent;
import dev.onebiteaidan.worldshop.Controller.TradeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PickupListener implements Listener {
    @EventHandler
    public void onTradeChange(PickupEvent e) {
        TradeManager.getInstance().syncPickupsToDatabase();
    }
}
