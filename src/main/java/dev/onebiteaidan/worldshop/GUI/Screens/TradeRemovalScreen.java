package dev.onebiteaidan.worldshop.GUI.Screens;

import dev.onebiteaidan.worldshop.DataManagement.StoreDataTypes.Trade;
import dev.onebiteaidan.worldshop.GUI.AbstractMenu;
import dev.onebiteaidan.worldshop.GUI.Button;
import dev.onebiteaidan.worldshop.Utils.Utils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

public class TradeRemovalScreen extends AbstractMenu {

    private final Trade trade;

    public TradeRemovalScreen(Trade trade) {
        super(text("Delete this trade?"), 18);
        this.trade = trade;

        initializeScreen();
    }

    private void initializeScreen() {
        // Yes Delete Button
        String yesButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";

        TextComponent yesButtonTitle = text("Yes");

        ItemStack yesButton = Utils.createButtonItem(Utils.createSkull(yesButtonURL), yesButtonTitle, null);
        setButton(11, new Button(yesButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked yes");
        }));


        // No Don't Delete Button
        String noButtonURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=";

        TextComponent noButtonTitle = text("No");

        ItemStack noButton = Utils.createButtonItem(Utils.createSkull(noButtonURL), noButtonTitle, null);
        setButton(15, new Button(noButton, (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("Clicked no");
        }));

        // Trade Display Item
        inventory.setItem(4, this.trade.generateDisplayItem());
    }

    public Trade getTrade() {
        return this.trade;
    }
}
