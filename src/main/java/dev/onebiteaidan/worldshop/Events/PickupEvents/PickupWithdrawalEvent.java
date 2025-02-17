package dev.onebiteaidan.worldshop.Events.PickupEvents;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;

public class PickupWithdrawalEvent extends PickupEvent {
    public PickupWithdrawalEvent(Pickup pickup) {
        setPickup(pickup);
    }
}
