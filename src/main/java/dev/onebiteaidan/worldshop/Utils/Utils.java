package dev.onebiteaidan.worldshop.Utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Utils {

    public static ItemStack createSkull(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwner(player.getName());

        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createSkull(String textureUrl) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures skin = playerProfile.getTextures();

        URL skinURL;
        try {
            skinURL = new URL(textureUrl);
        } catch (MalformedURLException e) {
            Logger.logStacktrace(e);
            return skull;
        }

        skin.setSkin(skinURL);
        playerProfile.setTextures(skin);
        skullMeta.setPlayerProfile(playerProfile);
        skull.setItemMeta(skullMeta);

        return skull;
    }


    /**
     * Checks the number of items the player has in their inventory that match the itemstack (excluding itemstack amount)
     * @param player player to check inventory of
     * @param itemStack itemstack to check for
     * @return returns the number of items the player has that match the provided itemstack
     */
    public static int getNumOfItems(Player player, ItemStack itemStack) {
        int amount = 0;
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (item.isSimilar(itemStack)) {
                    amount += item.getAmount();
                }
            }
        }
        return amount;
    }


    public static void removeNumItems(Player player, ItemStack itemStack, int amount) {
        int numRemoved = 0;
        int slot = 0;

        for (ItemStack item : player.getInventory()) {
            System.out.println("Iteration " + slot);
            System.out.println("NumRemoved: " + numRemoved + " Amount: " + amount);
            if (item != null) {
                if (numRemoved < amount) {
                    System.out.println("NumRemoved was less than amount");
                    System.out.println("Material Type: " + item.getType() + " x" + item.getAmount());
                    if (item.isSimilar(itemStack)) {
                        System.out.println("Item stack was similar");

                        // Check if the amount of this itemstack would overshoot our target numRemoved
                        if (item.getAmount() + numRemoved <= amount) {
                            System.out.println("Undershot");
                            // Wouldn't overshoot, remove whole stack
                            numRemoved += item.getAmount();
                            player.getInventory().setItem(slot, null);


                        } else {
                            // Would overshoot, remove needed amount
                            System.out.println("Overshot");
                            int amountNeeded = amount - numRemoved;
                            numRemoved += amountNeeded;
                            item.setAmount(item.getAmount() - amountNeeded);
                            player.getInventory().setItem(slot, item);
                        }
                    }
                } else {
                    break;
                }
            }
            slot++;
        }
    }

    /**
     * Attempts to fit an itemstack into the player's inventory.
     * @param player Player to fit item into.
     * @param item Itemstack to fit into player's inventory.
     * @return True if was able to fit item into inventory. False if it will not fit.
     */
    public static boolean fitItem(Player player, ItemStack item) {
        // Check if an empty slot exists
        //  -> One exists; add item to inven; return true
        //  -> No empties; lets find if we can add to existing stacks
        //      -> Yes we can; add to existing stacks; return true;
        //      -> no we can't; return false;



        // Check if an empty slot exists
        int emptySlot = player.getInventory().firstEmpty();

        if (emptySlot != -1) {
            // Item into first empty slot
            player.getInventory().setItem(emptySlot, item);
            return true;
        }

        // No empty slots
        // Check if we can add the amount to existing stacks
        Map<Integer, Integer> similar_slots = new HashMap<>(); // <slot #, capacity available>
        int count = 0;

        // Find all similar itemstacks
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null) {
                if (itemStack.isSimilar(item)) {
                    if (itemStack.getAmount() != itemStack.getMaxStackSize()) {
                        // ItemStack is not full
                        int spaceAvailable = itemStack.getMaxStackSize() - itemStack.getAmount();
                        similar_slots.put(count, spaceAvailable);
                    }
                }
            }
            count++;
        }

        // Check if all similar slots have enough capacity to fit the item
        int totalSpaceAvailable = 0;
        for (Integer key : similar_slots.keySet()) {
            totalSpaceAvailable += similar_slots.get(key);
        }


        if (totalSpaceAvailable >= item.getAmount()) {
            // We have enough space to store all items
            // Distribute items
            int itemsGiven = 0;

            for (Integer key : similar_slots.keySet()) {
                if (itemsGiven >= item.getAmount()) {
                    // We finished
                    System.out.println("Finished");
                    return true;
                }

                int amountFree = similar_slots.get(key);

                if ((item.getAmount() - itemsGiven) <= amountFree) {
                    // Items amount fits into amount free
                    player.getInventory().getItem(key).add(item.getAmount() - itemsGiven);
                    itemsGiven += item.getAmount() - itemsGiven;
                } else {
                    // Items amount is greater than amount free
                    player.getInventory().getItem(key).add(amountFree);
                    itemsGiven += amountFree;
                }
            }

            return true;
        } else {
            // Not enough space to fit the whole stack
            return false;
        }
    }


    // TODO: Method should probably be moved to the Screen class.
    public static ItemStack createButtonItem(Material material, TextComponent displayName, @Nullable List<TextComponent> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(displayName);
            if (lore != null) {
                meta.lore(lore);
            }
        }

        item.setItemMeta(meta);

        return item;
    }

    // TODO: Method should probably be moved to the Screen class.
    public static ItemStack createButtonItem(ItemStack item, @Nullable TextComponent displayName, @Nullable List<TextComponent> lore) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(displayName);
            meta.lore(lore);
        }

        item.setItemMeta(meta);

        return item;
    }
}
