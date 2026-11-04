package me.noci.challenges.challenge.modifiers.timer;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.TextGradient;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.timer.style.TimerStyle;
import me.noci.challenges.challenge.modifiers.timer.style.TimerStyleMode;
import me.noci.challenges.settings.Option;
import me.noci.challenges.utils.TimerUtils;
import me.noci.quickutilities.utils.EnumUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class TimerModifier extends ChallengeModifier {

    @Getter
    private long ticksPlayed;

    private TextColor gradientPrimary;
    private TextColor gradientAccent;
    private TimerStyle timerStyle;

    public TimerModifier() {
        this(0);
    }

    public TimerModifier(long ticksPlayed) {
        super("Timer");
        this.ticksPlayed = ticksPlayed;
    }

    @Override
    public void onInitialise() {
        timerStyle = new TimerStyle(TimerStyleMode.BLINK.styleData());
        loadConfigValues();
    }

    @Override
    public void onStop() {
        gradientPrimary = null;
        gradientAccent = null;
        timerStyle = null;
    }

    @Override
    public void onTick(Collection<? extends Player> players) {
        if (!getChallenge().paused()) {
            ticksPlayed++;
        }

        List<TextDecoration> textDecorations = Lists.newArrayList(TextDecoration.BOLD);
        if (getChallenge().paused()) {
            textDecorations.add(TextDecoration.ITALIC);
        }

        timerStyle.tick();

        String actionBarText = getChallenge().paused() ? Option.Timer.PAUSED.get() : playedTimeAsString();
        Component actionBar = TextGradient.gradient(actionBarText, gradientPrimary, gradientAccent,
                timerStyle::progressTransformer).decorate(textDecorations.toArray(TextDecoration[]::new));
        players.forEach(player -> player.sendActionBar(actionBar));
    }

    @Override
    public void onConfigReload() {
        loadConfigValues();
    }

    private void loadConfigValues() {
        gradientPrimary = Option.Timer.PRIMARY_COLOR.get();
        gradientAccent = Option.Timer.ACCENT_COLOR.get();

        String mode = Option.Timer.MODE.get();
        TimerStyleMode styleMode = EnumUtils.getIfPresent(TimerStyleMode.class, mode).orElse(TimerStyleMode.BLINK);
        timerStyle.styleData(styleMode.styleData());
    }

    public String playedTimeAsString() {
        return TimerUtils.convertTicksToText(ticksPlayed);
    }

}
