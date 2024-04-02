package me.noci.challenges.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class StopOnDeathModifier extends DefaultChallengeModifier {

    private SubscribedEvent<PlayerDeathEvent> playerDeathEvent;

    public StopOnDeathModifier(Challenge challenge) {
        super(challenge);
    }

    @Override
    public void onInitialise(Logger logger) {
        if (playerDeathEvent != null) {
            playerDeathEvent.unsubscribe();
        }

        playerDeathEvent = Events.subscribe(PlayerDeathEvent.class)
                .filter(event -> !challenge.paused())
                .filter(event -> challenge.isInChallenge(event.getPlayer()))
                .handle(event -> challenge.paused(true));
    }

    @Override
    public void onStop(Logger logger) {
        if (playerDeathEvent != null) {
            playerDeathEvent.unsubscribe();
            playerDeathEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, List<Player> players) {

    }

    @Override
    public String name() {
        return "Stop on Death";
    }
}
