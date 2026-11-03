package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class StopOnDeathModifier implements ChallengeModifier {


    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {

    }

    @Override
    public String name() {
        return "Stop on Death";
    }
}
