package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.worlds.ChallengeWorld;
import me.noci.challenges.worlds.LastKnownLocation;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class Challenge implements Comparable<Challenge> {

    private final Logger logger;

    @Getter private final UUID handle;
    @Getter private final ExitStrategy exitStrategy;
    @Getter private final Set<ChallengeModifier> modifiers;
    @Getter private final Map<UUID, LastKnownLocation> lastKnownLocation;
    private Reference<ChallengeWorld> world;

    //TODO Change to worldLoaded and only load worlds when they are needed
    @Getter @Setter private boolean started = false;
    @Getter @Setter private boolean paused = true;

    public Challenge(UUID handle, ExitStrategy exitStrategy, List<ChallengeModifier> challengeModifiers) {
        this(handle, exitStrategy, Maps.newHashMap(), null, challengeModifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, ChallengeWorld challengeWorld, ChallengeModifier... challengeModifiers) {
        this(handle, exitStrategy, Maps.newHashMap(), challengeWorld, challengeModifiers);
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, Map<UUID, LastKnownLocation> lastKnownLocations, List<ChallengeModifier> challengeModifiers) {
        this(handle, exitStrategy, lastKnownLocations, null, challengeModifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, Map<UUID, LastKnownLocation> lastKnownLocations, ChallengeWorld world, ChallengeModifier... modifiers) {
        this.logger = LogManager.getLogger("Challenge %s".formatted(handle.toString()));

        this.handle = handle;
        this.exitStrategy = exitStrategy;
        this.lastKnownLocation = lastKnownLocations;
        this.modifiers = ImmutableSet.copyOf(modifiers);
        this.world = new WeakReference<>(world);
    }

    public boolean isInChallenge(Entity entity) {
        return challengeWorld().map(world -> world.hasEntity(entity)).orElse(false);
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
    public <T extends ChallengeModifier> Optional<T> modifier(Class<T> modifier) {
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

    public void join(Player player) {
        Optional<LastKnownLocation> lastLocation = Optional.ofNullable(lastKnownLocation.get(player.getUniqueId()));
        World.Environment environment = lastLocation.map(LastKnownLocation::environment).orElse(World.Environment.NORMAL);

        challengeWorld()
                .flatMap(world -> world.worldByEnvironment(environment))
                .map(world ->
                        lastLocation.map(location -> location.getForWorld(world))
                                .orElse(world.getSpawnLocation())
                )
                .ifPresent(player::teleport);
    }

    public void setLastKnownLocation(Player player) {
        lastKnownLocation.put(player.getUniqueId(), LastKnownLocation.fromPlayer(player));
    }

    public boolean shouldCancelEvents() {
        return !started || paused;
    }

    @Override
    public int compareTo(@NotNull Challenge other) {

        Comparator<Challenge> compareStarted = Comparator.comparing(Challenge::started);
        Comparator<Challenge> comparePaused = Comparator.comparing(Challenge::paused);
        Comparator<Challenge> playedTime = Comparator.comparing(
                challenge -> challenge.modifier(TimerModifier.class).map(TimerModifier::ticksPlayed).orElse(0L)
        );

        return compareStarted.reversed().thenComparing(comparePaused).thenComparing(playedTime.reversed()).compare(this, other);
    }

    public void broadcast(Component message) {
        challengeWorld().ifPresent(challengeWorld -> challengeWorld.players().forEach(player -> player.sendMessage(message)));
    }

}
