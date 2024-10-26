package me.noci.challenges.challenge;

import lombok.SneakyThrows;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.serializ.ChallengeSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChallengeController {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Controller");
    private static final Path CHALLENGE_FILE = QuickChallenge.instance().getDataFolder().toPath().resolve("challenge.qcc");

    @Nullable private Challenge challenge;

    public ChallengeController() {
    }

    public Optional<Challenge> challenge() {
        return Optional.ofNullable(challenge);
    }

    public boolean shouldCancelEvents() {
        return challenge == null || !challenge.started() || challenge.paused();
    }

    public void startChallenge() {
        if (challenge == null || challenge.started()) {
            return;
        }

        challenge.listenForConfigReload();
        challenge.initialiseChallengeModifiers();

        challenge.started(true);
        challenge.paused(false);
    }

    public void stopChallenge() {
        if (challenge == null || !challenge.started()) {
            return;
        }
        challenge.stopChallengeModifiers();
        challenge.stopListeningForConfigReload();

        challenge.started(false);
        challenge.paused(true);
    }

    public void create(List<ChallengeModifier> modifiers) {
        if (challenge != null) {
            LOGGER.info("Could not create challenge. A challenge already exists.");
            return;
        }
        this.challenge = new Challenge(modifiers.toArray(ChallengeModifier[]::new));
        LOGGER.info("Challenge created.");

    }

    @SneakyThrows
    public boolean delete() {
        if (challenge == null) {
            return false;
        }
        challenge.stopChallengeModifiers();
        Files.deleteIfExists(CHALLENGE_FILE);
        challenge = null;

        return true;
    }

    public void stopChallenges() {
        LOGGER.info("Stopping challenges...");
        long start = System.currentTimeMillis();
        challenge().ifPresent(Challenge::stopChallengeModifiers);
        LOGGER.info("Challenges stopped. Took {} ms", System.currentTimeMillis() - start);
    }

    @SneakyThrows
    public void save() {
        if (challenge == null) return;
        LOGGER.info("Saving challenges to disk...");
        long start = System.currentTimeMillis();

        byte[] data = ChallengeSerializer.serialize(challenge);
        Files.createDirectories(CHALLENGE_FILE.getParent());
        Files.write(CHALLENGE_FILE, data);

        LOGGER.info("Challenge saved to disk. Took {} ms", System.currentTimeMillis() - start);
    }


    @SneakyThrows
    public void tryLoadChallenge() {
        if (!Files.exists(CHALLENGE_FILE)) {
            LOGGER.info("Cannot load challenge. Challenge wasn't created yet.");
            return;
        }

        LOGGER.info("Loading challenge from file...");
        long start = System.currentTimeMillis();

        Optional<Challenge> challenge = ChallengeSerializer.read(Files.readAllBytes(CHALLENGE_FILE));
        if (challenge.isEmpty()) {
            LOGGER.info("Failed to load challenge from file.");
            return;
        }

        this.challenge = challenge.get();
        var modifiers = this.challenge.modifiers();
        LOGGER.info("Challenge successfully loaded from file. Took {} ms", System.currentTimeMillis() - start);
        LOGGER.info("- With {} modifier(s): {}", modifiers.size(), modifiers.stream().map(ChallengeModifier::name).collect(Collectors.joining(", ")));
    }


}
