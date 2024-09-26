package dev.onebiteaidan.worldshop.Controller.Events.TradeEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;

public class TradeExpirationEvent extends TradeEvent {
    public TradeExpirationEvent(Trade trade) {
        setTrade(trade);
    }
}
