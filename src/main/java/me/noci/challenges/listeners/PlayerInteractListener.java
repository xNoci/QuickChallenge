package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final ChallengeController controller;

    public PlayerInteractListener(ChallengeController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getGameMode().isInvulnerable()) return;
        event.setCancelled(controller.shouldCancelEvents());
    }

}
