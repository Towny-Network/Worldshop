package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Pickup;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.TradeStatus;
import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static dev.onebiteaidan.worldshop.GUI.StaticItems.*;
import static net.kyori.adventure.text.Component.text;

public class ItemBuyerScreen extends AbstractMenu {

    public enum ConfirmStatus {
        CANNOT_CONFIRM,     // Red
        CAN_CONFIRM,        // Yellow
        DOUBLE_CONFIRM      // Green
    }

    ConfirmStatus confirmStatus;
    private final Trade trade;
    private final Player player;

    public ItemBuyerScreen(Trade trade, Player player) {
        super(text("Item Buyer").color(NamedTextColor.DARK_GRAY), 9);
        confirmStatus = ConfirmStatus.CANNOT_CONFIRM;

        this.trade = trade;
        this.player = player;
        initializeScreen();
    }

    private void initializeScreen() {
        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        setButton(0, new Button(backButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));

        // Confirm Button
        if (player.getInventory().containsAtLeast(trade.getItemRequested(), trade.getItemRequested().getAmount())) {
            setYellow();
        } else {
            setRed();
        }



//        ItemStack confirmButton = cannotConfirmButtonItem;
//        setButton(2, new Button(confirmButton, (InventoryClickEvent event) -> {
//            Player player = (Player) event.getWhoClicked();
//            player.sendMessage("Clicked the confirm button");
//            player.closeInventory();
//
//            Inventory inven = event.getInventory();
//            ItemStack forSale = inven.getItem(4);
//            ItemStack wanted = inven.getItem(6);
//            int amountWanted = wanted.getAmount();
//
//            player.getInventory().removeItem(wanted);
//
//            WorldShop.getStoreManager().completeTrade(trade, player);
//        }));


        // Item You're Buying
        ItemStack beingSold = trade.getItemOffered();
        setButton(4, new Button(beingSold, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked item that is being sold!");
        }));


        // Buying Marker
        ItemStack leftArrow = Utils.createSkull("http://textures.minecraft.net/texture/81c96a5c3d13c3199183e1bc7f086f54ca2a6527126303ac8e25d63e16b64ccf");
        TextComponent leftArrowTitle = text("You're buying this item!!");
        setButton(5, new Button(Utils.createButtonItem(leftArrow, leftArrowTitle, null), (InventoryClickEvent event) -> {
        }));


        // Selling Marker
        ItemStack rightArrow = Utils.createSkull("http://textures.minecraft.net/texture/333ae8de7ed079e38d2c82dd42b74cfcbd94b3480348dbb5ecd93da8b81015e3");
        TextComponent rightArrowTitle = text("You're paying this item!");
        setButton(6, new Button(Utils.createButtonItem(rightArrow, rightArrowTitle, null), (InventoryClickEvent event) -> {
        }));


        // Payment Item
        ItemStack paymentItem = trade.getItemRequested();
        setButton(7, new Button(paymentItem, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked the payment item!");
        }));


    }

    public Trade getTrade() {
        return this.trade;
    }

    private void setRed() {
        this.confirmStatus = ConfirmStatus.CANNOT_CONFIRM;
        setButton(2, new Button(cannotConfirmButtonItem, (InventoryClickEvent event) -> {
            // Do nothing
        }));
    }

    private void setYellow() {
        this.confirmStatus = ConfirmStatus.CAN_CONFIRM;
        setButton(2, new Button(canConfirmButtonItem, (InventoryClickEvent event) -> {
            setGreen();
        }));
    }

    private void setGreen() {
        this.confirmStatus = ConfirmStatus.DOUBLE_CONFIRM;
        setButton(2, new Button(doubleConfirmButtonItem, (InventoryClickEvent event) -> {
            ItemStack inReturn = inventory.getItem(7);

            Player player = (Player) event.getWhoClicked();

            // Remove first occurrence of a repeat item stack in the players inventory
            if (player.getInventory().containsAtLeast(inReturn, trade.getItemRequested().getAmount())) {
                Utils.removeNumItems(player, trade.getItemRequested(), trade.getItemRequested().getAmount());
                System.out.println("REMOVED EM");
            } else {
                WorldShop.getPlugin(WorldShop.class).getLogger().severe("Player attempted to buy an item without the price item being in their inventory");
                return;
            }

            System.out.println("GAJBFEAKJBFJKDF");

            WorldShop.getStoreManager().completeTrade(trade, player);

            // Brings the player back to the main page of the store.
            WorldShop.getMenuManager().openMenu(player, new MainShopScreen(player));
        }));
    }
}
