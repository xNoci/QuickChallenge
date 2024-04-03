package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.worlds.ChallengeWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Challenge {

    private final Logger logger;

    @Getter private final UUID handle;
    @Getter private final ExitStrategy exitStrategy;
    @Getter private final Set<ChallengeModifier> modifiers;
    private Reference<ChallengeWorld> world;

    @Getter @Setter private boolean started = false;
    @Getter @Setter private boolean paused = true;

    public Challenge(UUID handle, ExitStrategy exitStrategy, List<ChallengeModifier> challengeModifiers) {
        this(handle, exitStrategy, null, challengeModifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, ChallengeWorld world, ChallengeModifier... modifiers) {
        this.logger = LogManager.getLogger("Challenge %s".formatted(handle.toString()));

        this.handle = handle;
        this.exitStrategy = exitStrategy;
        this.modifiers = ImmutableSet.copyOf(modifiers);
        this.world = new WeakReference<>(world);
    }

    public boolean isInChallenge(Player player) {
        return challengeWorld().map(world -> world.hasEntity(player)).orElse(false);
    }

    public void initialiseChallengeModifiers() {
        long start = System.currentTimeMillis();
        logger.info("Initialising challenge modifiers..");
        modifiers.forEach(modifier -> {
            logger.info("Initialising challenge modifier '%s'...");
            modifier.onInitialise(logger, this);
        });

        logger.info("Challenge modifiers initialised. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    public void stopChallengeModifiers() {
        long start = System.currentTimeMillis();
        logger.info("Stopping challenge modifiers..");

        modifiers.forEach(modifier -> {
            modifier.onStop(logger, this);
            logger.info("Stopped challenge modifier '%s'".formatted(modifier.name()));
        });

        logger.info("Challenge modifiers stopped. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    public void tickChallengeModifiers() {
        List<Player> players = challengeWorld().map(ChallengeWorld::players).orElse(List.of());
        modifiers.forEach(modifier -> modifier.onTick(logger, this, players));
    }

    @SuppressWarnings("unchecked")
    public <T extends ChallengeModifier> Optional<T> getModifier(Class<T> modifier) {
        return modifiers.stream()
                .filter(challengeModifier -> challengeModifier.getClass().equals(modifier))
                .map(challengeModifier -> (T) challengeModifier)
                .findFirst();
    }

    public void challengeWorld(ChallengeWorld world) {
        this.world = new WeakReference<>(world);
    }

    public Optional<ChallengeWorld> challengeWorld() {
        return Optional.ofNullable(world.get());
    }

}
