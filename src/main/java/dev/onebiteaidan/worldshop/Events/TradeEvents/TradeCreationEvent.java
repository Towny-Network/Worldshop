package dev.onebiteaidan.worldshop.Events.TradeEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;

public class TradeCreationEvent extends TradeEvent {
    public TradeCreationEvent(Trade trade) {
        setTrade(trade);
    }
}
