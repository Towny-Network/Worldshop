package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.ItemBuyerScreenListener;
import dev.onebiteaidan.worldshop.View.Screen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class ItemBuyerScreen extends Screen {

    private final Trade trade;

    public ItemBuyerScreen(Player player, Trade trade) {
        setPlayer(player);
        this.trade = trade;

        Component title = text("Trade Viewer")
                .color(NamedTextColor.DARK_GRAY);

        setInventory(WorldShop.getPlugin(WorldShop.class).getServer().createInventory(this, 27, title)); //Todo: make the title of the store change based on nation it's in
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Item Being Sold
        ItemStack beingSold = trade.getItemOffered();
        getInventory().setItem(2, beingSold);

        // Item Being Sold Marker
        String beingSoldMarkerURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIyMWRhNDQxOGJkM2JmYjQyZWI2NGQyYWI0MjljNjFkZWNiOGY0YmY3ZDRjZmI3N2ExNjJiZTNkY2IwYjkyNyJ9fX0=";

        TextComponent beingSoldMarkerTitle = text("You are selling them item!");
        ArrayList<TextComponent> beingSoldLore = new ArrayList<>();
        beingSoldLore.add(text("This will go to the player who buys the item from you."));
        beingSoldLore.add(text("In return, you will receive the payment item(s) you specified."));

        ItemStack beingSoldMarker = Utils.createButtonItem(Utils.createSkull(beingSoldMarkerURL), beingSoldMarkerTitle, beingSoldLore);
        getInventory().setItem(11, beingSoldMarker);


        // Payment Item
        ItemStack paymentItem = trade.getItemRequested();
        getInventory().setItem(6, paymentItem);

        // Payment Item Marker
        String paymentItemMarkerURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjIyMWRhNDQxOGJkM2JmYjQyZWI2NGQyYWI0MjljNjFkZWNiOGY0YmY3ZDRjZmI3N2ExNjJiZTNkY2IwYjkyNyJ9fX0=";

        TextComponent paymentItemMarkerTitle = text("This is the item you requested as payment!");
        ArrayList<TextComponent> paymentItemLore = new ArrayList<>();
        paymentItemLore.add(text("This will go to you after another player buys from you."));
        paymentItemLore.add(text("In return, the buyer will receive the item you are selling."));

        ItemStack paymentItemMarker = Utils.createButtonItem(Utils.createSkull(paymentItemMarkerURL), paymentItemMarkerTitle, paymentItemLore);
        getInventory().setItem(15, paymentItemMarker);


        // Back Button
        TextComponent backButtonTitle = text("Go back")
                .color(NamedTextColor.RED);

        ItemStack backButton = Utils.createButtonItem(Material.RED_CONCRETE_POWDER, backButtonTitle, null);
        getInventory().setItem(22, backButton);
    }

    @Override
    public void update() {
        // Do nothing
    }

    public Trade getTrade() {
        return this.trade;
    }
}
