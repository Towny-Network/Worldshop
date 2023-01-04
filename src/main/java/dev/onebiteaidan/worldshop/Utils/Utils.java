package dev.onebiteaidan.worldshop.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

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
        return CraftItemStack.asBukkitCopy(ItemStack.a(tag));
    }
}
