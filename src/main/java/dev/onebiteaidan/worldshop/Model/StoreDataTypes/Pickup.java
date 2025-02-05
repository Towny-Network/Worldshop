package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Pickup {
    int pickupID;
    OfflinePlayer player;
    ItemStack item;
    int tradeID;
    boolean collected;
    long collectionTimestamp;

    /**
     * Use when creating a brand-new Pickup object for the system.
     * Sets withdraw to false, withdrawnTimestamp to an invalid value, and pickupID to an invalid value.
     * @param player
     * @param item
     * @param tradeID
     */
    public Pickup(OfflinePlayer player, ItemStack item, int tradeID) {
        this.pickupID = -1;
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.collected = false;
        this.collectionTimestamp = -1L; // Invalid value to show that it has not been withdrawn.
    }

    public Pickup(int pickupID, OfflinePlayer player, ItemStack item, int tradeID, boolean collected, long collectionTimestamp) {
        this.pickupID = pickupID;
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.collected = collected;
        this.collectionTimestamp = collectionTimestamp;
    }

    public int getPickupID() {
        return pickupID;
    }

    public void setPickupID(int pickupID) {
        this.pickupID = pickupID;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollectionStatus(boolean collected) {
        this.collected = collected;
    }

    public long getCollectionTimestamp() {
        return collectionTimestamp;
    }

    public void setCollectionTimestamp(long collectionTimestamp) {
        this.collectionTimestamp = collectionTimestamp;
    }

    public DisplayItem generateDisplayItem() {
        ItemStack copiedItem = item.clone();
        DisplayItem displayItem = new DisplayItem(copiedItem);
        displayItem.PickupID = pickupID;
        return displayItem;
    }

    @Override
    public String toString() {
        return "Pickup {" +
                "playerUUID=" + player.getUniqueId() + "( " + player.getName() + " )" +
                ", item=" + (item != null ? item.getType() : "None") +
                ", tradeID=" + tradeID +
                ", withdrawn=" + collected +
                ", withdrawnTimestamp=" + collectionTimestamp +
                "}";
    }
}
