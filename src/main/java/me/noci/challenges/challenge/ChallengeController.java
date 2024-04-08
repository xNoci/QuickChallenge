package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.serializ.ChallengeSerializer;
import me.noci.challenges.worlds.ChallengeWorld;
import me.noci.challenges.worlds.WorldController;
import me.noci.quickutilities.utils.BukkitUnit;
import me.noci.quickutilities.utils.Scheduler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class ChallengeController {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Controller");
    private static final Path CHALLENGE_FOLDER = Bukkit.getPluginsFolder().toPath().resolve("challenges");
    private static final String CHALLENGE_FILE_EXTENSION = ".qcc";

    private final HashMap<UUID, Challenge> challenges = Maps.newHashMap();
    private final WorldController worldController;

    public ChallengeController(WorldController worldController) {
        this.worldController = worldController;
        Scheduler.repeat(1, BukkitUnit.TICKS, () -> challenges().stream().filter(Challenge::started).forEach(Challenge::tickChallengeModifiers));
    }

    public Optional<Challenge> fromEntity(Entity entity) {
        return challenges().stream().
                filter(
                        challenge -> challenge.challengeWorld().map(challengeWorld -> challengeWorld.hasEntity(entity)).orElse(false)
                ).findFirst();
    }

    public void startChallenge(Challenge challenge) {
        challenge.initialiseChallengeModifiers();

        challenge.challengeWorld()
                .map(world -> Pair.of(world.players(), world.overworld()))
                .ifPresent(pair ->
                        pair.getRight().ifPresent(world ->
                                pair.getLeft().forEach(player -> player.teleport(world.getSpawnLocation()))
                        )
                );

        challenge.started(true);
        challenge.paused(false);
    }

    public List<Challenge> challenges() {
        return ImmutableList.copyOf(challenges.values());
    }

    public Challenge create(List<ChallengeModifier> modifiers, ExitStrategy exitStrategy) {
        UUID handle = UUID.randomUUID();
        LOGGER.info("Creating challenge with handle '%s'...".formatted(handle.toString()));
        LOGGER.info("using ExitStrategy '%s'".formatted(exitStrategy.name()));
        ChallengeWorld challengeWorld = worldController.generateChallengeWorld(handle, exitStrategy);
        Challenge challenge = new Challenge(handle, exitStrategy, challengeWorld, modifiers.toArray(ChallengeModifier[]::new));

        challenges.put(handle, challenge);
        return challenge;
    }

    @SneakyThrows
    public void delete(Challenge challenge) {
        Objects.requireNonNull(challenge, "Challenge cannot be null");

        UUID handle = challenge.handle();
        challenge.stopChallengeModifiers();

        worldController.deleteChallengeWorld(handle);
        challenges.remove(handle);
        Files.deleteIfExists(challengeFile(challenge));
    }

    public void save() {
        LOGGER.info("Saving challenges to disk...");
        long start = System.currentTimeMillis();

        challenges().stream()
                .filter(challenge -> challenge.exitStrategy() == ExitStrategy.SAVE_TO_FILE)
                .forEach(this::save);

        LOGGER.info("Challenges saved to disk. Took %s ms".formatted(System.currentTimeMillis() - start));
    }


    @SneakyThrows
    public void loadChallenges() {
        LOGGER.info("Loading challenges from file...");
        long start = System.currentTimeMillis();

        Files.createDirectories(CHALLENGE_FOLDER);
        try (Stream<Path> challenges = Files.list(CHALLENGE_FOLDER)) {
            challenges.filter(Files::isRegularFile)
                    .map(this::load)
                    .filter(Objects::nonNull)
                    .forEach(challenge -> this.challenges.put(challenge.handle(), challenge));
        }

        LOGGER.info("Challenges loaded from file. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    @SneakyThrows
    private void save(Challenge challenge) {
        Path filePath = challengeFile(challenge);
        byte[] data = ChallengeSerializer.serialize(challenge);
        Files.write(filePath, data);
        LOGGER.info("Saved challenge '%s' to file path '%s'.".formatted(challenge.handle(), filePath.toString()));
    }

    @SneakyThrows
    private @Nullable Challenge load(Path path) {
        Optional<Challenge> challenge = ChallengeSerializer.read(Files.readAllBytes(path));
        challenge.ifPresent(value -> {
            ChallengeWorld world = worldController.generateChallengeWorld(value.handle(), value.exitStrategy());
            value.challengeWorld(world);
        });

        challenge.ifPresent(value -> LOGGER.info("Loaded challenge '%s' from file.".formatted(value.handle())));

        return challenge.orElse(null);
    }

    private Path challengeFile(Challenge challenge) {
        return CHALLENGE_FOLDER.resolve(challenge.handle().toString() + CHALLENGE_FILE_EXTENSION);
    }

}
