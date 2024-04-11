package me.noci.challenges.challenge.modifiers;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.ResourcePack;
import me.noci.challenges.TimeRange;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import me.noci.quickutilities.utils.EnumUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static me.noci.challenges.serializer.TypeSerializers.*;

public class TrafficLightModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<TrafficLightModifier>> SERIALIZER = TypeSerializer.fixed(37, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        TimeRange nextPhaseDelay = TIME_RANGE.read(buffer);
        TimeRange yellowDuration = TIME_RANGE.read(buffer);
        TimeRange redDuration = TIME_RANGE.read(buffer);
        TrafficLightModifier.LightStatus lightStatus = TRAFFIC_LIGHT_STATUS.read(buffer);
        long nextAction = LONG.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new TrafficLightModifier(nextPhaseDelay, yellowDuration, redDuration, lightStatus, nextAction));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::nextPhaseDelay).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::yellowDuration).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::redDuration).orElse(TimeRange.oneSecond()));
        TRAFFIC_LIGHT_STATUS.write(buffer, value.map(TrafficLightModifier::lightStatus).orElse(TrafficLightModifier.LightStatus.GREEN));
        LONG.write(buffer, value.map(TrafficLightModifier::nextAction).orElse(0L));
    });

    @Getter private final TimeRange nextPhaseDelay;
    @Getter private final TimeRange yellowDuration;
    @Getter private final TimeRange redDuration;

    private final BossBar bossBar;
    private SubscribedEvent<PlayerMoveEvent> playerMoveEvent;
    @Getter private LightStatus lightStatus;
    @Getter private long nextAction;

    public TrafficLightModifier(TimeRange nextPhaseDelay, TimeRange yellowDuration, TimeRange redDuration, LightStatus lightStatus, long nextAction) {
        this.nextPhaseDelay = nextPhaseDelay;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
        this.lightStatus = lightStatus;
        this.nextAction = nextAction;
        this.bossBar = BossBar.bossBar(lightStatus.texture(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        logger.info("Traffic light is set to %s, next phase %s in %s ticks".formatted(lightStatus, EnumUtils.next(lightStatus), nextAction));

        if (playerMoveEvent != null) {
            playerMoveEvent.unsubscribe();
        }

        playerMoveEvent = Events.subscribe(PlayerMoveEvent.class)
                .filter(event -> !challenge.paused())
                .filter(event -> challenge.isInChallenge(event.getPlayer()))
                .filter(event -> lightStatus == LightStatus.RED)
                .handle(event -> {
                    double fromX = event.getFrom().x();
                    double fromZ = event.getFrom().z();

                    double toX = event.getTo().x();
                    double toZ = event.getTo().z();

                    boolean moved = fromX != toX || fromZ != toZ;
                    if (moved) {
                        event.getPlayer().setHealth(0);
                    }
                });
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .forEach(bossBar::removeViewer);

        if (playerMoveEvent != null) {
            playerMoveEvent.unsubscribe();
            playerMoveEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        players.forEach(bossBar::addViewer);

        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(Predicate.not(players::contains))
                .forEach(bossBar::removeViewer);


        if (nextAction <= 0) {
            lightStatus = EnumUtils.next(lightStatus);

            bossBar.name(lightStatus.texture());

            nextAction = switch (lightStatus) {
                case GREEN -> nextPhaseDelay.randomAsTick();
                case YELLOW -> yellowDuration.randomAsTick();
                case RED -> redDuration.randomAsTick();
            };

            if (lightStatus != LightStatus.GREEN) {
                players.forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 2));
            }

            logger.info("Switch traffic light to %s, next phase %s in %s ticks".formatted(lightStatus, EnumUtils.next(lightStatus), nextAction));
        }

        if (!challenge.paused()) {
            nextAction--;
        }
    }

    @Override
    public String name() {
        return "Traffic Light";
    }

    @Getter
    public enum LightStatus {
        GREEN(ResourcePack.TrafficLight.GREEN_LIGHT),
        YELLOW(ResourcePack.TrafficLight.YELLOW_LIGHT),
        RED(ResourcePack.TrafficLight.RED_LIGHT);

        private final Component texture;

        LightStatus(Component texture) {
            this.texture = texture;
        }
    }

}
