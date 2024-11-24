package dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.DisplayItem;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.View.Screens.ViewCompletedTradesScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public class ViewCompletedTradesScreenListener extends ScreenListener {

    @Override
    @EventHandler
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

                        //todo: May be useful here to add some prompts that encourage the user to make a support ticket (and also let them know something went wrong).

                        if (!(event.getCurrentItem() instanceof DisplayItem)) {
                            Logger.warning("Item gotten from ViewCompletedTradesScreen was not an instance of DisplayItem!");
                            return;
                        }

                        DisplayItem item = (DisplayItem) event.getCurrentItem();

                        assert item != null;
                        if (item.getPickupID() == -1) {
                            Logger.warning("DisplayItem gotten in ViewCompletedTradesScreen had an invalid pickupID!");
                            return;
                        }

                        WorldShop.getStoreManager().withdrawPickup(item.getPickupID());

                    } catch (NullPointerException e) {
                        Logger.logStacktrace(e);
                    }

                } else {
                    holder.getPlayer().sendMessage(text("There is not enough space in your inventory to collect the item! Please make some space!").color(NamedTextColor.RED));
                }
            }
        }
    }
}
