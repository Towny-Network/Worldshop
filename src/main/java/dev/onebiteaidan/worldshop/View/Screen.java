package dev.onebiteaidan.worldshop.View;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Screen implements InventoryHolder {
    protected JavaPlugin plugin = WorldShop.getPlugin(WorldShop.class);

    protected Player player;
    protected Inventory inventory;

    /**
     * Initialize a listener for the Screen.
     */
    protected void registerListener(ScreenListener screenListener) {
        Bukkit.getPluginManager().registerEvents(screenListener, WorldShop.getPlugin(WorldShop.class));
    }

    /**
     * Initialize Screen's inventory with items.
     */
    protected abstract void initializeScreen();


    /**
     * Opens the corresponding screen for this class's player.
     */
    public void openScreen() {
        this.player.openInventory(this.inventory);
    }

    /**
     * Updates/refreshes the inventory a player has open.
     * Used for when changes happen in the WorldShop that need to be reflected on the player's screen.
     * Default behavior is to reopen the inventory the player is currently viewing.
     */
    public void update() {
        // To update this page, player reopens the GUI.
        openScreen();
    }

    protected void setPlayer(Player player) {
        this.player = player;
    }

    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
