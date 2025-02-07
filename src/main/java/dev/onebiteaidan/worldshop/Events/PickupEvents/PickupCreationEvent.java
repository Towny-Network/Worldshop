package dev.onebiteaidan.worldshop.Events.PickupEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;

public class PickupCreationEvent extends PickupEvent {
    public PickupCreationEvent(Pickup pickup) {
        setPickup(pickup);
    }
}
