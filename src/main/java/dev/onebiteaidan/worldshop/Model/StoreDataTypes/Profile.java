package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Profile {

    private OfflinePlayer player;
    private int purchases;
    private int sales;

    public Profile(OfflinePlayer player, int purchases, int sales) {
        this.player = player;
        this.purchases = purchases;
        this.sales = sales;
    }

    public Profile(ResultSet rs) throws SQLException {
        //todo: Column names need to be retrieved from common source.
        this.player = Bukkit.getPlayer(UUID.fromString(rs.getString("PLAYER_UUID")));
        this.purchases = rs.getInt("PURCHASES");
        this.sales = rs.getInt("SALES");
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
}
