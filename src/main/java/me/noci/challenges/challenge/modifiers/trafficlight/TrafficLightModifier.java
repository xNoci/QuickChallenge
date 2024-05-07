package me.noci.challenges.challenge.modifiers.trafficlight;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.utils.EnumUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static me.noci.challenges.serializer.TypeSerializers.*;

public class TrafficLightModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<TrafficLightModifier>> SERIALIZER = TypeSerializer.fixed(37, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        TimeRange greenDuration = TIME_RANGE.read(buffer);
        TimeRange yellowDuration = TIME_RANGE.read(buffer);
        TimeRange redDuration = TIME_RANGE.read(buffer);
        LightStatus lightStatus = TRAFFIC_LIGHT_STATUS.read(buffer);
        long nextAction = LONG.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new TrafficLightModifier(greenDuration, yellowDuration, redDuration, lightStatus, nextAction));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::greenDuration).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::yellowDuration).orElse(TimeRange.oneSecond()));
        TIME_RANGE.write(buffer, value.map(TrafficLightModifier::redDuration).orElse(TimeRange.oneSecond()));
        TRAFFIC_LIGHT_STATUS.write(buffer, value.map(TrafficLightModifier::lightStatus).orElse(LightStatus.GREEN));
        LONG.write(buffer, value.map(TrafficLightModifier::nextAction).orElse(0L));
    });

    private final HashMap<UUID, LastLocation> lastLocations = Maps.newHashMap();
    @Getter private final TimeRange greenDuration;
    @Getter private final TimeRange yellowDuration;
    @Getter private final TimeRange redDuration;
    private final BossBar bossBar;
    @Getter private LightStatus lightStatus;
    @Getter private long nextAction;

    public TrafficLightModifier(TimeRange greenDuration, TimeRange yellowDuration, TimeRange redDuration, LightStatus lightStatus) {
        this(greenDuration, yellowDuration, redDuration, lightStatus, greenDuration.randomAsTick());
    }

    public TrafficLightModifier(TimeRange greenDuration, TimeRange yellowDuration, TimeRange redDuration, LightStatus lightStatus, long nextAction) {
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
        this.lightStatus = lightStatus;
        this.nextAction = nextAction;
        this.bossBar = BossBar.bossBar(lightStatus.texture(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        logger.info("Traffic Light is set to %s, next action in %s ticks; Green Duration: %s; Yellow Duration: %s, Red Duration: %s".formatted(lightStatus, nextAction, greenDuration, yellowDuration, redDuration));
        lastLocations.clear();
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .forEach(bossBar::removeViewer);
        lastLocations.clear();
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        players.forEach(bossBar::addViewer);

        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(Predicate.not(players::contains))
                .forEach(bossBar::removeViewer);

        if (lightStatus == LightStatus.RED) {
            if (!challenge.paused()) {
                players.stream().filter(this::checkMovement).forEach(player -> player.setHealth(0));
            } else {
                lastLocations.clear();
            }
        }

        if (nextAction <= 0) {
            lightStatus = EnumUtils.next(lightStatus);

            nextAction = switch (lightStatus) {
                case GREEN -> greenDuration.randomAsTick();
                case YELLOW -> yellowDuration.randomAsTick();
                case RED -> redDuration.randomAsTick();
            };

            bossBar.name(lightStatus.texture());

            if (lightStatus == LightStatus.RED) {
                players.forEach(this::checkMovement);
            }

            players.forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 2));
            logger.info("Switched traffic light to %s, next phase %s in %s ticks".formatted(lightStatus, EnumUtils.next(lightStatus), nextAction));
        }

        if (!challenge.paused()) {
            nextAction--;
        }
    }

    @Override
    public String name() {
        return "Traffic Light";
    }

    private boolean checkMovement(Player player) {
        UUID uuid = player.getUniqueId();
        LastLocation lastLocation = lastLocations.get(uuid);
        LastLocation currentLocation = LastLocation.fromPlayer(player);
        lastLocations.put(uuid, new LastLocation(currentLocation.x(), currentLocation.z()));
        return currentLocation.moved(lastLocation);
    }

    private record LastLocation(double x, double z) {
        public static LastLocation fromPlayer(Player player) {
            Location location = player.getLocation();
            return new LastLocation(location.x(), location.z());
        }

        public boolean moved(LastLocation other) {
            if (other == null) return false;
            return x != other.x || z != other.z;
        }

    }

}
