package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Pickup {
    int pickupID;
    OfflinePlayer player;
    ItemStack item;
    int tradeID;
    boolean withdrawn;
    long withdrawnTimestamp;

    /**
     * Use when creating a brand-new Pickup object for the system.
     * Sets withdraw to false and withdrawnTimestamp to an invalid value.
     * @param pickupID
     * @param player
     * @param item
     * @param tradeID
     */
    public Pickup(int pickupID, OfflinePlayer player, ItemStack item, int tradeID) {
        this.pickupID = pickupID;
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.withdrawn = false;
        this.withdrawnTimestamp = -1L; // Invalid value to show that it has not been withdrawn.
    }

    /**
     * Build a Pickup object from an SQL ResultSet
     * Needs to be called within a try-with-resources
     * @param rs ResultSet to build from.
     */
    public Pickup(ResultSet rs) throws SQLException {
        //todo: Column names need to be gotten from a common source
        this.pickupID = rs.getInt("PICKUP_ID");
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("PLAYER_UUD")));
        try {
            this.item = Utils.loadItemStack(rs.getBytes("PICKUP_ITEM"));
        } catch (IOException e) {
            Logger.logStacktrace(e);
        }

        this.tradeID = rs.getInt("TRADE_ID");
        this.withdrawn = rs.getBoolean("COLLECTED");
        this.withdrawnTimestamp = rs.getLong("TIME_COLLECTED");
    }

    public int getPickupID() {
        return pickupID;
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

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public long getTimeWithdrawn() {
        return withdrawnTimestamp;
    }

    public void setWithdrawnTimestamp(long withdrawnTimestamp) {
        this.withdrawnTimestamp = withdrawnTimestamp;
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
                ", withdrawn=" + withdrawn +
                ", withdrawnTimestamp=" + withdrawnTimestamp +
                "}";
    }
}
