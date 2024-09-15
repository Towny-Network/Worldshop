package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class Pickup {
    OfflinePlayer player;
    ItemStack item;
    int tradeID;
    boolean withdrawn;
    long withdrawnTimestamp;

    private boolean isDirty;

    public Pickup(OfflinePlayer player, ItemStack item, int tradeID, boolean withdrawn, long withdrawnTimestamp) {
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.withdrawn = withdrawn;
        this.withdrawnTimestamp = withdrawnTimestamp;
        this.isDirty = true;
    }

    public boolean syncToDatabase() {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        int updateStatus = 0;

        try {
            updateStatus = qb.insertInto("pickups", "player_uuid, pickup_item, trade_id, collected, time_collected")
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
}
