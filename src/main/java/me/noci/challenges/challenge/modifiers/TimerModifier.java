package me.noci.challenges.challenge.modifiers;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.TextGradient;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.BukkitUnit;
import me.noci.quickutilities.utils.EnumUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public class TimerModifier implements ChallengeModifier {

    private static final String TIMER_PAUSED_STRING = "Der Timer ist pausiert";

    @Getter private long ticksPlayed;

    private TextColor gradientPrimary;
    private TextColor gradientAccent;
    private TimerTransformer timerMode;

    public TimerModifier() {
        this(0);
    }

    public TimerModifier(long ticksPlayed) {
        this.ticksPlayed = ticksPlayed;
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        gradientPrimary = null;
        gradientAccent = null;
        timerMode = null;
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        if (!challenge.paused()) {
            ticksPlayed++;
        }

        List<TextDecoration> textDecorations = Lists.newArrayList(TextDecoration.BOLD);
        if (challenge.paused()) {
            textDecorations.add(TextDecoration.ITALIC);
        }

        timerMode.tick();

        String actionBarText = challenge.paused() ? TIMER_PAUSED_STRING : playedTimeAsString();
        Component actionBar = TextGradient.gradient(actionBarText, gradientPrimary, gradientAccent, timerMode::progressTransformer).decorate(textDecorations.toArray(TextDecoration[]::new));
        players.forEach(player -> player.sendActionBar(actionBar));
    }

    @Override
    public void onConfigReload(Logger logger, Challenge challenge, Config config) {
        gradientPrimary = Option.Settings.Timer.PRIMARY_COLOR.get();
        gradientAccent = Option.Settings.Timer.ACCENT_COLOR.get();
        
        String mode = Option.Settings.Timer.MODE.get();
        timerMode = EnumUtils.getIfPresent(TimerMode.class, mode).orElse(TimerMode.BLINK).createNew();
    }

    @Override
    public String name() {
        return "Timer";
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

    private enum TimerMode {
        BLINK(() -> new TimerTransformer(0.008f, 5.5f, 10, TimerTransformer.Transformer.BLINK)),
        WAVE(() -> new TimerTransformer(0.015f, 4.5f, 10, TimerTransformer.Transformer.WAVE));

        private final Supplier<TimerTransformer> transformer;

        TimerMode(Supplier<TimerTransformer> transformer) {
            this.transformer = transformer;
        }

        private TimerTransformer createNew() {
            return transformer.get();
        }
    }

    private static class TimerTransformer {

        private final float gradientSpeed;
        private final float gradientPeriod;
        private final float gradientAccentStrength;
        private final Transformer transformer;

        private float gradientTranslation = 0;

        private TimerTransformer(float gradientSpeed, float gradientPeriod, float gradientAccentStrength, Transformer transformer) {
            this.gradientSpeed = gradientSpeed;
            this.gradientPeriod = gradientPeriod;
            this.gradientAccentStrength = gradientAccentStrength;
            this.transformer = transformer;
        }

        private void tick() {
            gradientTranslation += gradientSpeed;
            gradientTranslation %= (float) (4 * Math.PI / gradientPeriod);
        }

        private float progressTransformer(int currentIndex, int stringLength) {
            return transformer.transform(gradientPeriod, gradientAccentStrength, gradientTranslation, currentIndex);
        }

        @FunctionalInterface
        private interface Transformer {
            Transformer BLINK = (gradientPeriod, gradientAccentStrength, gradientTranslation, currentIndex) -> {
                float progress = (gradientPeriod * 10);
                progress += gradientTranslation;
                progress = 0.5f + (float) Math.sin(gradientPeriod * progress) / 2;
                return (float) Math.pow(progress, gradientAccentStrength);
            };

            Transformer WAVE = (gradientPeriod, gradientAccentStrength, gradientTranslation, currentIndex) -> {
                float progress = (float) currentIndex / (gradientPeriod * 10);
                progress += gradientTranslation;
                progress = 0.5f + (float) Math.sin(gradientPeriod * progress) / 2;
                return (float) Math.pow(progress, gradientAccentStrength);
            };

            float transform(float gradientPeriod, float gradientAccentStrength, float gradientTranslation, int currentIndex);
        }

    }

}
