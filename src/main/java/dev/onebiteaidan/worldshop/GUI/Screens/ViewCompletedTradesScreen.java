package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Logger;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.GUI.PageableMenu;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta()) {
                    ItemMeta meta = clickedItem.getItemMeta();

                    Integer pickupID = meta.getPersistentDataContainer().get(new NamespacedKey(WorldShop.getInstance(), "pickup_id"), PersistentDataType.INTEGER);

                    System.out.println("Trying out pickupID");

                    if (pickupID != null) {
                        System.out.println("pickupID retrieved");
                        Pickup pickup = WorldShop.getStoreManager().getPickup(pickupID);
                        if (pickup != null) {
                            System.out.println("Pickup is not null");
                            // Insert item into inventory if possible
                            boolean success = Utils.fitItem(player, pickup.getItem());
                            System.out.println("Able to fit into the inventory: " + success);
                            if (success) {
                                player.sendMessage("Added your items to your inventory!");
                                // Mark pickup as complete
                                WorldShop.getStoreManager().withdrawPickup(pickupID);
                                // Keeps player on the same screen to collect more pickups
                                WorldShop.getMenuManager().openMenu(player, new ViewCompletedTradesScreen(player));
                            } else {
                                player.sendMessage("Make room in your inventory for the item!");
                            }

                        } else {
                            Logger.severe("PICKUP WAS NULL WHEN TRYING TO CLAIM ITEM FROM COMPLETED TRADES SCREEN. PLAYER: " + event.getWhoClicked().getName());
                        }
                    }
                }
            }));
        }
    }

    private List<ItemStack> getPickupDisplayItems(Player player, int page, int spaces) {
        // Get all pickups
        List<Pickup> pickups = WorldShop.getStoreManager().getPickups();

        // Filter trades by seller and withdrawn status
        pickups = pickups.stream()
                .filter(pickup -> pickup.getPlayer().equals((OfflinePlayer) player) && !pickup.isCollected())
                .collect(Collectors.toList());

        // Map each Pickup to a DisplayItem
        List<ItemStack> displayItems = pickups.stream()
                .map(Pickup::generateDisplayItem)
                .collect(Collectors.toList());

        // Return page display items.
        return getPageItems(displayItems, page, spaces);
    }
}
