package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.DisplayItem;
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

                    //todo: May be useful here to add some prompts that encourage the user to make a support ticket (and also let them know something went wrong).

                    if (!(event.getCurrentItem() instanceof DisplayItem)) {
                        Logger.warning("Item gotten from ViewCompletedTradesScreen was not an instance of DisplayItem!");
                        return;
                    }

                    DisplayItem item = (DisplayItem) event.getCurrentItem();

                    assert item != null;
                    if (item.getTradeID() == -1) {
                        Logger.warning("DisplayItem gotten in ViewCompletedTradesScreen had an invalid tradeID!");
                        return;
                    }

                    if (event.getClick().isLeftClick()) {
                        new TradeViewerScreen(holder.getPlayer(), WorldShop.getStoreManager().getTrade(item.getTradeID()));
                    } else if (event.getClick().isRightClick()) {
                        new TradeRemovalScreen(holder.getPlayer(), WorldShop.getStoreManager().getTrade(item.getTradeID()));
                    }

                } catch (NullPointerException e) {
                    Logger.logStacktrace(e);
                }
            }
        }
    }
}
