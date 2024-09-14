package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Pickup {
    OfflinePlayer player;
    ItemStack item;
    int tradeID;
    boolean withdrawn;
    long timeWithdrawn;

    public Pickup(OfflinePlayer player, ItemStack item, int tradeID, boolean withdrawn, long timeWithdrawn) {
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.withdrawn = withdrawn;
        this.timeWithdrawn = timeWithdrawn;
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
        return timeWithdrawn;
    }

    public void setTimeWithdrawn(long timeWithdrawn) {
        this.timeWithdrawn = timeWithdrawn;
    }
}
