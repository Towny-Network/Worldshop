package dev.onebiteaidan.worldshop.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.GUI.GUI;
import dev.onebiteaidan.worldshop.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.GUI.Screens.ItemSellerScreen;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemSellerScreenListener extends ScreenListener {
    @Override
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof GUI holder) {
            if (holder.getID().equals("ItemSellerScreen")) {
                event.setCancelled(true);

            /*
            Handling the special case for adding items to sell / set price.
             */
                // Check that the item is being clicked from inside the player's inventory
                if (event.getClickedInventory() instanceof ItemSellerScreen) {
                    return;
                }

                if (event.getCurrentItem() != null) {
                    // Determine if it's a right or left click
                    if (event.getClick().isLeftClick()) { // Set the item we are selling
                        holder.setSellItem(event.getCurrentItem());

                    } else if (event.getClick().isRightClick()) { // Set the item we want to receive
                        holder.setPriceItem(event.getCurrentItem());
                    }
                }
            }
        }
    }
}
