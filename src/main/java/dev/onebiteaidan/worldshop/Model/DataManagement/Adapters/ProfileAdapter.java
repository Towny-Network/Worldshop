package dev.onebiteaidan.worldshop.Model.DataManagement.Adapters;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileAdapter implements Adapter<Profile> {
    @Override
    public Map<String, Object> serialize(Profile profile) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("player", profile.getPlayer().getUniqueId());
        hash.put("purchases", profile.getPurchases());
        hash.put("sales", profile.getSales());

        return hash;
    }

    @Override
    public Profile deserialize(Map<String, Object> data) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("player")));
        int purchases = Integer.parseInt((String) data.get("purchases"));
        int sales = Integer.parseInt((String) data.get("sales"));

        return new Profile(player, purchases, sales);
    }
}
