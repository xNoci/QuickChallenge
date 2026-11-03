package me.noci.challenges.challenge;

import lombok.SneakyThrows;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.serializer.ChallengeLoader;
import me.noci.challenges.settings.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class ChallengeController {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Controller");

    private final Path challengeFile;
    private final ConfigurationLoader<?> loader;
    @Nullable
    private Challenge challenge;

    public ChallengeController(Path challengeFile) {
        this.challengeFile = challengeFile;
        loader = ChallengeLoader.createLoader(challengeFile);
    }

    public boolean isChallengeCreated() {
        return challenge != null;
    }

    public @Nullable Challenge getCurrentChallenge() {
        return challenge;
    }

    public boolean shouldCancelEvents() {
        return challenge == null || !challenge.started() || challenge.paused();
    }

    public void startChallenge() {
        LOGGER.info("Starting challenge...");
        if (challenge == null || challenge.started()) {
            LOGGER.info("No challenge to start.");
            return;
        }
        long start = System.currentTimeMillis();

        challenge.listenForConfigReload();
        challenge.initialiseChallengeModifiers();

        challenge.started(true);
        challenge.paused(false);
        LOGGER.info("Challenge started. Took {} ms", System.currentTimeMillis() - start);
    }

    public void stopChallenge() {
        LOGGER.info("Stopping challenge...");
        if (challenge == null || !challenge.started()) {
            LOGGER.info("No challenge to stop.");
            return;
        }
        long start = System.currentTimeMillis();

        challenge.stopChallengeModifiers();
        challenge.stopListeningForConfigReload();

        challenge.started(false);
        challenge.paused(true);
        LOGGER.info("Challenge stopped. Took {} ms", System.currentTimeMillis() - start);
    }

    public void createNewChallenge(HumanEntity entity, Set<ChallengeModifier> modifiers) {
        if (challenge != null) {
            LOGGER.info("Could not create challenge. A challenge already exists.");
            return;
        }

        this.challenge = new Challenge(entity.getName(), System.currentTimeMillis(), modifiers);
        LOGGER.info("Challenge created.");
        entity.sendMessage(Option.Command.Challenge.SUCCESSFULLY_CREATED.get());

    }

    @SneakyThrows
    public boolean delete() {
        if (challenge == null) {
            return false;
        }
        challenge.stopChallengeModifiers();
        Files.deleteIfExists(challengeFile);
        challenge = null;

        return true;
    }

    public void save() {
        if (challenge == null) return;
        LOGGER.info("Saving challenges to disk...");
        long start = System.currentTimeMillis();

        try {
            ConfigurationNode node = loader.createNode();
            node.set(Challenge.class, challenge);
            loader.save(node);
        } catch (ConfigurateException e) {
            LOGGER.warn("Failed to save challenge", e);
        }

        LOGGER.info("Challenge saved to disk. Took {} ms", System.currentTimeMillis() - start);
    }


    public void tryLoadChallenge() {
        if (!Files.exists(challengeFile)) {
            LOGGER.info("Cannot load challenge. Challenge wasn't created yet.");
            return;
        }

        LOGGER.info("Loading challenge from file...");
        long start = System.currentTimeMillis();

        try {
            ConfigurationNode node = loader.load();
            challenge = node.get(Challenge.class);

            if (challenge == null) {
                LOGGER.info("Failed to load challenge from file.");
                return;
            }

            var modifiers = this.challenge.modifiers();
            LOGGER.info("Challenge successfully loaded from file. Took {} ms", System.currentTimeMillis() - start);
            LOGGER.info("- With {} modifier(s): {}", modifiers.size(),
                    modifiers.stream().map(ChallengeModifier::getName).collect(Collectors.joining(", ")));
        } catch (ConfigurateException e) {
            LOGGER.warn("Failed to load challenge", e);
        }


    }


}
