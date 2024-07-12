package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import org.jetbrains.annotations.Nullable;

public interface ModifierApplier {

    void apply(@Nullable ChallengeModifier modifier);

    default void cancel() {
        apply(null);
    }

}
