package dev.onebiteaidan.worldshop.Events.TradeEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;

public class TradeCompletionEvent extends TradeEvent {
    public TradeCompletionEvent(Trade trade) {
        setTrade(trade);
    }
}
