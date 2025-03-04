package dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes;

import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pickup {
    int pickupID;
    OfflinePlayer player;
    ItemStack item;
    int tradeID;
    boolean collected;
    long collectionTimestamp;

    /**
     * Use when creating a brand-new Pickup object for the system.
     * Sets withdraw to false, withdrawnTimestamp to an invalid value, and pickupID to an invalid value.
     * @param player
     * @param item
     * @param tradeID
     */
    public Pickup(OfflinePlayer player, ItemStack item, int tradeID) {
        this.pickupID = -1;
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.collected = false;
        this.collectionTimestamp = -1L; // Invalid value to show that it has not been withdrawn.
    }

    public Pickup(int pickupID, OfflinePlayer player, ItemStack item, int tradeID, boolean collected, long collectionTimestamp) {
        this.pickupID = pickupID;
        this.player = player;
        this.item = item;
        this.tradeID = tradeID;
        this.collected = collected;
        this.collectionTimestamp = collectionTimestamp;
    }

    public int getPickupID() {
        return pickupID;
    }

    public void setPickupID(int pickupID) {
        this.pickupID = pickupID;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getTradeID() {
        return tradeID;
    }

    public void setTradeID(int tradeID) {
        this.tradeID = tradeID;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollectionStatus(boolean collected) {
        this.collected = collected;
    }

    public long getCollectionTimestamp() {
        return collectionTimestamp;
    }

    public void setCollectionTimestamp(long collectionTimestamp) {
        this.collectionTimestamp = collectionTimestamp;
    }

    public ItemStack generateDisplayItem() {
        ItemStack itemClone = item.clone();
        ItemMeta meta = itemClone.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(WorldShop.getInstance(), "pickup_id"),
                    PersistentDataType.INTEGER, pickupID);

            // Add lore to item
            List<Component> lore = new ArrayList<>();

            Trade trade = WorldShop.getStoreManager().getTrade(tradeID);
//            if (player.equals(trade.seller)) {
//                lore.add(Component.text("Bought From:").color(NamedTextColor.GOLD));
//                lore.add(Component.text(trade.seller.getName()));
//            } else {
//                lore.add(Component.text("Bought By:").color(NamedTextColor.GOLD));
//                lore.add(Component.text(trade.buyer.getName()));
//            }

            String buyerName = trade.getBuyer().getName();
            String sellerName = trade.getSeller().getName();

            ItemStack itemOffered = trade.getItemOffered();
            Component itemNameOffered;
            if (itemOffered.getItemMeta().hasDisplayName()) {
                itemNameOffered = itemOffered.getItemMeta().displayName();
            } else {
                itemNameOffered = Component.text(itemOffered.getType().toString()).color(NamedTextColor.GRAY);
            }
            Component itemBought = Component.text(trade.getItemOffered().getAmount() + "x ").append(itemNameOffered);

            ItemStack itemRequested = trade.getItemOffered();
            Component itemNameRequested;
            if (itemRequested.getItemMeta().hasDisplayName()) {
                itemNameRequested = itemOffered.getItemMeta().displayName();
            } else {
                itemNameRequested = Component.text(itemOffered.getType().toString()).color(NamedTextColor.GRAY);
            }
            Component itemSold = Component.text(trade.getItemOffered().getAmount() + "x ").append(itemNameRequested);

            lore.add(Component.text("Trade #" + tradeID));
            lore.add(Component.text(trade.buyer.getName() + " bought ").append(itemBought));
            lore.add(Component.text("from " + trade.seller.getName() + " for ").append(itemSold));

            // Set the item lore
            meta.lore(lore);

            itemClone.setItemMeta(meta);
        }

        return itemClone;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pickup) {
            Pickup pickup = (Pickup) o;
            return pickupID == pickup.pickupID &&
                    player.equals(pickup.player) &&
                    item.equals(pickup.item) &&
                    tradeID == pickup.tradeID &&
                    collected == pickup.collected &&
                    collectionTimestamp == pickup.collectionTimestamp;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pickup {" +
                "playerUUID=" + player.getUniqueId() + "( " + player.getName() + " )" +
                ", item=" + (item != null ? item.getType() : "None") +
                ", tradeID=" + tradeID +
                ", withdrawn=" + collected +
                ", withdrawnTimestamp=" + collectionTimestamp +
                "}";
    }
}
