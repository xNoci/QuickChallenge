package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;

public class StopOnDeathModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<StopOnDeathModifier>> SERIALIZER = TypeSerializer.fixed(1, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new StopOnDeathModifier());
    }, (buffer, value) -> BOOLEAN.write(buffer, value.isPresent()));

    private SubscribedEvent<PlayerDeathEvent> playerDeathEvent;

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        if (playerDeathEvent != null) {
            playerDeathEvent.unsubscribe();
        }

        playerDeathEvent = Events.subscribe(PlayerDeathEvent.class)
                .filter(event -> !challenge.paused())
                .handle(event -> challenge.paused(true));
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        if (playerDeathEvent != null) {
            playerDeathEvent.unsubscribe();
            playerDeathEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {

    }

    @Override
    public String name() {
        return "Stop on Death";
    }
}
