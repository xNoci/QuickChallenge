package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.settings.Config;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChallengeModifier {

    void onInitialise(Logger logger, Challenge challenge);

    void onStop(Logger logger, Challenge challenge);

    void onTick(Logger logger, Challenge challenge, List<Player> players);

    default void onConfigReload(Logger logger, Challenge challenge, Config config) {}

    String name();

}
