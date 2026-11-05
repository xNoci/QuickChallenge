package me.noci.challenges.challenge.modifiers.allitem.custommatcher;

import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ItemMatcher {

    private final Material material;

    public ItemMatcher(Material material) {
        this.material = material;
    }

    @Contract("null -> false")
    public boolean matches(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getType() == material;
    }

    public Material material() {
        return material;
    }

    protected String translationKey() {
        return material.translationKey();
    }

    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(material);
    }

    public Component name() {
        return Component.translatable(translationKey());
    }

}
