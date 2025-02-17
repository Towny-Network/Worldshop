package dev.onebiteaidan.worldshop.Commands;

import dev.onebiteaidan.worldshop.GUI.MenuManager;
import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredListener;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;

public class WorldshopCommand implements CommandExecutor {

    private final MenuManager menuManager;

    public WorldshopCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            menuManager.openMenu(player, new MainShopScreen(player));
        }

        return false;
    }
}
