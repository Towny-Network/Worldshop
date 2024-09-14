package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerManager {

    private static class PlayerProfile {
        OfflinePlayer player;
        int purchases;
        int sales;

        PlayerProfile(OfflinePlayer player, int purchases, int sales) {
            this.player = player;
            this.purchases = purchases;
            this.sales = sales;
        }
    }

    public PlayerManager() {}

    public PlayerProfile getPlayerStats(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                .select("*")
                .from("players")
                .where("uuid = ?")
                .addParameter(player.getUniqueId().toString())
                .executeQuery()) {

            rs.next();

            PlayerProfile pp = new PlayerProfile(
                    Bukkit.getOfflinePlayer(rs.getString("uuid")),
                    rs.getInt("purchases"),
                    rs.getInt("sales")
            );

            return pp;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void incrementPlayerPurchases(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {

            qb.update("players")
                    .set("purchases = purchases + ?")
                    .where("uuid = ?")
                    .addParameter(1)
                    .addParameter(player.getUniqueId().toString())
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementPlayerSales(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {

            qb.update("players")
                    .set("sales = sales + ?")
                    .where("uuid = ?")
                    .addParameter(1)
                    .addParameter(player.getUniqueId().toString())
                    .executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
