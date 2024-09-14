package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCompletedTradesScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class ViewCompletedTradesScreenListener extends ScreenListener {

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ViewCompletedTradesScreen) {
            ViewCompletedTradesScreen holder = (ViewCompletedTradesScreen) event.getInventory().getHolder();

            event.setCancelled(true);

            if (event.getRawSlot() > 27) {
                switch (event.getRawSlot()) {
                    case 31: // Back Button
                        new TradeManagementScreen(holder.getPlayer()).openScreen();
                        break;

                    case 33: // Next Page
                        holder.nextPage();
                        break;

                    case 29: // Prev Page
                        holder.previousPage();
                        break;

                    default:
                        break;
                }
            } else {

                if (holder.getPlayer().getInventory().firstEmpty() != -1) {
                    try {

                        ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                        NamespacedKey key = new NamespacedKey(WorldShop.getPlugin(WorldShop.class), "tradeID");


                        Integer tradeID = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

                        if (tradeID == null) {
                            WorldShop.getPlugin(WorldShop.class).getLogger().severe("UNABLE TO GET TRADEID FROM PERSISTENT DATA CONTAINER");
                            return;
                        }

                        StoreManager.getInstance().pickupCompletedTrade(holder.getPlayer(), tradeID);
                    } catch (NullPointerException e) {
                        Utils.logStacktrace(e);
                    }



                } else {
                    holder.getPlayer().sendMessage(text("There is not enough space in your inventory to collect the item! Please make some space!").color(NamedTextColor.RED));
                }
            }
        }
    }
}
