package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.View.Screens.TradeRemovalScreen;
import dev.onebiteaidan.worldshop.View.Screens.TradeViewerScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCurrentListingsScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ViewCurrentListingsScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof ViewCurrentListingsScreen) {
            ViewCurrentListingsScreen holder = (ViewCurrentListingsScreen) event.getInventory().getHolder();

            event.setCancelled(true);

            if (event.getRawSlot() > 27) {
                switch(event.getRawSlot()) {
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

                // Get trade ID

                try {

                    ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                    NamespacedKey key = new NamespacedKey(WorldShop.getPlugin(WorldShop.class), "tradeID");


                    Integer tradeID = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

                    if (tradeID == null) {
                        WorldShop.getPlugin(WorldShop.class).getLogger().severe("UNABLE TO GET TRADE ID FROM PERSISTENT DATA CONTAINER");
                        return;
                    }

                    if (event.getClick().isLeftClick()) {
                        new TradeViewerScreen(holder.getPlayer(), StoreManager.getInstance().getTrade(tradeID));
                    } else if (event.getClick().isRightClick()) {
                        new TradeRemovalScreen(holder.getPlayer(), StoreManager.getInstance().getTrade(tradeID));
                    }

                } catch (NullPointerException e) {
                    Logger.logStacktrace(e);
                }
            }
        }
    }
}
