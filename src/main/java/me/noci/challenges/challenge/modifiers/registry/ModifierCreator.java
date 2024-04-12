package me.noci.challenges.challenge.modifiers.registry;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public interface ModifierCreator {
    static <T extends ChallengeModifier> ModifierCreator of(Class<T> modifierType, ItemStack displayItem, Supplier<T> getter) {
        return new ModifierCreator() {
            @Override
            public Class<T> type() {
                return modifierType;
            }

            @Override
            public ItemStack displayItem() {
                return displayItem;
            }

            @Override
            public ChallengeModifier create() {
                return getter.get();
            }
        };
    }

    Class<? extends ChallengeModifier> type();

    ItemStack displayItem();

    ChallengeModifier create();
}
