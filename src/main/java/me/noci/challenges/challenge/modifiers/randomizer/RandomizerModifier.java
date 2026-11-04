package me.noci.challenges.challenge.modifiers.randomizer;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.registry.ModifierRegistry;
import me.noci.challenges.team.Team;
import me.noci.quickutilities.utils.RandomUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public abstract class RandomizerModifier<T, U> extends ChallengeModifier {

    private static final UUID STATIC_UUID = new UUID(0, 0);


    private final Map<UUID, Long> seeds = Maps.newHashMap();
    private final Map<UUID, Map<T, U>> values = Maps.newHashMap();
    private final Supplier<Map<T, U>> cacheSupplier;

    /**
     * Whether the randomizer should be global or per team.
     * If true every team will use the same randomizer.
     */
    private boolean globalRandomizer = false;

    public RandomizerModifier(@NotNull String name, Supplier<Map<T, U>> cacheSupplier) {
        super(name);
        this.cacheSupplier = cacheSupplier;
    }


    public abstract U randomize(Long seed, T value, RandomProvider generator);

    public U randomize(Team team, T value) {
        UUID uuid = globalRandomizer ? STATIC_UUID : team.uuid();

        Map<T, U> cached = values.computeIfAbsent(uuid, _ -> cacheSupplier.get());
        return cached.computeIfAbsent(value, v -> {
            Long seed = seed(uuid);
            return randomize(seed, v, RandomGeneratorFactory.getDefault()::create);
        });
    }


    public Map<UUID, Long> seeds() {
        return new HashMap<>(seeds);
    }

    public void setSeeds(Map<UUID, Long> seeds) {
        this.seeds.clear();
        this.seeds.putAll(seeds);
    }

    private Long seed(UUID uuid) {
        return seeds.computeIfAbsent(uuid, _ -> RandomUtils.random().nextLong());
    }

    @FunctionalInterface
    public interface RandomProvider {
        RandomGenerator create(long seed);
    }

    @Override
    public void onInitialise() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onTick(Collection<? extends Player> players) {

    }
}
