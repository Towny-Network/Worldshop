package dev.onebiteaidan.worldshop.Controller.Events.TradeEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;

public class TradeCompletionEvent extends TradeEvent {
    public TradeCompletionEvent(Trade trade) {
        setTrade(trade);
    }
}
