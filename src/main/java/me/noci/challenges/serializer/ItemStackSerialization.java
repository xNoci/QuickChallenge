package me.noci.challenges.serializer;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemStackSerialization {

    @SneakyThrows
    public static byte[] toBytes(ItemStack itemStack) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(itemStack);
            return outputStream.toByteArray();
        }
    }


    @SneakyThrows
    public static ItemStack fromBytes(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        }
    }

}
