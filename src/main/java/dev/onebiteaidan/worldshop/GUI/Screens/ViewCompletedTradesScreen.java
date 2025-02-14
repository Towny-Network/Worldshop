package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.DisplayItem;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.PageableMenu;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class ViewCompletedTradesScreen extends PageableMenu {

    private final Player player;

    public ViewCompletedTradesScreen(Player player) {
        super(text("Completed Trades"), 36);
        this.player = player;
        initializeScreen();
    }

    @Override
    public void openScreen(int page) {
        setCurrentPage(page);
        initializeScreen();
        open(player);
    }


    private void initializeScreen() {
        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(31, new Button(backButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new TradeManagementScreen());
            player.sendMessage("Clicked the back button");
        }));


        // Prev Page Button
        TextComponent prevPageTitle = text("Previous Page")
                .color(NamedTextColor.RED);

        if (isPageValid(getPickupDisplayItems(player, getCurrentPage(), 27), getCurrentPage() - 1, 27)) {
            // Add Strikethrough to prevPageTitle if there is no previous page to go to.
            prevPageTitle = prevPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack prevPage = Utils.createButtonItem(Material.ARROW, prevPageTitle, null);
        setButton(29, new Button(prevPage, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            if (isPageValid(getPickupDisplayItems(player, getCurrentPage() - 1, 27), getCurrentPage() - 1, 27)) {
                previousPage();
            }
            player.sendMessage("Clicked prev page!");
        }));


        // Next Page Button
        TextComponent nextPageTitle = text("Next Page")
                .color(NamedTextColor.RED);

        if (isPageValid(getPickupDisplayItems(player, getCurrentPage(), 27), getCurrentPage() + 1, 27)) {
            // Add Strikethrough to nextPageTitle if there is no next page to go to.
            nextPageTitle = nextPageTitle.decorate(TextDecoration.STRIKETHROUGH);
        }

        ItemStack nextPage = Utils.createButtonItem(Material.ARROW, nextPageTitle, null);
        setButton(33, new Button(nextPage, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            if (isPageValid(getPickupDisplayItems(player, getCurrentPage(), 27), getCurrentPage() + 1, 27)) {
                nextPage();
            }
            player.sendMessage("Clicked next page item!");
        }));


        // Populate remaining slots w/ completed trades posted by player
        int count = 0;
        for (ItemStack item : getPageItems(getPickupDisplayItems(player, getCurrentPage(), 27), getCurrentPage(), 27)) {
            setButton(count, new Button(item, (InventoryClickEvent event) -> {
                System.out.println("Adding " + item.getType() + " x" + item.getAmount() + " to you inventory!");
            }));
        }
    }

    private List<DisplayItem> getPickupDisplayItems(Player player, int page, int spaces) {
        // Get all pickups
        List<Pickup> pickups = WorldShop.getStoreManager().getPickups();

        // Filter trades by seller and withdrawn status
        pickups = pickups.stream()
                .filter(pickup -> pickup.getPlayer().equals((OfflinePlayer) player) && !pickup.isCollected())
                .collect(Collectors.toList());

        // Map each Pickup to a DisplayItem
        List<DisplayItem> displayItems = pickups.stream()
                .map(Pickup::generateDisplayItem)
                .collect(Collectors.toList());

        // Return page display items.
        return getPageItems(displayItems, page, spaces);
    }
}
