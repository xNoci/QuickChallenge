package me.noci.challenges.challenge.modifiers.registry;

import lombok.Getter;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;

import java.util.function.Supplier;

public class ModifierHolder<T extends ChallengeModifier> {

    @Getter
    private final Class<T> type;
    @Getter
    private final Supplier<T> creator;

    protected ModifierHolder(Class<T> type, Supplier<T> creator) {
        this.type = type;
        this.creator = creator;
    }

    public String key() {
        return type.getSimpleName();
    }

}
