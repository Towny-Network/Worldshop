package dev.onebiteaidan.worldshop.Controller.Events.TradeEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;

public class TradeDeletionEvent extends TradeEvent {
    public TradeDeletionEvent(Trade trade) {
        setTrade(trade);
    }

}
