package dev.onebiteaidan.worldshop.Controller.Events.PickupEvents;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;

public class PickupWithdrawalEvent extends PickupEvent {
    public PickupWithdrawalEvent(Pickup pickup) {
        setPickup(pickup);
    }
}
