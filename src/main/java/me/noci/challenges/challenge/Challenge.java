package me.noci.challenges.challenge;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.worlds.ChallengeLocation;
import me.noci.challenges.worlds.ChallengeWorld;
import me.noci.challenges.worlds.RespawnLocation;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Challenge implements Comparable<Challenge> {

    private final Logger logger;

    @Getter private final UUID handle;
    @Getter private final ExitStrategy exitStrategy;
    @Getter private final Set<ChallengeModifier> modifiers;
    @Getter private final Map<UUID, ChallengeLocation> lastKnownLocation;
    @Getter private final Map<UUID, RespawnLocation> respawnLocations;
    @Getter private final Map<UUID, List<ItemStack>> playerEnderChest;
    @Getter private final Map<UUID, List<ItemStack>> playerArmor;
    @Getter private final Map<UUID, List<ItemStack>> playerInventory;

    private Reference<ChallengeWorld> world;

    //TODO Change to worldLoaded and only load worlds when they are needed
    @Getter @Setter private boolean started = false;
    @Getter @Setter private boolean paused = true;

    public Challenge(UUID handle, ExitStrategy exitStrategy, ChallengeWorld challengeWorld, ChallengeModifier... challengeModifiers) {
        this(handle, exitStrategy, Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), challengeWorld, challengeModifiers);
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, Map<UUID, ChallengeLocation> lastKnownLocations,
                     Map<UUID, RespawnLocation> respawnLocations, Map<UUID, List<ItemStack>> playerEnderChest,
                     Map<UUID, List<ItemStack>> playerArmor, Map<UUID, List<ItemStack>> playerInventory,
                     List<ChallengeModifier> challengeModifiers) {
        this(handle, exitStrategy, lastKnownLocations, respawnLocations, playerEnderChest, playerArmor, playerInventory, null, challengeModifiers.toArray(ChallengeModifier[]::new));
    }

    public Challenge(UUID handle, ExitStrategy exitStrategy, Map<UUID, ChallengeLocation> lastKnownLocations,
                     Map<UUID, RespawnLocation> respawnLocations, Map<UUID, List<ItemStack>> playerEnderChest,
                     Map<UUID, List<ItemStack>> playerArmor, Map<UUID, List<ItemStack>> playerInventory,
                     ChallengeWorld world, ChallengeModifier... modifiers) {
        this.logger = LogManager.getLogger("Challenge %s".formatted(handle.toString()));

        this.handle = handle;
        this.exitStrategy = exitStrategy;
        this.lastKnownLocation = lastKnownLocations;
        this.respawnLocations = respawnLocations;
        this.playerEnderChest = playerEnderChest;
        this.playerArmor = playerArmor;
        this.playerInventory = playerInventory;
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
            logger.info("Initialising challenge modifier '%s'...".formatted(modifier.name()));
            modifier.onInitialise(logger, this);
        });

        logger.info("Challenge modifiers initialised. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    public void stopChallengeModifiers() {
        long start = System.currentTimeMillis();
        logger.info("Stopping challenge modifiers..");

        challengeWorld().map(ChallengeWorld::players)
                .stream()
                .flatMap(List::stream)
                .forEach(player -> {
                    setLastKnownLocation(player);
                    saveInventory(player);
                });

        modifiers.forEach(modifier -> {
            modifier.onStop(logger, this);
            logger.info("Stopped challenge modifier '%s'".formatted(modifier.name()));
        });

        logger.info("Challenge modifiers stopped. Took %s ms".formatted(System.currentTimeMillis() - start));
    }

    public void tickChallengeModifiers() {
        modifiers.forEach(modifier -> modifier.onTick(logger, this, players()));
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
        Optional<ChallengeLocation> lastLocation = Optional.ofNullable(lastKnownLocation.get(player.getUniqueId()));
        World.Environment environment = lastLocation.map(ChallengeLocation::environment).orElse(World.Environment.NORMAL);

        challengeWorld()
                .flatMap(world -> world.worldByEnvironment(environment))
                .map(world ->
                        lastLocation.map(location -> location.getForWorld(world))
                                .orElse(world.getSpawnLocation())
                )
                .ifPresent(location -> {
                    player.teleport(location);
                    applyInventory(player);
                });
    }

    public void setLastKnownLocation(Player player) {
        lastKnownLocation.put(player.getUniqueId(), ChallengeLocation.fromPlayer(player));
    }

    public boolean shouldCancelEvents() {
        return !started || paused;
    }

    public void respawnLocation(Player player, Location location, RespawnLocation.Type type) {
        respawnLocations.put(player.getUniqueId(), RespawnLocation.fromLocation(location, type));
    }

    public Optional<RespawnLocation> respawnLocation(Player player) {
        return Optional.ofNullable(respawnLocations.get(player.getUniqueId()));
    }

    public void saveEnderChest(Player player, List<ItemStack> content) {
        Require.nonNull(player);
        Require.nonNull(content);
        playerEnderChest.put(player.getUniqueId(), content);
    }

    public List<ItemStack> enderChest(Player player) {
        Require.nonNull(player);
        return playerEnderChest.getOrDefault(
                player.getUniqueId(),
                IntStream.range(0, InventoryType.ENDER_CHEST.getDefaultSize())
                        .mapToObj(operand -> ItemStack.empty()).collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public void saveInventory(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerInventory inventory = player.getInventory();
        playerArmor.put(uuid, Lists.newArrayList(inventory.getArmorContents()));
        playerInventory.put(uuid, Lists.newArrayList(inventory.getContents()));
    }

    private void applyInventory(Player player) {
        player.getInventory().clear();

        var armor = playerArmor.get(player.getUniqueId());
        if (armor != null) {
            player.getInventory().setArmorContents(armor.toArray(ItemStack[]::new));
        }

        var inventory = playerInventory.get(player.getUniqueId());
        if (inventory != null) {
            player.getInventory().setContents(inventory.toArray(ItemStack[]::new));
        }

        player.updateInventory();
    }

    @Override
    public int compareTo(@NotNull Challenge other) {
        Comparator<Challenge> compareStarted = Comparator.comparing(Challenge::started);
        Comparator<Challenge> comparePaused = Comparator.comparing(Challenge::paused);
        Comparator<Challenge> playedTime = Comparator.comparing(
                challenge -> challenge.modifier(TimerModifier.class).map(TimerModifier::ticksPlayed).orElse(0L)
        );
        Comparator<Challenge> modifierCount = Comparator.comparing(challenge -> challenge.modifiers().size());

        return compareStarted.reversed().thenComparing(comparePaused).thenComparing(playedTime.reversed()).thenComparing(modifierCount.reversed()).compare(this, other);
    }

    public void broadcast(Component message) {
        challengeWorld().ifPresent(challengeWorld -> challengeWorld.players().forEach(player -> player.sendMessage(message)));
    }

    public List<Player> players() {
        return challengeWorld().map(ChallengeWorld::players).orElse(List.of());
    }

}
