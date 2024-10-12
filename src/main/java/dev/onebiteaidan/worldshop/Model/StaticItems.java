package dev.onebiteaidan.worldshop.Model;

import dev.onebiteaidan.worldshop.Utils.Utils;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static net.kyori.adventure.text.Component.text;

/*
Static items are items that remain the same across all screens.
These items can often be modified in the config.yml
 */
public class StaticItems {

    // Define emptySellItem placeholder
    static TextComponent emptySellItemTitle = text("Left click the item in your inventory you want to sell!");
    public static final ItemStack emptySellItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptySellItemTitle, null);

    // Define emptyPriceItem placeholder
    static TextComponent emptyPriceItemTitle = text("Right click the item in your inventory you want to receive in trade!");
    public static final ItemStack emptyPriceItem = Utils.createButtonItem(Material.RED_STAINED_GLASS_PANE, emptyPriceItemTitle, null);

    // Define RED X
    static String cannotConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=";
    static TextComponent cannotConfirmTitle = text("You cannot confirm until you have put in a sell item and a price item!");
    public static final ItemStack cannotConfirmButton = Utils.createButtonItem(Utils.createSkull(cannotConfirmURL), cannotConfirmTitle, null);

    // Define the YELLOW Checkmark
    static String canConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVmNDI1YjRkYjdkNjJiMjAwZTg5YzAxM2U0MjFhOWUxMTBiZmIyN2YyZDhiOWY1ODg0ZDEwMTA0ZDAwZjRmNCJ9fX0=";
    static TextComponent canConfirmTitle = text("Click to confirm!");
    public static final ItemStack canConfirmButton = Utils.createButtonItem(Utils.createSkull(canConfirmURL), canConfirmTitle, null);

    // Define the GREEN Checkmark
    static String doubleConfirmURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
    static TextComponent doubleConfirmTitle = text("Are you sure?");
    public static final ItemStack doubleConfirmButton = Utils.createButtonItem(Utils.createSkull(doubleConfirmURL), doubleConfirmTitle, null);

    // Dividers
    public static final ItemStack divider = Utils.createButtonItem(Material.GRAY_STAINED_GLASS_PANE, null, null);
}
