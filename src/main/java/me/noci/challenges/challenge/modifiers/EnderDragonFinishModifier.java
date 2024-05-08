package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

import java.util.List;
import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;

public class EnderDragonFinishModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<EnderDragonFinishModifier>> SERIALIZER = TypeSerializer.fixed(1, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new EnderDragonFinishModifier());
    }, (buffer, value) -> BOOLEAN.write(buffer, value.isPresent()));

    private SubscribedEvent<EnderDragonChangePhaseEvent> enderDragonChangePhaseEvent;

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        if (enderDragonChangePhaseEvent != null) {
            enderDragonChangePhaseEvent.unsubscribe();
        }

        enderDragonChangePhaseEvent = Events.subscribe(EnderDragonChangePhaseEvent.class)
                .filter(event -> !challenge.paused())
                .filter(event -> event.getNewPhase() == EnderDragon.Phase.DYING)
                .handle(event -> {
                    challenge.paused(true);

                    Component message = challenge.modifier(TimerModifier.class)
                            .map(TimerModifier::playedTimeAsString)
                            .map(timePlayed -> Component.text("Die Challenge wurde nach %s erfolgreich beendet.".formatted(timePlayed), NamedTextColor.GREEN))
                            .orElse(Component.text("Die Challenge wurde erfolgreich beendet", NamedTextColor.GREEN));

                    challenge.broadcast(message);
                });

    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        if (enderDragonChangePhaseEvent != null) {
            enderDragonChangePhaseEvent.unsubscribe();
            enderDragonChangePhaseEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {

    }

    @Override
    public String name() {
        return "Ender Dragon Finish";
    }
}
