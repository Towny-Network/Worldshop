package dev.onebiteaidan.worldshop.Utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.onebiteaidan.worldshop.WorldShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;


public class Utils {

    // Next two methods grabbed from https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/

    public static byte[] saveItemStack(org.bukkit.inventory.ItemStack stack) throws IOException {
        // Create a NBT Compound Tag to save the item data into
        NBTTagCompound tag = new NBTTagCompound();
        // Convert the Bukkit ItemStack to an NMS one and use the NMS ItemStack to save the data in NBT
        CraftItemStack.asNMSCopy(stack).save(tag);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // Now save the data into a ByteArrayOutputStream to be able to convert it to a byte array
        // You can wrap a GZipOutputStream around if you also want to compress that data
        NBTCompressedStreamTools.a(tag, output);
        return output.toByteArray();
    }

    public static org.bukkit.inventory.ItemStack loadItemStack(byte[] data) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        // Reverse the process by reading the byte[] as ByteArrayInputStream and passing that to the NBT reader of Minecraft
        NBTTagCompound tag = NBTCompressedStreamTools.a(input);
        // At last load the ItemStack from the NBT Compound Tag and convert it back to an Bukkit ItemStack
        return CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(tag));
    }

    // Function to create playerheads
    public static ItemStack createSkull(String url) {
        ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "player");

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack createSkull(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwner(player.getName());

        head.setItemMeta(meta);

        return head;
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
