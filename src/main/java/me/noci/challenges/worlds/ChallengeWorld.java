package me.noci.challenges.worlds;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChallengeWorld {

    @Getter private final UUID handle;
    @Getter private final boolean deleteOnStop = true;

    private final Reference<World> overworld;
    private final Reference<World> nether;
    private final Reference<World> theEnd;

    protected ChallengeWorld(UUID handle, World overworld, World nether, World theEnd) {
        this.handle = handle;
        this.overworld = new WeakReference<>(overworld);
        this.nether = new WeakReference<>(nether);
        this.theEnd = new WeakReference<>(theEnd);
    }

    public boolean hasEntity(Entity entity) {
        if (overworld().filter(world -> world.getEntity(entity.getUniqueId()) != null).isPresent()) {
            return true;
        }

        if (nether().filter(world -> world.getEntity(entity.getUniqueId()) != null).isPresent()) {
            return true;
        }

        return theEnd().filter(world -> world.getEntity(entity.getUniqueId()) != null).isPresent();
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

}
