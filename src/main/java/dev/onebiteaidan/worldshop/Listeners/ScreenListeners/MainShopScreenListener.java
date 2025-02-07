package dev.onebiteaidan.worldshop.Listeners.ScreenListeners;

import dev.onebiteaidan.worldshop.GUI.Screens.ItemBuyerScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.ItemSellerScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.GUI.Screens.TradeManagementScreen;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.DisplayItem;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Listeners.ScreenListener;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainShopScreenListener extends ScreenListener {

    @Override
    @EventHandler
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
                        if (e.getCurrentItem() instanceof DisplayItem) {
                            DisplayItem displayItem = (DisplayItem) e.getCurrentItem();
                            Trade trade = WorldShop.getStoreManager().getTrade(displayItem.getTradeID());
                            if (trade != null) {
                                new ItemBuyerScreen(holder.getPlayer(), trade).openScreen();
                            } else {
                                Logger.severe("TRADE WAS NULL WHEN OPENING THE BUY SCREEN. PLAYER: " + e.getWhoClicked().getName());
                            }
                        }
                    }
                    break;
            }
        }
    }
}
