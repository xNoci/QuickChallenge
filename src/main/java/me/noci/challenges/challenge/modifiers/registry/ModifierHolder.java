package me.noci.challenges.challenge.modifiers.registry;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import net.kyori.adventure.key.Key;

import java.util.function.Supplier;

public record ModifierHolder<T extends ChallengeModifier>(Class<T> type, Supplier<T> creator, Key key) {

    ModifierHolder(Key key, Class<T> type, Supplier<T> creator) {
        this(type, creator, key);
    }

}
