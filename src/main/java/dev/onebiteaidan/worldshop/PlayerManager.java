package dev.onebiteaidan.worldshop;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
        Connection connection = WorldShop.getDatabase().getConnection();
        try {
            ResultSet rs = WorldShop.getDatabase().query("SELECT * FROM players WHERE uuid = ?;",
                    new Object[]{player.getUniqueId()},
                    new int[]{Types.VARCHAR}, connection);

            rs.next();

            PlayerProfile pp = new PlayerProfile(
                    Bukkit.getOfflinePlayer(rs.getString("uuid")),
                    rs.getInt("purchases"),
                    rs.getInt("sales")
            );

            rs.close();
            connection.close();

            return pp;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void incrementPlayerPurchases(Player player) {
        WorldShop.getDatabase().update("UPDATE players SET purchases = purchases + ? WHERE uuid = ?;",
                new Object[]{1, player.getUniqueId()},
                new int[]{Types.INTEGER, Types.VARCHAR});
    }

    public void incrementPlayerSales(Player player) {
        WorldShop.getDatabase().update("UPDATE players SET sales = sales + ? WHERE uuid = ?;",
                new Object[]{1, player.getUniqueId()},
                new int[]{Types.INTEGER, Types.VARCHAR});
    }
}
