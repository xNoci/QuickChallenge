package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.settings.Config;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class Challenge {

    private static final Logger LOGGER = LogManager.getLogger("Challenge");

    @Getter private final Set<ChallengeModifier> modifiers;
    @Getter @Setter private boolean started = false;
    @Getter @Setter private boolean paused = true;
    private Runnable configReloadListener;
    private boolean configReloaded = true;

    public Challenge(List<ChallengeModifier> modifiers) {
        this(modifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(ChallengeModifier... modifiers) {
        this.modifiers = ImmutableSet.copyOf(modifiers);
    }

    public void listenForConfigReload() {
        Config config = QuickChallenge.instance().config();
        if (configReloadListener != null) {
            config.removeListener(configReloadListener);
        }

        configReloadListener = () -> configReloaded = true;
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

    public void tickModifiers() {
        List<Player> players = players();
        Config config = QuickChallenge.instance().config();
        modifiers.forEach(modifier -> {
            if (configReloaded) {
                modifier.onConfigReload(LOGGER, this, config);
            }
            modifier.onTick(LOGGER, this, players);
        });

        configReloaded = false;
    }

    @SuppressWarnings("unchecked")
    public <T extends ChallengeModifier> Optional<T> modifier(Class<T> modifier) {
        return modifiers.stream()
                .filter(challengeModifier -> challengeModifier.getClass().equals(modifier))
                .map(challengeModifier -> (T) challengeModifier)
                .findFirst();
    }

    public <T extends ChallengeModifier, E> Optional<E> modifier(Class<T> modifier, Function<T, E> mapper) {
        return modifier(modifier).map(mapper);
    }

    public <T extends ChallengeModifier, E> E modifier(Class<T> modifier, Function<T, E> mapper, E defaultValue) {
        return modifier(modifier).map(mapper).orElse(defaultValue);
    }

    public void broadcast(Component message) {
        Bukkit.broadcast(message);
    }

    public List<Player> players() {
        return ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    }

}
