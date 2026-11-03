package me.noci.challenges.challenge;

import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.paper.QuickChallenge;
import me.noci.challenges.settings.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Challenge {

    private static final Logger LOGGER = LogManager.getLogger("Challenge");

    @Getter
    private final String createdBy;
    @Getter
    private final long createTimestamp;
    private final Map<Class<? extends ChallengeModifier>, ChallengeModifier> modifiers;
    @Getter
    @Setter
    private boolean started = false;
    @Getter
    @Setter
    private boolean paused = true;

    private Runnable configReloadListener;

    public Challenge(String createdBy,
                     long createTimestamp, Map<Class<? extends ChallengeModifier>, ChallengeModifier> modifiers) {
        this.modifiers = new HashMap<>(modifiers);
        this.createdBy = createdBy;
        this.createTimestamp = createTimestamp;

        for (ChallengeModifier modifier : modifiers()) {
            modifier.setChallenge(this);
        }
    }

    public void listenForConfigReload() {
        Config config = QuickChallenge.instance().config();
        if (configReloadListener != null) {
            config.removeListener(configReloadListener);
        }

        configReloadListener = () -> {
            for (ChallengeModifier modifier : modifiers()) {
                modifier.onConfigReload(LOGGER);
            }
        };
        config.registerListener(configReloadListener);
    }

    public void stopListeningForConfigReload() {
        if (configReloadListener == null) return;

        QuickChallenge.instance().config().removeListener(configReloadListener);
        configReloadListener = null;
    }

    public void initialiseChallengeModifiers() {
        long start = System.currentTimeMillis();
        LOGGER.info("Initialising challenge modifiers..");

        for (ChallengeModifier modifier : modifiers.values()) {
            LOGGER.info("Initialising challenge modifier '{}'...", modifier.getName());
            modifier.onInitialise(LOGGER);
        }

        LOGGER.info("Challenge modifiers initialised. Took {} ms", System.currentTimeMillis() - start);
    }

    public void stopChallengeModifiers() {
        long start = System.currentTimeMillis();
        LOGGER.info("Stopping challenge modifiers..");

        for (ChallengeModifier modifier : modifiers()) {
            modifier.onStop(LOGGER);
            LOGGER.info("Stopped challenge modifier '{}'", modifier.getName());
        }

        LOGGER.info("Challenge modifiers stopped. Took {} ms", System.currentTimeMillis() - start);
    }

    public void tickModifiers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (ChallengeModifier modifier : modifiers()) {
            modifier.onTick(LOGGER, players);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ChallengeModifier> Optional<T> modifier(Class<T> modifier) {
        return Optional.ofNullable((T) modifiers.get(modifier));
    }

    public <T extends ChallengeModifier, E> Optional<E> modifier(Class<T> modifier, Function<T, E> mapper) {
        return modifier(modifier).map(mapper);
    }

    public <T extends ChallengeModifier, E> E modifier(Class<T> modifier, Function<T, E> mapper, E defaultValue) {
        return modifier(modifier).map(mapper).orElse(defaultValue);
    }

    public <T extends ChallengeModifier> boolean hasModifier(Class<T> modifier) {
        return modifiers.containsKey(modifier);
    }

    public Collection<ChallengeModifier> modifiers() {
        return modifiers.values();
    }

}
