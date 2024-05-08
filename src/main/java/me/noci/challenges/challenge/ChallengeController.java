package me.noci.challenges.challenge;

import lombok.SneakyThrows;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.serializ.ChallengeSerializer;
import me.noci.quickutilities.utils.BukkitUnit;
import me.noci.quickutilities.utils.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChallengeController {

    private static final Logger LOGGER = LogManager.getLogger("Challenge Controller");
    private static final Path CHALLENGE_FILE = QuickChallenge.instance().getDataFolder().toPath().resolve("challenge.qcc");

    private static final Component NO_CHALLENGE_CREATED = Component.text("Es wurde noch keine Challenge erstellt.", NamedTextColor.RED);
    private static final Component CHALLENGE_NOT_STARTED = Component.text("Challenge wurde noch nicht gestartet.", NamedTextColor.RED);

    @Nullable private Challenge challenge;

    public ChallengeController() {
        Scheduler.repeat(1, BukkitUnit.TICKS, () -> {
            if (challenge == null) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(NO_CHALLENGE_CREATED));
                return;
            }

            if(!challenge.started()) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(CHALLENGE_NOT_STARTED));
                return;
            }

            challenge.tickChallengeModifiers();
        });
    }

    public Optional<Challenge> challenge() {
        return Optional.ofNullable(challenge);
    }

    public boolean shouldCancelEvents() {
        return challenge == null || !challenge.started() || challenge.paused();
    }

    public boolean isStarted() {
        return challenge().map(Challenge::started).orElse(false);
    }

    public void startChallenge() {
        if (challenge == null || challenge.started()) {
            return;
        }

        challenge.initialiseChallengeModifiers();

        challenge.started(true);
        challenge.paused(false);
    }

    public void stopChallenge() {
        if (challenge == null || !challenge.started()) {
            return;
        }
        challenge.stopChallengeModifiers();

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
