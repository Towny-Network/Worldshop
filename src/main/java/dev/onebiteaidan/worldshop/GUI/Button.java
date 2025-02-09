package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Button {
    /* Itemstack isn't mean for inheritance so we can't directly extend it.
    * Instead, we're going to create a button class that holds an itemstack and a runnable. */

    private final ItemStack item;
    private final Runnable simpleAction;
    private final Consumer<InventoryClickEvent> eventAction;

    // Constructor for actions that don't need an event
    public Button(ItemStack item, Runnable action) {
        this.item = item;
        this.simpleAction = action;
        this.eventAction = null;
    }

    // Constructor for actions that need the event
    public Button(ItemStack item, Consumer<InventoryClickEvent> action) {
        this.item = item;
        this.simpleAction = null;
        this.eventAction = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public void runAction(InventoryClickEvent event) {
        if (eventAction != null) {
            eventAction.accept(event);  // Run event-based action
        } else if (simpleAction != null) {
            simpleAction.run();  // Run simple action
        }
    }
}
