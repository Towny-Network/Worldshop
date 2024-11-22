package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Cache.PlayerCache;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;

public class PlayerManager {
    private final Database db;
    private final PlayerCache players;

    public PlayerManager() {
        this.db = WorldShop.getDatabase();
        this.players = new PlayerCache(db);
    }

    /**
     * Creates a new Profile object and stores it in the database.
     * @param player OfflinePlayer object to create a profile for.
     */
    public void createPlayerProfile(OfflinePlayer player) {
        Profile profile = new Profile(player, 0, 0);
        players.put(player, profile);
    }

    /**
     * Retrieves a Profile object from the database.
     * @param player OfflinePlayer object to look up the record of.
     * @return Returns corresponding Profile object.
     */
    public Profile getPlayerProfile(OfflinePlayer player) {
        return players.get(player);
    }

    /**
     * Increments the purchases counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerPurchases(OfflinePlayer player) {
        Profile profile = players.get(player);
        profile.setPurchases(profile.getPurchases() + 1);
        players.put(profile.getPlayer(), profile);
    }

    /**
     * Increments the sales counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerSales(OfflinePlayer player) {
        Profile profile = players.get(player);
        profile.setSales(profile.getSales() + 1);
        players.put(profile.getPlayer(), profile);
    }
}
