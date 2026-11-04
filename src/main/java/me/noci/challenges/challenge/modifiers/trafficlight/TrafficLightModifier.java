package me.noci.challenges.challenge.modifiers.trafficlight;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.time.BukkitUnit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Predicate;

public class TrafficLightModifier extends ChallengeModifier {

    private static final Logger LOGGER = LogManager.getLogger("Traffic Light");


    public static final TimeRange DEFAULT_GREEN = TimeRange.of(BukkitUnit.SECONDS, 30, 180);
    public static final TimeRange DEFAULT_YELLOW = TimeRange.of(BukkitUnit.TICKS, 30, 100);
    public static final TimeRange DEFAULT_RED = TimeRange.of(BukkitUnit.SECONDS, 5, 15);

    private final HashMap<UUID, LastLocation> lastLocations = Maps.newHashMap();
    private final BossBar bossBar;
    @Getter
    private TimeRange greenDuration;
    @Getter
    private TimeRange yellowDuration;
    @Getter
    private TimeRange redDuration;
    @Getter
    private LightStatus lightStatus;
    @Getter
    private long nextActionTimer;

    public TrafficLightModifier(TimeRange greenDuration, TimeRange yellowDuration,
                                TimeRange redDuration, LightStatus lightStatus) {
        super("Traffic Light");
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
        this.lightStatus = lightStatus;
        this.nextActionTimer = randomDuration();
        this.bossBar = BossBar.bossBar(lightStatus.texture(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void onInitialise() {
        LOGGER.info(
                "Light is set to {}, next action in {} ticks; Green Duration: {}; Yellow Duration: {}, Red Duration: {}",
                lightStatus, nextActionTimer, greenDuration, yellowDuration, redDuration);
        lastLocations.clear();
    }

    @Override
    public void onStop() {
        for (BossBarViewer viewer : bossBar.viewers()) {
            bossBar.removeViewer((Audience) viewer);
        }

        lastLocations.clear();
    }

    @Override
    public void onTick(Collection<? extends Player> players) {
        players.forEach(bossBar::addViewer);

        Lists.newArrayList(bossBar.viewers()).stream()
                .map(viewer -> (Player) viewer)
                .filter(Predicate.not(players::contains))
                .forEach(bossBar::removeViewer);

        if (lightStatus == LightStatus.RED) {
            if (!getChallenge().paused()) {
                players.stream().filter(this::checkMovement).forEach(player -> player.setHealth(0));
            } else {
                lastLocations.clear();
            }
        }

        if (nextActionTimer <= 0) {
            nextPhase(players);
        }

        if (!getChallenge().paused()) {
            nextActionTimer--;
        }
    }

    public void nextPhase(Collection<? extends Player> players) {
        setPhase(EnumUtils.next(lightStatus), players);
    }

    public void setPhase(LightStatus status, Collection<? extends Player> players) {
        lightStatus = status;
        nextActionTimer = randomDuration();

        bossBar.name(lightStatus.texture());

        if (lightStatus == LightStatus.RED) {
            players.forEach(this::checkMovement);
        }

        players.forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 2));
        LOGGER.info("Switched traffic light to {}, next phase {} in {} ticks", lightStatus,
                EnumUtils.next(lightStatus), nextActionTimer);
    }

    private long randomDuration() {
        return switch (lightStatus) {
            case GREEN -> greenDuration.randomAsTick();
            case YELLOW -> yellowDuration.randomAsTick();
            case RED -> redDuration.randomAsTick();
        };
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
