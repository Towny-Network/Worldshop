package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.Table;
import dev.onebiteaidan.worldshop.Model.DataManagement.Database.DatabaseSchema.TradeColumn;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class Trade {

    int tradeID;
    TradeStatus tradeStatus;
    OfflinePlayer seller;
    OfflinePlayer buyer;
    ItemStack itemOffered;
    ItemStack itemRequested;
    long listingTimestamp;
    long completionTimestamp;

    private boolean isDirty; // Dirty bit for database synchronization


    /**
     * Constructor for when a player creates a new trade via the GUI.
     * @param itemOffered  The item the player is offering for trade.
     * @param seller       The player offering the item.
     * @param itemRequested The item the player wants in return.
     */
    public Trade(Player seller, ItemStack itemOffered, ItemStack itemRequested) {
        this.tradeID = -1; // Setting to invalid. Is updated when synced to database.
        this.tradeStatus = TradeStatus.OPEN;
        this.seller = seller;
        this.buyer = null;
        this.itemOffered = itemOffered;
        this.itemRequested = itemRequested;
        this.listingTimestamp = System.currentTimeMillis();
        this.completionTimestamp = 0L;
        this.isDirty = true;

        boolean success = addToDatabase();

        if (!success) {
            Logger.severe("Trade " + this.tradeID + " was NOT successfully added to the database.");
        } else {
            Logger.info("Trade " + this.tradeID + " was successfully added to the database.");
        }
    }

    /**
     * Constructor for rebuilding from the database.
     * Pulls all data from the tradeID
     * @param tradeID       The ID of the trade.
     */
    public Trade(int tradeID) {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        qb.select(TradeColumn.ALL)
                .from(Table.TRADES)
                .where(TradeColumn.TRADE_ID + " = ?")
                .addParameter(tradeID);

        try (ResultSet rs = qb.executeQuery()) {
            if (rs.next()) {
                // Retrieve tradeStatus
                this.tradeStatus = TradeStatus.values()[rs.getInt(TradeColumn.TRADE_STATUS + "")];

                // Retrieve seller
                this.seller = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(TradeColumn.SELLER_UUID + "")));

                // Check if the buyer UUID is null before processing
                String buyerUuidString = rs.getString(TradeColumn.BUYER_UUID + "");
                if (buyerUuidString != null && !buyerUuidString.isEmpty()) {
                    this.buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyerUuidString));
                } else {
                    this.buyer = null;  // Set buyer to null if there is no buyer UUID
                }

                // Retrieve itemOffered
                byte[] itemOfferedStackBytes = rs.getBytes(TradeColumn.ITEM_OFFERED + "");
                this.itemOffered = Utils.deserializeItem(itemOfferedStackBytes);

                // Retrieve itemRequested
                byte[] itemRequestedStackBytes = rs.getBytes(TradeColumn.ITEM_REQUESTED + "");
                this.itemRequested = Utils.deserializeItem(itemRequestedStackBytes);

                // Retrieve listingTimestamp
                this.listingTimestamp = rs.getLong(TradeColumn.LISTING_TIMESTAMP + "");

                // Retrieve completionTimestamp
                this.completionTimestamp = rs.getLong(TradeColumn.COMPLETION_TIMESTAMP + "");
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            Logger.logStacktrace(e);
        }
    }

    private boolean addToDatabase() {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        int updateStatus = 0;

        try {
            qb.insertInto(Table.TRADES, TradeColumn.SELLER_UUID, TradeColumn.BUYER_UUID, TradeColumn.ITEM_OFFERED, TradeColumn.ITEM_REQUESTED, TradeColumn.TRADE_STATUS, TradeColumn.LISTING_TIMESTAMP, TradeColumn.COMPLETION_TIMESTAMP)
                    .values("?,?,?,?,?,?,?")
                    .addParameter(this.seller.getUniqueId().toString());

            // If buyer is null, don't call OfflinePlayer::getUniqueId()
            if (this.buyer == null) {
                qb.addParameter(null);
            } else {
                qb.addParameter(this.buyer.getUniqueId().toString());
            }

            qb.addParameter(this.itemOffered)
                    .addParameter(this.itemRequested)
                    .addParameter(this.tradeStatus.ordinal())
                    .addParameter(this.listingTimestamp)
                    .addParameter(this.completionTimestamp)
                    .executeUpdate();

            // Get the most recent ID added to the database (should be the max)
            PreparedStatement ps = db.getConnection().prepareStatement("SELECT MAX(" + TradeColumn.TRADE_ID + ") FROM " + Table.TRADES);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.tradeID = rs.getInt(1);
                updateStatus = 1;
            } else {
                Logger.severe("TRADE WAS NOT GIVEN AN ID. " + this.toString());
            }

        } catch (SQLException e) {
            Logger.logStacktrace(e);
        }


        // Return status of database operation
        if (updateStatus > 0) {
            this.isDirty = false;
            return true;
        }

        return false;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
        this.isDirty = true;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
        this.isDirty = true;
    }

    public OfflinePlayer getSeller() {
        return seller;
    }

    public void setSeller(OfflinePlayer seller) {
        this.seller = seller;
        this.isDirty = true;
    }

    public OfflinePlayer getBuyer() {
        return buyer;
    }

    public void setBuyer(OfflinePlayer buyer) {
        this.buyer = buyer;
        this.isDirty = true;
    }

    public ItemStack getItemOffered() {
        return itemOffered;
    }

    public void setItemOffered(ItemStack itemOffered) {
        this.itemOffered = itemOffered;
        this.isDirty = true;
    }

    public ItemStack getItemRequested() {
        return itemRequested;
    }

    public void setItemRequested(ItemStack itemRequested) {
        this.itemRequested = itemRequested;
        this.isDirty = true;
    }

    public long getListingTimestamp() {
        return listingTimestamp;
    }

    public void setListingTimestamp(long listingTimestamp) {
        this.listingTimestamp = listingTimestamp;
        this.isDirty = true;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(long completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public DisplayItem generateDisplayItem() {
        ItemStack displayItem = itemOffered.clone();
        return new DisplayItem(displayItem, tradeID);
    }

    @Override
    public String toString() {
        return "Trade {" +
                "tradeId=" + tradeID +
                ", tradeStatus=" + tradeStatus +
                ", seller=" + (seller != null ? seller.getUniqueId() : "None") +
                ", buyer=" + (buyer != null ? buyer.getUniqueId() : "None") +
                ", itemOffered=" + (itemOffered != null ? itemOffered.getType() : "None") +
                ", itemRequested=" + (itemRequested != null ? itemRequested.getType() : "None") +
                ", listingTimestamp=" + listingTimestamp +
                ", completionTimestamp=" + (completionTimestamp > 0 ? completionTimestamp : "Not completed") +
                '}';
    }

}
