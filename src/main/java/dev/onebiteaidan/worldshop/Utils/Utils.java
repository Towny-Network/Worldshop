package dev.onebiteaidan.worldshop.Utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


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
