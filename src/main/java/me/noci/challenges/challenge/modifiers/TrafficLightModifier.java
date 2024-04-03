package me.noci.challenges.challenge.modifiers;

import lombok.Getter;
import me.noci.challenges.TimeRange;
import me.noci.challenges.challenge.Challenge;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import me.noci.quickutilities.utils.EnumUtils;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class TrafficLightModifier implements ChallengeModifier {

    private final NamespacedKey key;
    private final TimeRange nextPhaseDelay;
    private final TimeRange yellowDuration;
    private final TimeRange redDuration;

    private SubscribedEvent<PlayerMoveEvent> playerMoveEvent;
    private LightStatus lightStatus;
    private KeyedBossBar statusBar;
    private long nextAction;

    public TrafficLightModifier(Challenge challenge, TimeRange nextPhaseDelay, TimeRange yellowDuration, TimeRange redDuration) {
        this.key = NamespacedKey.fromString(challenge.handle().toString() + "traffic_light");
        this.nextPhaseDelay = nextPhaseDelay;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        lightStatus = LightStatus.GREEN;
        nextAction = nextPhaseDelay.randomAsTick();
        logger.debug("Traffic light is set to %s, next phase %s in %s ticks".formatted(lightStatus, EnumUtils.next(lightStatus), nextAction));

        if (statusBar != null) {
            Bukkit.removeBossBar(statusBar.getKey());
        }

        statusBar = Bukkit.createBossBar(key, lightStatus.texture(), BarColor.WHITE, BarStyle.SOLID);
        statusBar.setVisible(true);

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
        if (statusBar != null) {
            Bukkit.removeBossBar(statusBar.getKey());
            statusBar = null;
        }

        if (playerMoveEvent != null) {
            playerMoveEvent.unsubscribe();
            playerMoveEvent = null;
        }
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        if (statusBar != null) {
            players.stream()
                    .filter(player -> !statusBar.getPlayers().contains(player))
                    .forEach(player -> statusBar.addPlayer(player));

            statusBar.getPlayers().stream()
                    .filter(player -> !players.contains(player))
                    .forEach(player -> statusBar.removePlayer(player));
        }

        if (nextAction <= 0) {
            lightStatus = EnumUtils.next(lightStatus);
            changeDisplayTo(lightStatus);

            nextAction = switch (lightStatus) {
                case GREEN -> nextPhaseDelay.randomAsTick();
                case YELLOW -> yellowDuration.randomAsTick();
                case RED -> redDuration.randomAsTick();
            };

            if (lightStatus != LightStatus.GREEN) {
                players.forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5, 2));
            }

            logger.debug("Switch traffic light to %s, next phase %s in %s ticks".formatted(lightStatus, EnumUtils.next(lightStatus), nextAction));
        }

        if (!challenge.paused()) {
            nextAction--;
        }
    }

    @Override
    public String name() {
        return "Traffic Light";
    }

    private void changeDisplayTo(LightStatus status) {
        if (statusBar == null) return;
        statusBar.setTitle(status.texture());
        statusBar.setVisible(false);
        statusBar.setVisible(true);
    }

    @Getter
    public enum LightStatus {
        GREEN("\uEFF1"),
        YELLOW("\uEFF2"),
        RED("\uEFF3");

        private final String texture;

        LightStatus(String texture) {
            this.texture = texture;
        }
    }

}
