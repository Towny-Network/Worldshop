package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.Events.TradeEvents.TradeEvent;
import dev.onebiteaidan.worldshop.Controller.TradeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TradeListener implements Listener {

    @EventHandler
    public void onTradeChange(TradeEvent e) {
        TradeManager.getInstance().syncTradesToDatabase();
    }
}
