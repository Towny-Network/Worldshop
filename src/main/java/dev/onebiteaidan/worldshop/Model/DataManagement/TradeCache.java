package dev.onebiteaidan.worldshop.Model.DataManagement;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TradeCache {

    // A map that holds tradeID -> Trade for fast lookup
    private final Map<Integer, Trade> tradeCache = new ConcurrentHashMap<>();
    private final TradeRepository tradeRepository; // Interface to interact with the database

    // Constructor to initialize the TradeCache with a reference to the repository
    public TradeCache(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    // Method to load active trades from the database into the cache at plugin startup
    public void loadTradesIntoCache() {
        List<Trade> trades = tradeRepository.getActiveTradesFromDatabase();
        for (Trade trade : trades) {
            tradeCache.put(trade.getTradeID(), trade);
        }
    }

    // Method to add a new trade to the cache (and optionally to the database)
    public void addTrade(Trade trade) {
        tradeCache.put(trade.getTradeID(), trade);
        CompletableFuture.runAsync(() -> tradeRepository.saveTrade(trade)); // Save to DB asynchronously
    }

    // Method to update an existing trade in the cache (and optionally in the database)
    public void updateTrade(Trade trade) {
        if (tradeCache.containsKey(trade.getTradeID())) {
            tradeCache.put(trade.getTradeID(), trade); // Replace the trade in the cache
            CompletableFuture.runAsync(() -> tradeRepository.updateTrade(trade)); // Update DB asynchronously
        }
    }

    // Method to remove a trade from the cache (and optionally from the database)
    public void removeTrade(int tradeId) {
        tradeCache.remove(tradeId);
        CompletableFuture.runAsync(() -> tradeRepository.deleteTradeById(tradeId)); // Remove from DB asynchronously
    }

    // Method to fetch all active trades from the cache
    public List<Trade> getActiveTrades() {
        return new ArrayList<>(tradeCache.values());
    }

    // Method to refresh the cache periodically (e.g., every 5 minutes)
    public void scheduleCacheRefresh() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(WorldShop.getPlugin(WorldShop.class), () -> {
            List<Trade> trades = tradeRepository.getActiveTradesFromDatabase();
            // Clear the cache and reload it with fresh data from the database
            tradeCache.clear();
            for (Trade trade : trades) {
                tradeCache.put(trade.getTradeID(), trade);
            }
        }, 0L, 6000L); // 6000 ticks = 5 minutes
    }

    // Method to clear the cache (for maintenance or admin commands)
    public void clearCache() {
        tradeCache.clear();
    }

    // Optional: Method to manually refresh specific trade from database if necessary
    public void refreshTrade(int tradeId) {
        Trade trade = tradeRepository.getTradeById(tradeId);
        if (trade != null) {
            tradeCache.put(tradeId, trade);
        }
    }

}
