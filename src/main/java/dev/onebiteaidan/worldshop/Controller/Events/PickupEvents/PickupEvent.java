package dev.onebiteaidan.worldshop.Controller.Events.PickupEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class PickupEvent extends Event {
    protected static final HandlerList HANDLERS = new HandlerList();

    protected Pickup pickup;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    protected void setPickup(Pickup pickup) {
        this.pickup = pickup;
    }

    public Pickup getPickup() {
        return pickup;
    }
}
