package dev.onebiteaidan.worldshop.Model.StoreDataTypes;

import dev.onebiteaidan.worldshop.Model.DataManagement.Database;
import dev.onebiteaidan.worldshop.Model.DataManagement.QueryBuilder;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;


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

        boolean success = syncToDatabase();

        if (!success) {
            Logger.severe("Trade " + this.tradeID + " had an error occur in the database.");
        }
    }

    /**
     * Constructor for rebuilding from the database.
     * @param tradeID         The ID of the trade.
     * @param tradeStatus     The status of the trade.
     * @param seller          The player offering the item.
     * @param buyer           The player who is paying for the item.
     * @param itemOffered     The item being offered by the seller.
     * @param itemRequested   The item the seller wants in return.
     * @param listingTimestamp The time the trade was listed.
     * @param completionTimestamp The time the trade was completed.
     */
    public Trade(int tradeID, TradeStatus tradeStatus, OfflinePlayer seller, OfflinePlayer buyer, ItemStack itemOffered, ItemStack itemRequested, long listingTimestamp, long completionTimestamp) {
        this.tradeID = tradeID;
        this.tradeStatus = tradeStatus;
        this.seller = seller;
        this.buyer = buyer;
        this.itemOffered = itemOffered;
        this.itemRequested = itemRequested;
        this.listingTimestamp = listingTimestamp;
        this.completionTimestamp = completionTimestamp;
    }

    public boolean syncToDatabase() {
        Database db = WorldShop.getDatabase();
        QueryBuilder qb = new QueryBuilder(db);

        int updateStatus = 0;
        AtomicInteger generatedTradeID = new AtomicInteger(-1);

        try {
            updateStatus = qb.insertInto("trades", "seller_uuid, buyer_uuid, item_offered, item_requested, tradeStatus, listing_timestamp, completion_timestamp")
                    .values("?,?,?,?,?,?,?")
                    .addParameter(this.seller.getUniqueId().toString())
                    .addParameter(this.buyer.getUniqueId().toString())
                    .addParameter(this.itemOffered)
                    .addParameter(this.itemRequested)
                    .addParameter(this.tradeStatus.ordinal())
                    .addParameter(this.listingTimestamp)
                    .addParameter(this.completionTimestamp)
                    .executeUpdateWithGeneratedKeys(rs -> {
                        if (rs.next()) {
                            // Retrieve the generated tradeID
                            generatedTradeID.set(rs.getInt(1)); // Assuming tradeID is the first column returned
                        }
                    });

            if (generatedTradeID.get() > 0) {
                this.tradeID = generatedTradeID.get();
            } else {
                Logger.severe("TradeID was not obtained from database for a new Trade object. " + this);
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
