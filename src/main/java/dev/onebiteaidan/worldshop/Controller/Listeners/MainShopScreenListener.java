package dev.onebiteaidan.worldshop.Controller.Listeners;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.View.ScreenListener;
import dev.onebiteaidan.worldshop.View.Screens.*;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainShopScreenListener extends ScreenListener {

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof MainShopScreen) {
            MainShopScreen holder = (MainShopScreen) e.getInventory().getHolder();

            e.setCancelled(true);

            switch (e.getRawSlot()) {
                case 45: // Previous Page
                    holder.previousPage();
                    break;

                case 47: // Search
                    break;

                case 48: // Filter
                    break;

                case 49: // Stats Head Display
                    break;

                case 50: // Sell Item
                    new ItemSellerScreen(holder.getPlayer()).openScreen();
                    break;

                case 51: // View Trades
                    new TradeManagementScreen(holder.getPlayer()).openScreen();
                    break;

                case 53: // Next Page
                    holder.nextPage();
                    break;

                default: // Open buy screen for the item that was clicked on

                    // Make sure the player isn't clicking an item in their hotbar or inventory
                    if (e.getRawSlot() >= 54 && e.getRawSlot() <= 89) {
                        break;
                    }

                    // Open the buy screen
                    if (e.getCurrentItem() != null) {
                        Trade trade = StoreManager.getInstance().getTradeFromDisplayItem(e.getCurrentItem());
                        new ItemBuyerScreen(holder.getPlayer(), trade).openScreen();
                    }

                    break;

            }
        }
    }
}
