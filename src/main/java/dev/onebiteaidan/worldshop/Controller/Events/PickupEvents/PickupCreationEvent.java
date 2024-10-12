package dev.onebiteaidan.worldshop.Controller.Events.PickupEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

public class PickupCreationEvent extends PickupEvent {
    public PickupCreationEvent(Pickup pickup) {
        setPickup(pickup);
    }
}
