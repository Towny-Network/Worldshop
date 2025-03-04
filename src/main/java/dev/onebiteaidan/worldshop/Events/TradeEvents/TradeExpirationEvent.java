package dev.onebiteaidan.worldshop.Events.TradeEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;

public class TradeExpirationEvent extends TradeEvent {
    public TradeExpirationEvent(Trade trade) {
        setTrade(trade);
    }
}
