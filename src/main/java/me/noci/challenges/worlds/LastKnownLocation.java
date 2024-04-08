package me.noci.challenges.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public record LastKnownLocation(double x, double y, double z, float pitch, float yaw, World.Environment environment) {

    public static LastKnownLocation fromPlayer(Player player) {
        return new LastKnownLocation(player.getX(), player.getY(), player.getZ(), player.getPitch(), player.getYaw(), player.getWorld().getEnvironment());
    }

    public Location getForWorld(World world) {
        return new Location(world, x, y, z, pitch, yaw);
    }

}
