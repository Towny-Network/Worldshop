package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.Events.TradeEvent;
import dev.onebiteaidan.worldshop.Controller.StoreManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TradeListener implements Listener {

    @EventHandler
    public void onTradeChange(TradeEvent e) {
        StoreManager.getInstance().syncTradesToDatabase();
    }
}
