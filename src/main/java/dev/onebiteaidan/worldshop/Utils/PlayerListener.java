package dev.onebiteaidan.worldshop.Utils;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void updatePlayerTable(PlayerJoinEvent e) {
        WorldShop.getPlayerManager().addNewPlayer(e.getPlayer());
    }
}
