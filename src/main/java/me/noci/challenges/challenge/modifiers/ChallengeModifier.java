package me.noci.challenges.challenge.modifiers;

import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChallengeModifier {

    void onInitialise(Logger logger);

    void onStop(Logger logger);

    void onTick(Logger logger, List<Player> players);

    String name();

}
