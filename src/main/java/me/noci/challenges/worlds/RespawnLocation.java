package me.noci.challenges.worlds;

import com.google.common.collect.ImmutableList;
import me.noci.quickutilities.utils.DirectionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public record RespawnLocation(ChallengeLocation challengeLocation, Type type) {

    private static final Function<Vector, Vector> BELOW = vector -> vector.add(new Vector(0, -1, 0));
    private static final Function<Vector, Vector> UP = vector -> vector.add(new Vector(0, 1, 0));
    private static final ImmutableList<Vector> RESPAWN_ANCHOR_LAYER_OFFSETS = ImmutableList.of(new Vector(0, 0, -1), new Vector(-1, 0, 0), new Vector(0, 0, 1), new Vector(1, 0, 0), new Vector(-1, 0, -1), new Vector(1, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, 1));
    private static final ImmutableList<Vector> RESPAWN_ANCHOR_OFFSETS = new ImmutableList.Builder<Vector>().addAll(RESPAWN_ANCHOR_LAYER_OFFSETS).addAll(RESPAWN_ANCHOR_LAYER_OFFSETS.stream().map(BELOW).iterator()).addAll(RESPAWN_ANCHOR_LAYER_OFFSETS.stream().map(UP).iterator()).build();

    public static RespawnLocation fromLocation(Location location, Type type) {
        return new RespawnLocation(ChallengeLocation.fromLocation(location), type);
    }

    public Optional<Location> tryFindRespawnLocation(ChallengeWorld challengeWorld) {
        Location location = challengeLocation.fromChallengeWorld(challengeWorld).orElse(null);
        if (location == null) return Optional.empty();

        return switch (type) {
            case BED -> tryFindBedLocation(location);
            case RESPAWN_ANCHOR -> tryFindRespawnAnchorLocation(location);
            case OTHER -> tryFindOtherLocation(location);
        };
    }

    private Optional<Location> tryFindBedLocation(@NotNull Location location) {
        Block block = location.getBlock();
        if (!(block.getBlockData() instanceof Bed bed)) return Optional.empty();


        BlockFace bedFacing = bed.getFacing();
        BlockFace bedFacingClockWise = DirectionUtils.getClockWise(bedFacing);
        int[][] standUpOffsets = bedSurroundStandUpOffsets(bed.getFacing(), bedFacingClockWise);

        for (int[] offset : standUpOffsets) {
            Location toCheck = location.clone().add(offset[0], 0, offset[1]);
            if (validSpawnLocation(toCheck)) return Optional.of(toCheck.add(0.5, 0, 0.5));
        }

        return Optional.empty();
    }

    private Optional<Location> tryFindRespawnAnchorLocation(@NotNull Location location) {
        Block block = location.getBlock();
        if (!(block.getBlockData() instanceof RespawnAnchor respawnAnchor)) return Optional.empty();
        if (respawnAnchor.getCharges() <= 0) return Optional.empty();

        for (Vector vec : RESPAWN_ANCHOR_OFFSETS) {
            Location toCheck = location.clone().add(vec);
            if (validSpawnLocation(toCheck)) {

                respawnAnchor.setCharges(respawnAnchor.getCharges() - 1);
                block.setBlockData(respawnAnchor);

                return Optional.of(toCheck.add(0.5, 0, 0.5));
            }
        }

        return Optional.empty();
    }

    private Optional<Location> tryFindOtherLocation(@NotNull Location location) {
        return validSpawnLocation(location) ? Optional.of(location) : Optional.empty();
    }

    private boolean validSpawnLocation(@NotNull Location location) {
        Block block = location.getBlock();
        return block.getRelative(BlockFace.DOWN, 1).isSolid() &&
                !block.isSolid() &&
                !block.getRelative(BlockFace.UP, 1).isSolid();
    }


    private int[][] bedSurroundStandUpOffsets(BlockFace bedDirection, BlockFace respawnDirection) {
        return new int[][]{
                {respawnDirection.getModX(), respawnDirection.getModZ()},
                {respawnDirection.getModX() - bedDirection.getModX(), respawnDirection.getModZ() - bedDirection.getModZ()},
                {respawnDirection.getModX() - bedDirection.getModX() * 2, respawnDirection.getModZ() - bedDirection.getModZ() * 2},
                {-bedDirection.getModX() * 2, -bedDirection.getModZ() * 2},
                {-respawnDirection.getModX() - bedDirection.getModX() * 2, -respawnDirection.getModZ() - bedDirection.getModZ() * 2},
                {-respawnDirection.getModX() - bedDirection.getModX(), -respawnDirection.getModZ() - bedDirection.getModZ()},
                {-respawnDirection.getModX(), -respawnDirection.getModZ()},
                {-respawnDirection.getModX() + bedDirection.getModX(), -respawnDirection.getModZ() + bedDirection.getModZ()},
                {bedDirection.getModX(), bedDirection.getModZ()},
                {respawnDirection.getModX() + bedDirection.getModX(), respawnDirection.getModZ() + bedDirection.getModZ()}
        };
    }

    public enum Type {
        BED,
        RESPAWN_ANCHOR,
        OTHER
    }

}
