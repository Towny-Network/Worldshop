package dev.onebiteaidan.worldshop.Events.TradeEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;

public class TradeDeletionEvent extends TradeEvent {
    public TradeDeletionEvent(Trade trade) {
        setTrade(trade);
    }

}
