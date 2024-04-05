package me.noci.challenges.listeners;

import me.noci.challenges.worlds.WorldController;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerPortalListener implements Listener {

    private final WorldController worldController;

    public PlayerPortalListener(WorldController worldController) {
        this.worldController = worldController;
    }

    @EventHandler
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void handlePlayerPortal(PlayerPortalEvent event) {
        Location to = event.getTo();

        worldController.fromEntity(event.getPlayer())
                .flatMap(challengeWorld -> challengeWorld.worldByEnvironment(event.getTo().getWorld().getEnvironment()))
                .ifPresent(to::setWorld);

        event.setTo(to);
        event.setCanCreatePortal(true);
    }

    @EventHandler
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void handleEntityPortal(EntityPortalEvent event) {
        Location to = event.getTo();
        if (to == null) return;
        worldController.fromEntity(event.getEntity())
                .flatMap(challengeWorld -> challengeWorld.worldByEnvironment(to.getWorld().getEnvironment()))
                .ifPresent(to::setWorld);

        event.setTo(to);
    }

}
