package me.noci.challenges.worlds;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import me.noci.challenges.ExitStrategy;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChallengeWorld {

    @Getter private final UUID handle;
    @Getter private final ExitStrategy exitStrategy;

    private final Reference<World> overworld;
    private final Reference<World> nether;
    private final Reference<World> theEnd;

    protected ChallengeWorld(UUID handle, ExitStrategy exitStrategy, World overworld, World nether, World theEnd) {
        this.handle = handle;
        this.exitStrategy = exitStrategy;
        this.overworld = new WeakReference<>(overworld);
        this.nether = new WeakReference<>(nether);
        this.theEnd = new WeakReference<>(theEnd);
    }

    public boolean hasEntity(Entity entity) {
        if (entity == null) return false;

        World entityWorld = entity.getWorld();
        World.Environment worldEnvironment = entityWorld.getEnvironment();
        UUID entityWorldID = entityWorld.getUID();

        return worldByEnvironment(worldEnvironment)
                .map(world -> world.getUID().equals(entityWorldID))
                .orElse(false);
    }

    public Optional<World> worldByEnvironment(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> overworld();
            case NETHER -> nether();
            case THE_END -> theEnd();
            default -> Optional.empty();
        };
    }

    public Optional<World> overworld() {
        return Optional.ofNullable(overworld.get());
    }

    public Optional<World> nether() {
        return Optional.ofNullable(nether.get());
    }

    public Optional<World> theEnd() {
        return Optional.ofNullable(theEnd.get());
    }

    public List<World> worlds() {
        ImmutableList.Builder<World> worlds = ImmutableList.builderWithExpectedSize(3);
        overworld().ifPresent(worlds::add);
        nether().ifPresent(worlds::add);
        theEnd().ifPresent(worlds::add);
        return worlds.build();
    }

    public List<Player> players() {
        ImmutableList.Builder<Player> players = ImmutableList.builder();

        overworld().map(World::getPlayers).ifPresent(players::addAll);
        nether().map(World::getPlayers).ifPresent(players::addAll);
        theEnd().map(World::getPlayers).ifPresent(players::addAll);

        return players.build();
    }

}
