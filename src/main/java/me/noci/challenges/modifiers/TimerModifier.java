package me.noci.challenges.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.colors.ColorUtils;
import me.noci.challenges.colors.Colors;
import me.noci.quickutilities.utils.BukkitUnit;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;

public class TimerModifier extends DefaultChallengeModifier {

    private static final String TIMER_PAUSED_STRING = "Der Timer ist pausiert";

    private static final float GRADIENT_SPEED = 0.015f;
    private static final float GRADIENT_PERIOD = 4.5f;
    private static final float GRADIENT_ACCENT_STRENGTH = 10;

    private float gradientTranslation;
    private long ticksPlayed;

    public TimerModifier(Challenge challenge) {
        super(challenge);
    }

    @Override
    public void onInitialise(Logger logger) {
        gradientTranslation = 0;
        ticksPlayed = 0;
    }

    @Override
    public void onStop(Logger logger) {

    }

    @Override
    public void onTick(Logger logger, List<Player> players) {
        if (!challenge.paused()) {
            ticksPlayed++;
        }

        gradientTranslation += GRADIENT_SPEED;
        gradientTranslation %= (float) (4 * Math.PI / GRADIENT_PERIOD);

        String actionBarText = challenge.paused() ? TIMER_PAUSED_STRING : playedTimeAsString();
        Component actionBar = ColorUtils.gradientText(actionBarText, Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_PRIMARY_COLOR, (currentIndex, stringLength) -> {
            float progress = (float) currentIndex / (GRADIENT_PERIOD * 10);
            progress += gradientTranslation;
            progress = 0.5f + (float) Math.sin(GRADIENT_PERIOD * progress) / 2;
            return (float) Math.pow(progress, GRADIENT_ACCENT_STRENGTH);
        });

        players.forEach(player -> player.sendActionBar(actionBar));
    }

    @Override
    public String name() {
        return "Timer";
    }

    private String playedTimeAsString() {
        long timePlayedSeconds = BukkitUnit.TICKS.toSeconds(ticksPlayed);

        long seconds = timePlayedSeconds % 60;
        long minutes = timePlayedSeconds / 60 % 60;
        long hours = timePlayedSeconds / 3600 % 24;
        long days = timePlayedSeconds / 86400;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        builder.append(minutes).append("m ");
        builder.append(seconds).append("s");

        return builder.toString();
    }

}
