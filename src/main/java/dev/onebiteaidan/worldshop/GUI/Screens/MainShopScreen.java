package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.PageableMenu;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class MainShopScreen extends PageableMenu {

    private final Player player;

    public MainShopScreen(Player player) {
        super(text("WorldShop").color(NamedTextColor.DARK_GRAY), 54);
        this.player = player;
        initializeScreen();
    }

    private void initializeScreen() {
        // Prev Page Button
        TextComponent prevPageTitle = Component.text("Previous Page")
                .color(NamedTextColor.RED);

        if (!isPageValid(getAllDisplayItems(), getCurrentPage() - 1, 45)) {
            // Add Strikethrough to prevPageTitle if there is no previous page to go to.
            prevPageTitle = prevPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack prevPage = Utils.createButtonItem(Material.ARROW, prevPageTitle, null);
        setButton(45, new Button(prevPage, (InventoryClickEvent event) -> {
            if (isPageValid(getAllDisplayItems(), getCurrentPage() - 1, 45)) {
                previousPage();
            }
        }));


        // Next Page Button
        TextComponent nextPageTitle = Component.text("Next Page")
                .color(NamedTextColor.RED);

        if (isPageValid(getAllDisplayItems(), getCurrentPage() + 1, 45)) {
            // Add Strikethrough to nextPageTitle if there is no next page to go to.
            nextPageTitle = nextPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack nextPage = Utils.createButtonItem(Material.ARROW, nextPageTitle, null);
        setButton(53, new Button(nextPage, (InventoryClickEvent event) -> {
            if (isPageValid(getAllDisplayItems(), getCurrentPage() + 1, 45)) {
                nextPage();
            }
        }));


        // View Trades Button
        TextComponent viewTradesTitle = Component.text("Manage Trades")
                .color(NamedTextColor.YELLOW);

        List<TextComponent> viewTradesLore = new ArrayList<>();
        viewTradesLore.add(Component.text("Click to view your listings as well as to claim your items from completed trades!"));

        ItemStack viewTrades = Utils.createButtonItem(Material.CHEST, viewTradesTitle, viewTradesLore);
        setButton(51, new Button(viewTrades, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new TradeManagementScreen());
        }));


        // Sell Item Button
        TextComponent sellItemTitle = Component.text("Sell Item")
                .color(NamedTextColor.GREEN);

        List<TextComponent> sellItemLore = new ArrayList<>();
        sellItemLore.add(Component.text("Click to sell an item! You can view your currently listed items in Manage Trades!"));

        ItemStack sell = Utils.createButtonItem(Material.WRITABLE_BOOK, sellItemTitle, sellItemLore);
        setButton(50, new Button(sell, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new ItemSellerScreen());
        }));


        // Player head with stats
        TextComponent statsHeadTitle = Component.text(player.getName() + "'s Stats")
                .color(NamedTextColor.DARK_AQUA);

        ItemStack statsHead = Utils.createButtonItem(Utils.createSkull(player), statsHeadTitle, null);
        setButton(49, new Button(statsHead, (InventoryClickEvent event) -> {
            // Do Nothing
        }));


        // Filter Trades Button
        TextComponent filterTradesTitle = Component.text("Filter")
                .color(NamedTextColor.BLUE)
                .decorate(TextDecoration.STRIKETHROUGH);

        List<TextComponent> filterLore = new ArrayList<TextComponent>();
        filterLore.add(Component.text("This feature has not been implemented yet!").color(NamedTextColor.RED));

        ItemStack filterTrades = Utils.createButtonItem(Material.HOPPER, filterTradesTitle, filterLore);
        setButton(48, new Button(filterTrades, (event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("This feature has not been implemented yet");
            player.closeInventory();
            // Not implemented
        }));


        // Search Items
        TextComponent searchItemsTitle = Component.text("Search")
                .color(NamedTextColor.AQUA)
                .decorate(TextDecoration.STRIKETHROUGH);

        List<TextComponent> searchLore = new ArrayList<TextComponent>();
        searchLore.add(Component.text("This feature has not been implemented yet!").color(NamedTextColor.RED));

        ItemStack searchItems = Utils.createButtonItem(Material.SPYGLASS, searchItemsTitle, searchLore);
        setButton(47, new Button(searchItems, (event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("This feature has not been implemented yet");
            player.closeInventory();
            // Not implemented
        }));


        // Add stored trades for the first page
        List<ItemStack> items = getPageItems(getAllDisplayItems(player), getCurrentPage(), 45);

        for (int i = 0; i < items.size(); i++) {
            setButton(i, new Button(items.get(i), (event) -> {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta()) {
                    ItemMeta meta = clickedItem.getItemMeta();

                    Integer tradeID = meta.getPersistentDataContainer().get(new NamespacedKey(WorldShop.getInstance(), "trade_id"), PersistentDataType.INTEGER);

                    if (tradeID != null) {
                        Trade trade = WorldShop.getStoreManager().getTrade(tradeID);
                        if (trade != null) {
                            WorldShop.getMenuManager().openMenu(player, new ItemBuyerScreen(trade, player));
                        } else {
                            Logger.severe("TRADE WAS NULL WHEN OPENING THE BUY SCREEN. PLAYER: " + event.getWhoClicked().getName());
                        }
                    }
                }
            }));
        }
    }

    public void openScreen(int page) {
        setCurrentPage(page);
        initializeScreen();

        open(player);
    }

    /**
     * Gets all display items from the store manager for display in the main shop page
     * @return returns and arraylist of the display item stacks
     */
    private List<ItemStack> getAllDisplayItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Trade trade : WorldShop.getStoreManager().getTrades()) {
            items.add(trade.generateDisplayItem());
        }

        return items;
    }

    /**
     * Gets all display items from the store manager for display in the main shop page.
     * Only returns trades with the OPEN TradeStatus.
     * @param player Removes any trades with this player as the seller
     * @return returns and arraylist of the display item stacks
     */
    private List<ItemStack> getAllDisplayItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        for (Trade trade : WorldShop.getStoreManager().getTrades()) {
            if (!trade.getSeller().equals(player)) {
                if (trade.getTradeStatus() == TradeStatus.OPEN) {
                    items.add(trade.generateDisplayItem());
                }
            }
        }

        return items;
    }
}
