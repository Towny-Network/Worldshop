package dev.onebiteaidan.worldshop.Controller.Events;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class TradeEvent extends Event {
    protected static final HandlerList HANDLERS = new HandlerList();

    protected  Trade trade;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    protected void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }
}
