package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final ChallengeController challengeController;

    public PlayerMoveListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handlePlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return;
        }

        event.setCancelled(challengeController.shouldCancelEvents());
    }

}
