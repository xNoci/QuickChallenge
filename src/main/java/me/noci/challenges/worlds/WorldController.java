package me.noci.challenges.worlds;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import me.noci.challenges.ExitStrategy;
import me.noci.quickutilities.utils.logfilter.LogFilter;
import me.noci.quickutilities.utils.logfilter.LogFilters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WorldController {

    private static final Logger LOGGER = LogManager.getLogger("WorldController");

    private final HashMap<UUID, ChallengeWorld> challengeWorlds = new HashMap<>();

    public WorldController() {
    }

    public void deleteWorlds() {
        LOGGER.info("Deleting challenge worlds...");

        worlds().stream()
                .filter(world -> world.exitStrategy() == ExitStrategy.DELETE)
                .forEach(challengeWorld -> deleteChallengeWorld(challengeWorld.handle()));

        LOGGER.info("Challenge worlds deleted.");
    }

    public ChallengeWorld generateChallengeWorld(UUID handle, ExitStrategy exitStrategy) {
        if (challengeWorlds.containsKey(handle)) {
            throw new IllegalStateException("A challenge world for handle '%s' already exists.");
        }

        LOGGER.info("Creating new challenge world with handle %s...".formatted(handle));

        long seed = handle.getMostSignificantBits();
        LOGGER.info("Using seed [%s]".formatted(seed));

        World overworld = generateWorld(handle, seed, World.Environment.NORMAL);
        World nether = generateWorld(handle, seed, World.Environment.NETHER);
        World theEnd = generateWorld(handle, seed, World.Environment.THE_END);

        ChallengeWorld challengeWorld = new ChallengeWorld(handle, exitStrategy, overworld, nether, theEnd);
        challengeWorlds.put(handle, challengeWorld);

        LOGGER.info("New challenge world created.");
        return challengeWorld;
    }

    public void deleteChallengeWorld(UUID handle) {
        LOGGER.info("Deleting challenge world with handle %s...".formatted(handle));
        long start = System.currentTimeMillis();

        ChallengeWorld challengeWorld = challengeWorlds.get(handle);

        if (challengeWorld != null) {
            challengeWorld.worlds().forEach(this::deleteWorld);
        }

        challengeWorlds.remove(handle);
        LOGGER.info("Challenge world deleted. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    public Optional<ChallengeWorld> fromEntity(Entity entity) {
        return worlds().stream()
                .filter(challengeWorld -> challengeWorld.hasEntity(entity))
                .findFirst();
    }

    private World generateWorld(UUID handle, long seed, World.Environment environment) {
        LOGGER.info("Generating world (%s)...".formatted(environment.name()));
        long start = System.currentTimeMillis();
        World world = WorldGenerationLogFilter.handleSilently(() -> Bukkit.createWorld(
                new WorldCreator(handle + "_world_" + environment.name().toLowerCase())
                        .environment(environment)
                        .seed(seed)
        ));
        LOGGER.info("World (%s) generated. Took %s ms".formatted(environment.name().toLowerCase(), System.currentTimeMillis() - start));

        return world;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteWorld(World world) {
        LOGGER.info("Deleting world (%s)...".formatted(world.getEnvironment().name()));
        WorldGenerationLogFilter.handleSilently(() -> {
            Optional<World> defaultWorld = Optional.ofNullable(Bukkit.getWorld("world"));
            defaultWorld.map(w -> w.getUID().equals(world.getUID()) ? null : w);
            world.getPlayers().forEach(player -> defaultWorld.ifPresentOrElse(w -> player.teleport(w.getSpawnLocation()), player::kick));

            Bukkit.unloadWorld(world, false);

            Path worldPath = world.getWorldFolder().toPath();

            try (Stream<Path> files = Files.walk(worldPath)) {
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                LOGGER.error("Failed to delete world: ", e);
            }
        });
    }

    private List<ChallengeWorld> worlds() {
        return ImmutableList.copyOf(challengeWorlds.values());
    }

    private static class WorldGenerationLogFilter {

        private static boolean stopLog = false;

        public static void stopLogging() {
            stopLog = true;
        }

        public static void startLogging() {
            stopLog = false;
        }

        static {
            LogFilters.addFilter(message -> stopLog ? LogFilter.Result.DENY : LogFilter.Result.NEUTRAL);
        }

        public static <T> T handleSilently(Supplier<T> supplier) {
            stopLogging();
            T value = supplier.get();
            startLogging();
            return value;
        }

        public static void handleSilently(Runnable runnable) {
            stopLogging();
            runnable.run();
            startLogging();
        }

    }

}
