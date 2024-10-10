package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import org.bukkit.inventory.ItemStack;

public class DisplayItem extends ItemStack {

    int tradeID;

    public DisplayItem(ItemStack item, int tradeID) {
        super(item);
        this.tradeID = tradeID;
    }

    public int getTradeID() {
        return tradeID;
    }
}
