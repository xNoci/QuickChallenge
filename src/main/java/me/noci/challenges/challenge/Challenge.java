package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Challenge implements Comparable<Challenge> {

    private static final Logger LOGGER = LogManager.getLogger("Challenge");

    @Getter private final Set<ChallengeModifier> modifiers;
    @Getter @Setter private boolean started = false;
    @Getter @Setter private boolean paused = true;

    public Challenge(List<ChallengeModifier> modifiers) {
        this(modifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(ChallengeModifier... modifiers) {
        this.modifiers = ImmutableSet.copyOf(modifiers);
    }

    public void initialiseChallengeModifiers() {
        long start = System.currentTimeMillis();
        LOGGER.info("Initialising challenge modifiers..");
        modifiers.forEach(modifier -> {
            LOGGER.info("Initialising challenge modifier '{}'...", modifier.name());
            modifier.onInitialise(LOGGER, this);
        });

        LOGGER.info("Challenge modifiers initialised. Took {} ms", System.currentTimeMillis() - start);
    }

    public void stopChallengeModifiers() {
        long start = System.currentTimeMillis();
        LOGGER.info("Stopping challenge modifiers..");

        modifiers.forEach(modifier -> {
            modifier.onStop(LOGGER, this);
            LOGGER.info("Stopped challenge modifier '{}'", modifier.name());
        });

        LOGGER.info("Challenge modifiers stopped. Took {} ms", System.currentTimeMillis() - start);
    }

    public void tickChallengeModifiers() {
        modifiers.forEach(modifier -> modifier.onTick(LOGGER, this, players()));
    }

    @SuppressWarnings("unchecked")
    public <T extends ChallengeModifier> Optional<T> modifier(Class<T> modifier) {
        return modifiers.stream()
                .filter(challengeModifier -> challengeModifier.getClass().equals(modifier))
                .map(challengeModifier -> (T) challengeModifier)
                .findFirst();
    }

    @Override
    public int compareTo(@NotNull Challenge other) {
        Comparator<Challenge> compareStarted = Comparator.comparing(Challenge::started);
        Comparator<Challenge> comparePaused = Comparator.comparing(Challenge::paused);
        Comparator<Challenge> playedTime = Comparator.comparing(
                challenge -> challenge.modifier(TimerModifier.class).map(TimerModifier::ticksPlayed).orElse(0L)
        );
        Comparator<Challenge> modifierCount = Comparator.comparing(challenge -> challenge.modifiers().size());

        return compareStarted.reversed().thenComparing(comparePaused).thenComparing(playedTime.reversed()).thenComparing(modifierCount.reversed()).compare(this, other);
    }

    public void broadcast(Component message) {
        Bukkit.broadcast(message);
    }

    public List<Player> players() {
        return ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    }

}
