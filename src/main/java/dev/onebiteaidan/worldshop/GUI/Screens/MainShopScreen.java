package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.DisplayItem;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.GUI.GUI;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.PageableScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class MainShopScreen extends PageableScreen {

    public MainShopScreen(Player player) {
        setPlayer(player);

        TextComponent title = text("WorldShop")
                .color(NamedTextColor.DARK_GRAY);

        GUI gui = new GUI(54, title, "MainShopScreen");
        plugin.getServer().getPluginManager().registerEvents(gui, plugin); //Todo: make the title of the store change based on nation it's in

        setGUI(gui);
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Prev Page Button
        TextComponent prevPageTitle = Component.text("Previous Page")
                .color(NamedTextColor.RED);

        if (!isPageValid(getAllDisplayItems(), getCurrentPage() - 1, 45)) {
            // Add Strikethrough to prevPageTitle if there is no previous page to go to.
            prevPageTitle = prevPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack prevPage = Utils.createButtonItem(Material.ARROW, prevPageTitle, null);
        Button prevPageButton = new Button(prevPage, () -> {
            player.sendMessage("Opening previous page");
//            previousPage();
        });
        gui.addButton(45, prevPageButton);


        // Next Page Button
        TextComponent nextPageTitle = Component.text("Next Page")
                .color(NamedTextColor.RED);

        if (isPageValid(getAllDisplayItems(), getCurrentPage() + 1, 45)) {
            // Add Strikethrough to nextPageTitle if there is no next page to go to.
            nextPageTitle = nextPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack nextPage = Utils.createButtonItem(Material.ARROW, nextPageTitle, null);
        Button nextPageButton = new Button(nextPage, () -> {
            player.sendMessage("Opening next page");
//            nextPage();
        });
        gui.addButton(53, nextPageButton);


        // View Trades Button
        TextComponent viewTradesTitle = Component.text("Manage Trades")
                .color(NamedTextColor.YELLOW);

        ItemStack viewTrades = Utils.createButtonItem(Material.CHEST, viewTradesTitle, null);
        Button viewTradesButton = new Button(viewTrades, () -> {
            player.sendMessage("Opening trade management screen");
//            new TradeManagementScreen(player).openScreen();
        });
        gui.addButton(51, viewTradesButton);


        // Sell Item Button
        TextComponent sellItemTitle = Component.text("Sell Item")
                .color(NamedTextColor.GREEN);

        ItemStack sell = Utils.createButtonItem(Material.WRITABLE_BOOK, sellItemTitle, null);
        Button sellButton = new Button(sell, () -> {
           player.sendMessage("Opening sell menu");
            new ItemSellerScreen(player).openScreen();
        });
        gui.addButton(50, sellButton);


        // Player head with stats
        TextComponent statsHeadTitle = Component.text(player.getName() + "'s Stats")
                .color(NamedTextColor.DARK_AQUA);

        ItemStack statsHead = Utils.createButtonItem(Utils.createSkull(player), statsHeadTitle, null);
        Button statsHeadButton = new Button(statsHead, () -> {
            player.sendMessage("Clicked on stats head");
            // Do nothing else
        });
        gui.addButton(49, statsHeadButton);


        // Filter Trades Button
        TextComponent filterTradesTitle = Component.text("Filter")
                .color(NamedTextColor.BLUE);

        ItemStack filterTrades = Utils.createButtonItem(Material.HOPPER, filterTradesTitle, null);
        Button filterTradesButton = new Button(filterTrades, (event) -> {
            player.sendMessage("Clicked on trade filter");
            // Not implemented
        });
        gui.addButton(48, filterTradesButton);


        // Search Items
        TextComponent searchItemsTitle = Component.text("Search")
                .color(NamedTextColor.AQUA);

        ItemStack searchItems = Utils.createButtonItem(Material.SPYGLASS, searchItemsTitle, null);
        Button searchItemsButton = new Button(searchItems, (event) -> {
            player.sendMessage("Clicked on search items");
            // Not implemented
        });
        gui.addButton(47, searchItemsButton);


        // Add stored trades for the first page
        List<ItemStack> items = getPageItems(getAllDisplayItems(player), getCurrentPage(), 45);
        for (int i = 0; i < items.size(); i++) {
            Button itemButton = new Button(items.get(i), (event) -> {
                // Open new ItemBuyerScreen with player and trade from item clicked on.
                if (event.getCurrentItem() != null) {
                    if (event.getCurrentItem() instanceof DisplayItem displayItem) {
                        Trade trade = WorldShop.getStoreManager().getTrade(displayItem.getTradeID());
                        if (trade != null) {
                            new ItemBuyerScreen(player, trade).openScreen();
                        } else {
                            Logger.severe("TRADE WAS NULL WHEN OPENING THE BUY SCREEN. PLAYER: " + event.getWhoClicked().getName());
                        }
                    }
                }
            });
            gui.addButton(i, itemButton);
        }
    }

    public void openScreen(int page) {
        setCurrentPage(page);
        initializeScreen();

        player.openInventory(getInventory());
    }

    @Override
    public void update() {
        // To update this page, player reopens the GUI.
        this.player.openInventory(getInventory());
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
     * Gets all display items from the store manager for display in the main shop page
     * @param player Removes any trades with this player as the seller
     * @return returns and arraylist of the display item stacks
     */
    private List<ItemStack> getAllDisplayItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        for (Trade trade : WorldShop.getStoreManager().getTrades()) {
            if (!trade.getSeller().equals(player)) {
                items.add(trade.generateDisplayItem());
            }
        }

        return items;
    }
}
