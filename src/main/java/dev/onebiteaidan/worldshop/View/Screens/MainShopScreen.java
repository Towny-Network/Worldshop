package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Controller.StoreManager;
import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.View.PageableScreen;
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

        setInventory(plugin.getServer().createInventory(this, 54, title)); //Todo: make the title of the store change based on nation it's in
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
        getInventory().setItem(45, prevPage);


        // Next Page Button
        TextComponent nextPageTitle = Component.text("Next Page")
                .color(NamedTextColor.RED);

        if (isPageValid(getAllDisplayItems(), getCurrentPage() + 1, 45)) {
            // Add Strikethrough to nextPageTitle if there is no previous page to go to.
            nextPageTitle = nextPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack nextPage = Utils.createButtonItem(Material.ARROW, nextPageTitle, null);
        getInventory().setItem(53, nextPage);


        // View Trades Button
        TextComponent viewTradesTitle = Component.text("Manage Trades")
                .color(NamedTextColor.YELLOW);

        ItemStack viewTradesButton = Utils.createButtonItem(Material.CHEST, viewTradesTitle, null);
        getInventory().setItem(51, viewTradesButton);


        // Sell Item Button
        TextComponent sellItemTitle = Component.text("Sell Item")
                .color(NamedTextColor.GREEN);

        ItemStack sellButton = Utils.createButtonItem(Material.WRITABLE_BOOK, sellItemTitle, null);
        getInventory().setItem(50, sellButton);


        // Player head with stats
        TextComponent statsHeadTitle = Component.text(player.getName() + "'s Stats")
                .color(NamedTextColor.DARK_AQUA);

        ItemStack statsHead = Utils.createButtonItem(Utils.createSkull(player), statsHeadTitle, null);
        getInventory().setItem(49, statsHead);


        // Sort Trades Button
        TextComponent sortTradesTitle = Component.text("Sort")
                .color(NamedTextColor.BLUE);

        ItemStack sortTradesButton = Utils.createButtonItem(Material.HOPPER, sortTradesTitle, null);
        getInventory().setItem(48, sortTradesButton);


        // Search Items
        TextComponent searchItemsTitle = Component.text("Search")
                .color(NamedTextColor.AQUA);

        ItemStack searchItemsButton = Utils.createButtonItem(Material.SPYGLASS, searchItemsTitle, null);
        getInventory().setItem(47, searchItemsButton);


        // Add stored trades for the first page
        List<ItemStack> items = getPageItems(getAllDisplayItems(player), getCurrentPage(), 45);
        for (int i = 0; i < items.size(); i++) {
            // Cannot use gui.addItem(item) here because it combines identical listings
            getInventory().setItem(i, items.get(i));
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

        for (Trade trade : StoreManager.getInstance().getTrades()) {
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

        for (Trade trade : StoreManager.getInstance().getTrades()) {
            if (!trade.getSeller().equals(player)) {
                items.add(trade.generateDisplayItem());
            }
        }

        return items;
    }
}
