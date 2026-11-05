package me.noci.challenges.challenge.modifiers.allitem;

import me.noci.challenges.ResourcePack;
import me.noci.challenges.challenge.modifiers.allitem.custommatcher.ItemMatcher;
import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AllItem {

    private final Key key;
    private final char icon;
    private final @NotNull ItemMatcher matcher;

    protected AllItem(Key key, char icon, @NotNull ItemMatcher itemMatcher) {
        this.key = key;
        this.matcher = itemMatcher;
        this.icon = icon;
    }

    public Key key() {
        return this.key;
    }

    public boolean matches(@Nullable ItemStack itemStack) {
        return matcher.matches(itemStack);
    }

    public ItemBuilder asItemBuilder() {
        return matcher.asItemBuilder();
    }

    public ItemStack asItem() {
        return asItemBuilder().build();
    }

    public Component name() {
        return matcher.name();
    }

    public Component icon() {
        return ResourcePack.AllItems.itemIcon(icon);
    }

    ItemMatcher matcher() {
        return matcher;
    }

}
