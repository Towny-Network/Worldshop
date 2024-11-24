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
import java.util.List;
import java.util.UUID;


public class Utils {

    public static byte[] saveItemStack(ItemStack item) {
        return item.serializeAsBytes();
    }

    public static ItemStack loadItemStack(byte[] data) {
        return ItemStack.deserializeBytes(data);
    }

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
