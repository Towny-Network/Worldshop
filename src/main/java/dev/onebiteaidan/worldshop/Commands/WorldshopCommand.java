package dev.onebiteaidan.worldshop.Commands;

import dev.onebiteaidan.worldshop.GUI.MenuManager;
import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
