package dev.onebiteaidan.worldshop.Controller;

import dev.onebiteaidan.worldshop.Controller.Events.TradeCompletionEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeCreationEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeDeletionEvent;
import dev.onebiteaidan.worldshop.Controller.Events.TradeExpirationEvent;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.*;

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

    public void createTrade(Trade trade) {
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

        pickups.add(buyerPickup);
        pickups.add(sellerPickup);

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

    public void syncTradesToDatabase() {
        for (Trade trade : this.trades) {
            if (trade.isDirty()) {
                trade.syncToDatabase();
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

    public void pickupCompleted(Pickup pickup) {
        pickup.setWithdrawn(true);
        pickup.setWithdrawnTimestamp(System.currentTimeMillis());
    }

    public void addToUpdateList(Player p) {
        this.playerUpdateList.add(p);
    }

    public void removeFromUpdateList(Player p) {
        this.playerUpdateList.remove(p);
    }


    /**
     * Gets the trade from the passed in display item.
     * @param displayItem item to find trade of.
     * @return returns the itemstack that has the same trade. Returns NULL of no matching itemstack is found
     */
    public Trade getTradeFromDisplayItem(ItemStack displayItem) {
        if (displayItem.getItemMeta().hasLocalizedName()) {
            Database db = WorldShop.getDatabase();
            QueryBuilder qb = new QueryBuilder(db);

            return getTradeFromTradeID(Integer.parseInt(displayItem.getItemMeta().getLocalizedName()));
        }
        return null;
    }

    /**
     * Get the trade from the associated integer trade ID
     * @param id integer ID associated with the trade.
     * @return returns the trade associated w/ the ID. Returns null of no trade is found
     */
    public Trade getTradeFromTradeID(int id) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                .select("*")
                .from("trades")
                .where("trade_id = ?")
                .addParameter(id)
                .executeQuery()) {

            rs.next();

            // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
            OfflinePlayer buyer = null;
            String buyerUUID = rs.getString("buyer_uuid");
            if (!rs.wasNull()) {
                buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
            }

            Trade trade = new Trade(rs.getInt("trade_id"),
                    TradeStatus.values()[rs.getInt("status")],
                    Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                    buyer,
                    ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                    ItemStack.deserializeBytes(rs.getBytes("in_return")),
                    rs.getLong("time_listed"),
                    rs.getLong("time_completed")
            );

            return trade;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<ItemStack> getAllCurrentTradesDisplayItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                    .select("*")
                    .from("trades")
                    .where("status = ? AND seller_uuid = ?")
                    .addParameter(TradeStatus.OPEN.ordinal())
                    .addParameter(player.getUniqueId().toString())
                    .executeQuery()) {

            while (rs.next()) {
                // The string to UUID conversion breaks when the value is null, so we have to do a null check here.
                OfflinePlayer buyer = null;
                String buyerUUID = rs.getString("buyer_uuid");
                if (!rs.wasNull()) {
                    buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUUID));
                }

                items.add(new Trade(rs.getInt("trade_id"),
                        TradeStatus.values()[rs.getInt("status")],
                        Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("seller_uuid"))),
                        buyer,
                        ItemStack.deserializeBytes(rs.getBytes("for_sale")),
                        ItemStack.deserializeBytes(rs.getBytes("in_return")),
                        rs.getLong("time_listed"),
                        rs.getLong("time_completed")
                ).generateDisplayItem());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<ItemStack> getAllCompletedTradesItems(Player player) {
        ArrayList<ItemStack> pickups = new ArrayList<>();
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        try (ResultSet rs = qb
                    .select("*")
                    .from("pickups")
                    .where("player_uuid = ? AND collected = ?")
                    .addParameter(player.getUniqueId().toString())
                    .addParameter(false)
                    .executeQuery()) {

            while (rs.next()) {

                Pickup p = new Pickup(Bukkit.getPlayer(UUID.fromString(rs.getString("player_uuid"))),
                        ItemStack.deserializeBytes(rs.getBytes("pickup_item")),
                        rs.getInt("trade_id"), rs.getBoolean("collected"),
                        rs.getLong("time_collected")
                );

                ItemStack item = p.getItem();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setLocalizedName(String.valueOf(p.getTradeID()));
                item.setItemMeta(itemMeta);

                pickups.add(item);
            }

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }

        return pickups;
    }

    // endregion



}