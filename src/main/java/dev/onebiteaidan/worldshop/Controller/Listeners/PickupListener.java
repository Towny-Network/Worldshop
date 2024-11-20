package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.Events.PickupEvents.PickupEvent;
import dev.onebiteaidan.worldshop.Controller.StoreManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PickupListener implements Listener {
    @EventHandler
    public void onTradeChange(PickupEvent e) {
        StoreManager.getInstance().syncPickupsToDatabase();
    }
}
