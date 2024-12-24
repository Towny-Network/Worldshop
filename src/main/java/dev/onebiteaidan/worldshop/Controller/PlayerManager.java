package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Model.DataManagement.Repositories.ProfileRepository;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerManager {


    private final ProfileRepository profileRepository;

    public PlayerManager(JavaPlugin plugin) {
        File databaseFile = new File(plugin.getDataFolder().getAbsolutePath() + "worldshop.db");
        profileRepository = new ProfileRepository(databaseFile);
    }

    /**
     * Creates a new Profile object and stores it in the database.
     * @param player OfflinePlayer object to create a profile for.
     */
    public void createPlayerProfile(OfflinePlayer player) {
        Profile profile = new Profile(player, 0, 0);
        profileRepository.save(player.getUniqueId(), profile);
    }

    /**
     * Retrieves a Profile object from the database.
     * @param player OfflinePlayer object to look up the record of.
     * @return Returns corresponding Profile object.
     */
    public Profile getPlayerProfile(OfflinePlayer player) {
        return profileRepository.find(player.getUniqueId());
    }

    /**
     * Increments the purchases counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerPurchases(OfflinePlayer player) {
        Profile profile = profileRepository.find(player.getUniqueId());
        profile.setPurchases(profile.getPurchases() + 1);
        profileRepository.save(player.getUniqueId(), profile);
    }

    /**
     * Increments the sales counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerSales(OfflinePlayer player) {
        Profile profile = profileRepository.find(player.getUniqueId());
        profile.setPurchases(profile.getSales() + 1);
        profileRepository.save(player.getUniqueId(), profile);
    }
}
