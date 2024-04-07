package me.noci.challenges.challenge.modifiers;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.colors.ColorUtils;
import me.noci.challenges.colors.Colors;
import me.noci.challenges.serializer.TypeSerializer;
import me.noci.quickutilities.utils.BukkitUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static me.noci.challenges.serializer.TypeSerializers.BOOLEAN;
import static me.noci.challenges.serializer.TypeSerializers.LONG;

public class TimerModifier implements ChallengeModifier {

    public static final TypeSerializer<Optional<TimerModifier>> SERIALIZER = TypeSerializer.fixed(9, buffer -> {
        boolean enabled = BOOLEAN.read(buffer);
        long ticksPlayed = LONG.read(buffer);
        if (!enabled) return Optional.empty();
        return Optional.of(new TimerModifier(ticksPlayed));
    }, (buffer, value) -> {
        BOOLEAN.write(buffer, value.isPresent());
        LONG.write(buffer, value.map(TimerModifier::ticksPlayed).orElse(0L));
    });

    private static final String TIMER_PAUSED_STRING = "Der Timer ist pausiert";

    private static final float GRADIENT_SPEED = 0.015f;
    private static final float GRADIENT_PERIOD = 4.5f;
    private static final float GRADIENT_ACCENT_STRENGTH = 10;

    private float gradientTranslation;
    @Getter private long ticksPlayed;

    public TimerModifier(long ticksPlayed) {
        this.gradientTranslation = 0;
        this.ticksPlayed = ticksPlayed;
    }

    @Override
    public void onInitialise(Logger logger, Challenge challenge) {
    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {

    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {
        if (!challenge.paused()) {
            ticksPlayed++;
        }

        gradientTranslation += GRADIENT_SPEED; //TODO Change speed to be proportional to string length
        gradientTranslation %= (float) (4 * Math.PI / GRADIENT_PERIOD);

        List<TextDecoration> textDecorations = Lists.newArrayList(TextDecoration.BOLD);
        if (challenge.paused()) {
            textDecorations.add(TextDecoration.ITALIC);
        }

        String actionBarText = challenge.paused() ? TIMER_PAUSED_STRING : playedTimeAsString();
        Component actionBar = ColorUtils.gradientText(actionBarText, Colors.TIMER_PRIMARY_COLOR, Colors.TIMER_ACCENT_COLOR, (currentIndex, stringLength) -> {
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
