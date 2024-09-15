package dev.onebiteaidan.worldshop.Controller.Events;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TradeDeletionEvent extends TradeEvent {
    public TradeDeletionEvent(Trade trade) {
        setTrade(trade);
    }

}
