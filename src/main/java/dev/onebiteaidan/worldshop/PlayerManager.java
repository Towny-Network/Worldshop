package dev.onebiteaidan.worldshop;

import dev.onebiteaidan.worldshop.DataManagement.Repositories.ProfileRepository;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Profile;
import org.bukkit.OfflinePlayer;

public class PlayerManager {


    private final ProfileRepository profileRepository;

    public PlayerManager(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Creates a new Profile object and stores it in the database.
     * @param player OfflinePlayer object to create a profile for.
     */
    public void createPlayerProfile(OfflinePlayer player) {
        Profile profile = new Profile(player, 0, 0);
        profileRepository.save(profile);
    }

    /**
     * Retrieves a Profile object from the database.
     * @param player OfflinePlayer object to look up the record of.
     * @return Returns corresponding Profile object.
     */
    public Profile getPlayerProfile(OfflinePlayer player) {
        return profileRepository.findById(player.getUniqueId());
    }

    /**
     * Increments the purchases counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerPurchases(OfflinePlayer player) {
        Profile profile = profileRepository.findById(player.getUniqueId());
        profile.setPurchases(profile.getPurchases() + 1);
        profileRepository.save(profile);
    }

    /**
     * Increments the sales counter in the database for respective player.
     * @param player OfflinePlayer object to update the record of.
     */
    public void incrementPlayerSales(OfflinePlayer player) {
        Profile profile = profileRepository.findById(player.getUniqueId());
        profile.setPurchases(profile.getSales() + 1);
        profileRepository.save(profile);
    }
}
