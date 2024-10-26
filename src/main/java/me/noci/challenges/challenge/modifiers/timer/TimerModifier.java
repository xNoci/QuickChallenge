package me.noci.challenges.challenge.modifiers.timer;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.TextGradient;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.timer.style.TimerStyle;
import me.noci.challenges.challenge.modifiers.timer.style.TimerStyleMode;
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

public class TimerModifier implements ChallengeModifier {

    private static final String TIMER_PAUSED_STRING = "Der Timer ist pausiert";

    @Getter private long ticksPlayed;

    private TextColor gradientPrimary;
    private TextColor gradientAccent;
    private TimerStyle timerStyle;

    public TimerModifier() {
        this(0);
    }

    public TimerModifier(long ticksPlayed) {
        this.ticksPlayed = ticksPlayed;
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
        timerStyle = new TimerStyle(TimerStyleMode.BLINK.styleData());
        loadConfigValues();
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
        gradientPrimary = null;
        gradientAccent = null;
        timerStyle = null;
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

        timerStyle.tick();

        String actionBarText = challenge.paused() ? TIMER_PAUSED_STRING : playedTimeAsString();
        Component actionBar = TextGradient.gradient(actionBarText, gradientPrimary, gradientAccent, timerStyle::progressTransformer).decorate(textDecorations.toArray(TextDecoration[]::new));
        players.forEach(player -> player.sendActionBar(actionBar));
    }

    @Override
    public void onConfigReload(Logger logger, Challenge challenge, Config config) {
        loadConfigValues();
    }

    private void loadConfigValues() {
        gradientPrimary = Option.Settings.Timer.PRIMARY_COLOR.get();
        gradientAccent = Option.Settings.Timer.ACCENT_COLOR.get();

        String mode = Option.Settings.Timer.MODE.get();
        TimerStyleMode styleMode = EnumUtils.getIfPresent(TimerStyleMode.class, mode).orElse(TimerStyleMode.BLINK);
        timerStyle.styleData(styleMode.styleData());
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

}
