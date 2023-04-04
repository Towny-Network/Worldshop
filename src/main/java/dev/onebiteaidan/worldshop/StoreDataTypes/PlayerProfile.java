package dev.onebiteaidan.worldshop.StoreDataTypes;

import org.bukkit.entity.Player;

public class PlayerProfile {

    Player player;
    int purchases;
    int sales;

    public PlayerProfile(Player player, int purchases, int sales) {
        this.player = player;
        this.purchases = purchases;
        this.sales = sales;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPurchases() {
        return purchases;
    }

    public int getSales() {
        return sales;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }
}
