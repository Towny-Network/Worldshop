package dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes;

import org.bukkit.OfflinePlayer;

public class Profile {

    private OfflinePlayer player;
    private int purchases;
    private int sales;

    public Profile(OfflinePlayer player, int purchases, int sales) {
        this.player = player;
        this.purchases = purchases;
        this.sales = sales;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile) {
            Profile profile = (Profile) o;
            return player == profile.player &&
                    purchases == profile.purchases &&
                    sales == profile.sales;
        }
        return false;
    }
}
