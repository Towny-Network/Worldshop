package dev.onebiteaidan.worldshop.StoreDataTypes;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.management.MemoryType;
import java.sql.Types;
import java.util.ArrayList;


public class Trade {

    int tradeID;
    TradeStatus status;
    OfflinePlayer seller;
    OfflinePlayer buyer;
    ItemStack forSale;
    ItemStack inReturn;
    long timeListed;// Unix time
    long timeCompleted;


    /**
     * Constructor for when a player creates a new trade via the gui.
     * @param forSale      Item player is trading away
     * @param seller       Person selling the forSale item
     */
    public Trade(Player seller, ItemStack forSale, ItemStack inReturn) {
        this.status = TradeStatus.OPEN;
        this.seller = seller;
        this.buyer = null;
        this.forSale = forSale;
        this.inReturn = inReturn;
        this.timeListed = System.currentTimeMillis();
        this.timeCompleted = 0L;
    }

    /**
     * Constructor for rebuilding from the Database.
     * @param tradeID The ID of the trade.
     * @param status The status of the trade.
     * @param seller The player who is selling the forSale itemstack
     * @param buyer The player who paying the inReturn itemstack
     * @param forSale The item that is for sale
     * @param inReturn The item the seller wants in return for the forSale itemstack
     * @param timeListed The time that the trade is made
     * @param timeCompleted The time that the trade is completed
     */
    public Trade(int tradeID, TradeStatus status, OfflinePlayer seller, OfflinePlayer buyer, ItemStack forSale, ItemStack inReturn, long timeListed, long timeCompleted) {
        this.tradeID = tradeID;
        this.status = status;
        this.seller = seller;
        this.buyer = buyer;
        this.forSale = forSale;
        this.inReturn = inReturn;
        this.timeListed = timeListed;
        this.timeCompleted = timeCompleted;
    }

    public ItemStack generateDisplayItem() {
        ItemStack displayItem = new ItemStack(forSale.getType(), forSale.getAmount());
        ItemMeta displayItemMeta = displayItem.getItemMeta();

        // Items that have displaynames are items that have been renamed to something other than their original title.
        // Therefore, we have to implement this shit system
        if (forSale.getItemMeta().hasDisplayName()) {
            displayItemMeta.setDisplayName(ChatColor.YELLOW + forSale.getItemMeta().getDisplayName() + " x" + this.forSale.getAmount());
        } else {
            String s = forSale.getType().toString();
            String[] parts = s.split("_");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].toLowerCase();
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }

            displayItemMeta.setDisplayName(ChatColor.YELLOW + String.join(" ", parts) + " x" + this.forSale.getAmount());
        }

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Being Sold By: " + this.seller.getName());

        if (forSale.getItemMeta().hasLore()) {
            lore.add(ChatColor.DARK_GRAY + "===================");
            lore.add("");
            for (String s : forSale.getItemMeta().getLore()) {
                lore.add(ChatColor.DARK_PURPLE + s);
            }
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "===================");
        }

        lore.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click " + ChatColor.RESET + "" + ChatColor.GOLD + "to buy this item");

        displayItemMeta.setLore(lore);
        // Adding in trade ID for indentification later on
        displayItemMeta.setLocalizedName(String.valueOf(tradeID));
        displayItem.setItemMeta(displayItemMeta);

        return displayItem;
    }

    public ItemStack getForSale() {
        return forSale;
    }

    public ItemStack getInReturn() {
        return inReturn;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        WorldShop.getDatabase().update("UPDATE trades SET status = ? WHERE trade_id = ?;",
                new Object[]{status.ordinal(), this.tradeID},
                new int[]{Types.INTEGER, Types.INTEGER}
        );

        this.status = status;
    }

    public OfflinePlayer getSeller() {
        return seller;
    }

    public OfflinePlayer getBuyer() {
        return buyer;
    }

    public void setBuyer(Player buyer) {
        WorldShop.getDatabase().update("UPDATE trades SET buyer_uuid = ? WHERE trade_id = ?;",
                new Object[]{buyer.getUniqueId(), this.tradeID},
                new int[]{Types.VARCHAR, Types.INTEGER}
        );

        this.buyer = buyer;
    }

    public int getTradeID() {
        return tradeID;
    }

    public long getTimeListed() {
        return timeListed;
    }

    public void setTimeCompleted(long timeCompleted) {
        WorldShop.getDatabase().update("UPDATE trades SET time_completed = ? WHERE trade_id = ?;",
                new Object[]{timeCompleted, this.tradeID},
                new int[]{Types.BIGINT, Types.INTEGER});
    }

    public long getTimeCompleted() {
        return timeCompleted;
    }

}
