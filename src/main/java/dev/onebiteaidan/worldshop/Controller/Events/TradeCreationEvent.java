package dev.onebiteaidan.worldshop.Controller.Events;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TradeCreationEvent extends TradeEvent {
    public TradeCreationEvent(Trade trade) {
        setTrade(trade);
    }
}
