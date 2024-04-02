package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChallengeModifier {

    void onInitialise(Logger logger, Challenge challenge);

    void onStop(Logger logger, Challenge challenge);

    void onTick(Logger logger, Challenge challenge, List<Player> players);

    String name();

}
