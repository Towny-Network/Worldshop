package dev.onebiteaidan.worldshop.View.Screens;

import dev.onebiteaidan.worldshop.Model.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.Utils.Utils;
import dev.onebiteaidan.worldshop.Controller.Listeners.ScreenListeners.TradeRemovalScreenListener;
import dev.onebiteaidan.worldshop.View.Screen;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public class TradeRemovalScreen extends Screen {

    private final Trade trade;

    public TradeRemovalScreen(Player player, Trade trade) {
        setPlayer(player);
        this.trade = trade;

        TextComponent title = text("Delete this trade?");

        setInventory(plugin.getServer().createInventory(this, 18, title));
        initializeScreen();
    }

    @Override
    protected void initializeScreen() {
        // Yes Delete Button
        String yesButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";

        TextComponent yesButtonTitle = text("Yes");

        ItemStack yesButton = Utils.createButtonItem(Utils.createSkull(yesButtonURL), yesButtonTitle, null);
        getInventory().setItem(11, yesButton);

        // No Don't Delete Button
        String noButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=";

        TextComponent noButtonTitle = text("No");

        ItemStack noButton = Utils.createButtonItem(Utils.createSkull(noButtonURL), noButtonTitle, null);
        getInventory().setItem(15, noButton);


        // Trade Display Item
        getInventory().setItem(4, this.trade.generateDisplayItem());
    }

    public Trade getTrade() {
        return this.trade;
    }
}
