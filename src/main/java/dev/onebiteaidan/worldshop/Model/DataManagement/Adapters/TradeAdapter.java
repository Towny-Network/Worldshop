package dev.onebiteaidan.worldshop.Model.DataManagement.Adapters;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeAdapter implements Adapter<Trade> {
    @Override
    public Map<String, Object> serialize(Trade trade) {
        HashMap<String, Object> hash = new HashMap<>();

        hash.put("trade_id", trade.getTradeID());
        hash.put("trade_status", trade.getTradeStatus().ordinal());
        hash.put("seller", trade.getSeller().getUniqueId());
        hash.put("buyer", trade.getBuyer().getUniqueId());
        hash.put("itemOffered", trade.getItemOffered());
        hash.put("itemRequested", trade.getItemRequested());
        hash.put("listingTimestamp", trade.getListingTimestamp());
        hash.put("completionTimestamp", trade.getCompletionTimestamp());

        return hash;
    }

    @Override
    public Trade deserialize(Map<String, Object> data) {
        // Extract data from the map
        int tradeId = Integer.parseInt((String) data.get("trade_id"));
        TradeStatus tradeStatus = TradeStatus.values()[(int) data.get("trade_status")];
        OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("seller")));
        OfflinePlayer buyer = Bukkit.getOfflinePlayer(UUID.fromString((String) data.get("buyer")));
        ItemStack itemOffered = (ItemStack) data.get("itemOffered");
        ItemStack itemRequested = (ItemStack) data.get("itemRequested");
        long listingTimestamp = (long) data.get("listingTimestamp");
        long completionTimestamp = (long) data.get("completionTimestamp");

        // Create and populate the Trade object
        return new Trade(tradeId, tradeStatus, seller, buyer, itemOffered, itemRequested, listingTimestamp, completionTimestamp);
    }
}
