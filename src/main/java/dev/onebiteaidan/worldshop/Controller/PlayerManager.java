package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.Table;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.PlayerColumn;

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

    private PlayerProfile getPlayerStats(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                .select(PlayerColumn.ALL.toString())
                .from(Table.PLAYERS)
                .where(PlayerColumn.PLAYER_UUID + " = ? ")
                .addParameter(player.getUniqueId().toString())
                .executeQuery()) {

            rs.next();

            return new PlayerProfile(
                    Bukkit.getOfflinePlayer(rs.getString(PlayerColumn.PLAYER_UUID.toString())),
                    rs.getInt(PlayerColumn.PURCHASES.toString()),
                    rs.getInt(PlayerColumn.SALES.toString())
            );

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }

        return null;
    }

    public void incrementPlayerPurchases(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {

            qb.update(Table.PLAYERS)
                    .set(PlayerColumn.PURCHASES.toString())
                    .where(PlayerColumn.PLAYER_UUID + " = ?")
                    .addParameter(PlayerColumn.PURCHASES + " " + 1)
                    .addParameter(player.getUniqueId().toString())
                    .executeUpdate();

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
    }

    public void incrementPlayerSales(Player player) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try {

            qb.update(Table.PLAYERS)
                    .set(PlayerColumn.SALES + " = " + PlayerColumn.SALES + " + ?")
                    .where(PlayerColumn.PLAYER_UUID + " = ?")
                    .addParameter(1)
                    .addParameter(player.getUniqueId().toString())
                    .executeUpdate();

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }
    }
}
