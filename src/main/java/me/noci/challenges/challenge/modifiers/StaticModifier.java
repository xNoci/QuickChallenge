package me.noci.challenges.challenge.modifiers;

import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class StaticModifier extends ChallengeModifier {
    protected StaticModifier(@NotNull String name) {
        super(name);
    }

    @Override
    public void onInitialise(Logger logger) {

    }

    @Override
    public void onStop(Logger logger) {

    }

    @Override
    public void onTick(Logger logger, Collection<? extends Player> players) {

    }
}
