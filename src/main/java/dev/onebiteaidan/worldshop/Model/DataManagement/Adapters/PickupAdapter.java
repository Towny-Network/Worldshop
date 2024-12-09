package dev.onebiteaidan.worldshop.Model.DataManagement.Adapters;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PickupAdapter implements Adapter<Pickup> {
    @Override
    public Map<String, Object> serialize(Pickup pickup) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("pickup_id", pickup.getPickupID());
        hash.put("player", pickup.getPlayer().getUniqueId());
        hash.put("item", pickup.getItem());
        hash.put("trade_id", pickup.getTradeID());
        hash.put("withdraw", pickup.isWithdrawn());
        hash.put("withdrawn_timestamp", pickup.getTimeWithdrawn());

        return hash;
    }

    @Override
    public Pickup deserialize(Map<String, Object> data) {
        // Extract data from the map
        int pickupId = Integer.parseInt((String) data.get("pickup_id"));
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("player")));
        ItemStack item = (ItemStack) data.get("item");
        int tradeId = Integer.parseInt((String) data.get("trade_id"));
        boolean withdrawn = (boolean) data.get("withdrawn");
        long withdrawnTimestamp = (long) data.get("withdrawnTimestamp");

        // Create and populate the Pickup object
        return new Pickup(pickupId, player, item, tradeId, withdrawn, withdrawnTimestamp);
    }
}
