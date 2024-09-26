package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Controller.Events.PickupEvents.PickupCreationEvent;
import dev.onebiteaidan.worldshop.Controller.Events.PickupEvents.PickupWithdrawalEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeEvents.TradeCompletionEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeEvents.TradeCreationEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeEvents.TradeDeletionEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeEvents.TradeExpirationEvent;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.View.Screen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class StoreManager {

    private static StoreManager instance;

    private final ArrayList<Player> playerUpdateList;

    private final ArrayList<Trade> trades;
    private final ArrayList<Pickup> pickups;


    private StoreManager() {
        playerUpdateList = new ArrayList<>();
        trades = new ArrayList<>();
        pickups = new ArrayList<>();
    }

    public static StoreManager getInstance() {
        if (instance == null) {
            instance = new StoreManager();
        }

        return instance;
    }

    /**
     * Find a trade from a given tradeID
     * @param tradeID for corresponding trade
     * @return Trade object with matching ID if found, otherwise return null
     */
    public Trade getTrade(int tradeID) {
        for (Trade trade : trades) {
            if (trade.getTradeID() == tradeID) {
                return trade;
            }
        }

        return null;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<Pickup> getPickups() {
        return pickups;
    }

    public void createTrade(Trade trade) {
        trades.add(trade);

        // Send TradeCreationEvent
        Bukkit.getPluginManager().callEvent(new TradeCreationEvent(trade));
    }

    public void completeTrade(Trade trade, Player buyer) {
        trade.setTradeStatus(TradeStatus.COMPLETE);
        trade.setBuyer(buyer);
        trade.setCompletionTimestamp(System.currentTimeMillis());

        // Create two new pickups
        Pickup buyerPickup = new Pickup(buyer, trade.getItemOffered(), trade.getTradeID(), false, 0L);
        Pickup sellerPickup = new Pickup(trade.getSeller(), trade.getItemRequested(), trade.getTradeID(), false, 0L);

        createPickup(buyerPickup);
        createPickup(sellerPickup);

        // Update the views of all players
        updateAllScreens();

        // Send TradeCompletionEvent
        Bukkit.getPluginManager().callEvent(new TradeCompletionEvent(trade));
    }

    public void deleteTrade(Trade trade) {

        // Send TradeDeletionEvent
        Bukkit.getPluginManager().callEvent(new TradeDeletionEvent(trade));
    }

    public void expireTrade(Trade trade) {
        // Send TradeExpirationEvent
        Bukkit.getPluginManager().callEvent(new TradeExpirationEvent(trade));
    }

    public void createPickup(Pickup pickup) {
        pickups.add(pickup);

        // Send PickupCreationEvent
        Bukkit.getPluginManager().callEvent(new PickupCreationEvent(pickup));
    }

    public void withdrawPickup(Pickup pickup) {
        pickup.setWithdrawn(true);
        pickup.setWithdrawnTimestamp(System.currentTimeMillis());

        // Send PickupWithdrawalEvent
        Bukkit.getPluginManager().callEvent(new PickupWithdrawalEvent(pickup));
    }

    public void withdrawPickup(int tradeId, Player player) {
        // Get pickup object from tradeId
        for (Pickup pickup : pickups) {
            if (pickup.getTradeID() == tradeId && pickup.getPlayer().equals(player)) {
                // Pass pickup object into withdrawPickup
                withdrawPickup(pickup);
            }
        }

        // No matching pickups found
        Logger.severe("NO PICKUPS MATCHED TRADE ID OR PLAYER DURING PICKUP WITHDRAWAL!");
        Logger.severe("PLAYER: " + player.getName() + ", TRADE ID: " + tradeId);
    }

    public void syncTradesToDatabase() {
        for (Trade trade : this.trades) {
            if (trade.isDirty()) {
                trade.syncToDatabase();
            }
        }
    }

    public void syncPickupsToDatabase() {
        for (Pickup pickup : this.pickups) {
            if (pickup.isDirty()) {
                pickup.syncToDatabase();
            }
        }
    }

    public void updateAllScreens() {
        for (Player player : playerUpdateList) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Screen) {
                ((Screen) player.getOpenInventory().getTopInventory().getHolder()).update();
            } else {
                Logger.severe("Attempted to update an InventoryView that was not an instanceof Screen");
            }
        }
    }

    public void addToUpdateList(Player p) {
        this.playerUpdateList.add(p);
    }

    public void removeFromUpdateList(Player p) {
        this.playerUpdateList.remove(p);
    }
}