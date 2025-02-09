package dev.onebiteaidan.worldshop.GUI;

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
    static String cannotConfirmURL = "http://textures.minecraft.net/texture/beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7";
    static TextComponent cannotConfirmTitle = text("You cannot confirm until you have put in a sell item and a price item!");
    public static final ItemStack cannotConfirmButtonItem = Utils.createButtonItem(Utils.createSkull(cannotConfirmURL), cannotConfirmTitle, null);

    // Define the YELLOW Checkmark
    static String canConfirmURL = "http://textures.minecraft.net/texture/eef425b4db7d62b200e89c013e421a9e110bfb27f2d8b9f5884d10104d00f4f4";
    static TextComponent canConfirmTitle = text("Click to confirm!");
    public static final ItemStack canConfirmButtonItem = Utils.createButtonItem(Utils.createSkull(canConfirmURL), canConfirmTitle, null);

    // Define the GREEN Checkmark
    static String doubleConfirmURL = "http://textures.minecraft.net/texture/a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6";
    static TextComponent doubleConfirmTitle = text("Are you sure?");
    public static final ItemStack doubleConfirmButtonItem = Utils.createButtonItem(Utils.createSkull(doubleConfirmURL), doubleConfirmTitle, null);

    // Dividers
    public static final ItemStack divider = Utils.createButtonItem(Material.GRAY_STAINED_GLASS_PANE, null, null);
}
