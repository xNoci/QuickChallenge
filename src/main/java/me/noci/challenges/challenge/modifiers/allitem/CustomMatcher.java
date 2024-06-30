package me.noci.challenges.challenge.modifiers.allitem;

import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

@FunctionalInterface
public interface CustomMatcher {

    static CustomMatcher potion(PotionType type) {
        return itemStack -> {
            if (!(itemStack.getItemMeta() instanceof PotionMeta potionMeta)) return false;
            return potionMeta.getBasePotionType() == type;
        };
    }

    static CustomMatcher translation(String key) {
        return itemStack -> {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return false;
            if (!(meta.displayName() instanceof TranslatableComponent component)) return false;
            return component.key().equals(key);
        };
    }

    boolean matches(ItemStack itemStack);

}
