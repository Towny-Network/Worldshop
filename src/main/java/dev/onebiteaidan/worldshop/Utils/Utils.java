package dev.onebiteaidan.worldshop.Utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;


public class Utils {

    // Next two methods grabbed from https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/

    public byte[] saveItemStack(org.bukkit.inventory.ItemStack stack) throws IOException {
        // Create a NBT Compound Tag to save the item data into
        NBTTagCompound tag = new NBTTagCompound();
        // Convert the Bukkit ItemStack to an NMS one and use the NMS ItemStack to save the data in NBT
        CraftItemStack.asNMSCopy(stack).save(tag);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // Now save the data into a ByteArrayOutputStream to be able to convert it to an byte array
        // You can wrap a GZipOutputStream around if you also want to compress that data
        NBTCompressedStreamTools.a(tag, output);
        return output.toByteArray();
    }

    public org.bukkit.inventory.ItemStack loadItemStack(byte[] data) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        // Reverse the process by reading the byte[] as ByteArrayInputStream and passing that to the NBT reader of Minecraft
        NBTTagCompound tag = NBTCompressedStreamTools.a(input);
        // At last load the ItemStack from the NBT Compound Tag and convert it back to an Bukkit ItemStack
        return CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(tag));
    }

    //Function to create playerheads
    public static ItemStack createSkull(String url) {
        ItemStack head = new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD, 1);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

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

    public static int getNumOfItems(Player player, ItemStack itemStack) {
        int amount = 0;
        for (ItemStack item : player.getInventory()) {
            if (item.isSimilar(itemStack)) {
                amount += item.getAmount();
            }
        }
        return amount;
    }
}
