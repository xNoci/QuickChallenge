package me.noci.challenges.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

public record ChallengeLocation(double x, double y, double z, float yaw, float pitch, World.Environment environment) {

    public static ChallengeLocation fromPlayer(Player player) {
        return fromLocation(player.getLocation());
    }

    public static ChallengeLocation fromLocation(Location location) {
        return new ChallengeLocation(location.x(), location.y(), location.z(), location.getYaw(), location.getPitch(), location.getWorld().getEnvironment());
    }

    public Location getForWorld(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Optional<Location> fromChallengeWorld(ChallengeWorld challengeWorld) {
        return challengeWorld.worldByEnvironment(environment).map(this::getForWorld);
    }

}
