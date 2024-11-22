package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.Table;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.PickupColumn;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOError;
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

    public boolean syncToDatabase() {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        int updateStatus = 0;

        try {
            updateStatus = qb.insertInto(Table.PICKUPS, PickupColumn.PLAYER_UUID, PickupColumn.PICKUP_ITEM, PickupColumn.TRADE_ID, PickupColumn.COLLECTED, PickupColumn.TIME_COLLECTED)
                    .values("?,?,?,?,?")
                    .addParameter(this.player.getUniqueId().toString())
                    .addParameter(this.item)
                    .addParameter(this.tradeID)
                    .addParameter(this.withdrawn)
                    .addParameter(this.withdrawnTimestamp)
                    .executeUpdate();

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }


        // Return status of database operation
        if (updateStatus > 0) {
            this.isDirty = false;
            return true;
        }

        return false;
    }

    public int getPickupID() {
        return pickupID;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.isDirty = true;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        this.isDirty = true;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
        this.isDirty = true;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(boolean withdrawn) {
        this.withdrawn = withdrawn;
        this.isDirty = true;
    }

    public long getTimeWithdrawn() {
        return withdrawnTimestamp;
    }

    public void setWithdrawnTimestamp(long withdrawnTimestamp) {
        this.withdrawnTimestamp = withdrawnTimestamp;
        this.isDirty = true;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public DisplayItem generateDisplayItem() {
        ItemStack displayItem = item.clone();
        return new DisplayItem(displayItem, tradeID);
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
