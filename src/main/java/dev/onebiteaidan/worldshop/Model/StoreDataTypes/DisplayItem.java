package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import org.bukkit.inventory.ItemStack;

public class DisplayItem extends ItemStack {

    int TradeID;
    int PickupID;

    public DisplayItem(ItemStack item) {
        super(item);
        // Initialize values to invalid defaults
        this.TradeID = -1;
        this.PickupID = -1;
    }

    public int getTradeID() {
        return TradeID;
    }

    public void setTradeID(int tradeID) {
        TradeID = tradeID;
    }

    public int getPickupID() {
        return PickupID;
    }

    public void setPickupID(int pickupID) {
        PickupID = pickupID;
    }
}
