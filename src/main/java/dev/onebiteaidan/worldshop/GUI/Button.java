package dev.onebiteaidan.worldshop.GUI;

import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Button {
    /* Itemstack isn't mean for inheritance so we can't directly extend it.
    * Instead, we're going to create a button class that holds an itemstack and a runnable. */

    private final ItemStack item;
    private final Runnable action;

    public Button(ItemStack item, Runnable action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public void runAction(InventoryClickEvent event) {
        if (action != null) {
            action.run();
        }
    }
}
