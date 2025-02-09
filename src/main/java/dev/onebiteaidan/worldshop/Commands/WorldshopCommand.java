package dev.onebiteaidan.worldshop.Commands;

import dev.onebiteaidan.worldshop.GUI.Screens.MainShopScreen;
import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WorldshopCommand implements CommandExecutor {

    //Fixme: THIS COMMAND WILL EVENTUALLY BE REMOVED AND REPLACED WITH THE NATION SHOP SYSTEM

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            new MainShopScreen(player).openScreen(1);

            System.out.println("Command run");

        } else {
            ArrayList<RegisteredListener> rls = InventoryClickEvent.getHandlerList().getRegisteredListeners(WorldShop.getPlugin(WorldShop.class));

            for (RegisteredListener rl: rls) {
                System.out.println(rl.getListener().toString());
            }
        }
        return false;
    }
}
