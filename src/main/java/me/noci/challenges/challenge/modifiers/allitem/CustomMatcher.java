package me.noci.challenges.challenge.modifiers.allitem;

import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

@FunctionalInterface
public interface CustomMatcher {

    static CustomMatcher potion(PotionType type) {
        return new PotionMatcher(type);
    }

    static CustomMatcher translation(String key) {
        return new TranslationKeyMatcher(key);
    }

    boolean matches(ItemStack itemStack);

    class PotionMatcher implements CustomMatcher {

        private final PotionType type;

        private PotionMatcher(PotionType type) {
            this.type = type;
        }

        @Override
        public boolean matches(ItemStack itemStack) {
            if (!(itemStack.getItemMeta() instanceof PotionMeta potionMeta)) return false;
            return potionMeta.getBasePotionType() == type;
        }

    }

    class TranslationKeyMatcher implements CustomMatcher {

        private final String key;

        private TranslationKeyMatcher(String translationKey) {
            this.key = translationKey;
        }

        @Override
        public boolean matches(ItemStack itemStack) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return false;
            if (!(meta.displayName() instanceof TranslatableComponent component)) return false;
            return component.key().equals(key);
        }
    }

}
