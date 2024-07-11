package me.noci.challenges.challenge.modifiers;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.TextGradient;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.BukkitUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimerModifier implements ChallengeModifier {

    private static final String TIMER_PAUSED_STRING = "Der Timer ist pausiert";

    private static final float GRADIENT_SPEED = 0.015f;
    private static final float GRADIENT_PERIOD = 4.5f;
    private static final float GRADIENT_ACCENT_STRENGTH = 10;

    private float gradientTranslation;
    @Getter private long ticksPlayed;

    private Runnable configReloadListener;
    @Nullable private TextColor gradientPrimary;
    @Nullable private TextColor gradientAccent;

    public TimerModifier() {
        this(0);
    }

    public TimerModifier(long ticksPlayed) {
        this.gradientTranslation = 0;
        this.ticksPlayed = ticksPlayed;
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        Config config = QuickChallenge.instance().config();
        if (configReloadListener != null) {
            config.removeListener(configReloadListener);
        }

        configReloadListener = () -> {
            gradientPrimary = null;
            gradientAccent = null;
        };

        config.registerListener(configReloadListener);
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        if (configReloadListener != null) {
            QuickChallenge.instance().config().removeListener(configReloadListener);
            configReloadListener = null;
        }

        gradientPrimary = null;
        gradientAccent = null;
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        if (!challenge.paused()) {
            ticksPlayed++;
        }

        gradientTranslation += GRADIENT_SPEED;
        gradientTranslation %= (float) (4 * Math.PI / GRADIENT_PERIOD);

        List<TextDecoration> textDecorations = Lists.newArrayList(TextDecoration.BOLD);
        if (challenge.paused()) {
            textDecorations.add(TextDecoration.ITALIC);
        }

        String actionBarText = challenge.paused() ? TIMER_PAUSED_STRING : playedTimeAsString();
        Component actionBar = TextGradient.gradient(actionBarText, gradientPrimary(), gradientAccent(), (currentIndex, stringLength) -> {
            float progress = (float) currentIndex / (GRADIENT_PERIOD * 10);
            progress += gradientTranslation;
            progress = 0.5f + (float) Math.sin(GRADIENT_PERIOD * progress) / 2;
            return (float) Math.pow(progress, GRADIENT_ACCENT_STRENGTH);
        }).decorate(textDecorations.toArray(TextDecoration[]::new));

        players.forEach(player -> player.sendActionBar(actionBar));
    }

    @Override
    public String name() {
        return "Timer";
    }

    private TextColor gradientPrimary() {
        if (gradientPrimary != null) return gradientPrimary;
        gradientPrimary = Option.Settings.TimerGradient.PRIMARY.get();
        return gradientPrimary;
    }

    private TextColor gradientAccent() {
        if (gradientAccent != null) return gradientAccent;
        gradientAccent = Option.Settings.TimerGradient.ACCENT.get();
        return gradientAccent;
    }

    public String playedTimeAsString() {
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
