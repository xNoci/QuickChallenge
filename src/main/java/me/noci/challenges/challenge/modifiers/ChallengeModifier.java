package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.quickutilities.utils.Require;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class ChallengeModifier {

    private final String name;
    private Challenge owner;

    public ChallengeModifier(@NotNull String name) {
        this.name = name;
    }

    public final void setChallenge(@NotNull Challenge challenge) {
        Require.nonNull(challenge, "Challenge modifier cannot be assigned to a null challenge");
        Require.checkState(owner == null, "Challenge modifier is already assigned to a challenge");
        this.owner = challenge;
    }

    public abstract void onInitialise(Logger logger);

    public abstract void onStop(Logger logger);

    public abstract void onTick(Logger logger, Collection<? extends Player> players);

    public void onConfigReload(Logger logger) {
    }

    public final String getName() {
        return name;
    }

    public final @NotNull Challenge getChallenge() {
        Require.checkState(owner != null, "Challenge modifier is not assigned to a challenge");
        return owner;
    }

}
