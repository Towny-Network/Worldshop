package dev.onebiteaidan.worldshop.Controller.Events.TradeEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;

public class TradeCreationEvent extends TradeEvent {
    public TradeCreationEvent(Trade trade) {
        setTrade(trade);
    }
}
