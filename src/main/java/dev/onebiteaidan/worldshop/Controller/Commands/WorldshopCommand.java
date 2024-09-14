package dev.onebiteaidan.worldshop.Controller.Commands;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldshopCommand implements CommandExecutor {

    //Fixme: THIS COMMAND WILL EVENTUALLY BE REMOVED AND REPLACED WITH THE NATION SHOP SYSTEM

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            WorldShop.getStoreManager().openShop(player, 1);
        }
        return false;
    }
}
