package dev.onebiteaidan.worldshop.GUI;

import dev.onebiteaidan.worldshop.WorldShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Screen implements InventoryHolder, Listener {
    protected JavaPlugin plugin = WorldShop.getPlugin(WorldShop.class);

    protected Player player;
    protected GUI gui;
    protected Inventory inventory;

    /**
     * Initialize Screen's inventory with items.
     */
    protected abstract void initializeScreen();

    /**
     * Opens the corresponding screen for this class's player.
     */
    public void openScreen() {
        this.player.openInventory(this.gui.getInventory());
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

    protected void setGUI(GUI gui) {
        this.gui = gui;
    }

    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

    public @NotNull Inventory getInventory() {
        return this.gui.getInventory();
    }
}
