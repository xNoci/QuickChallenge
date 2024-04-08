package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private static final Logger log = LogManager.getLogger(PlayerTeleportListener.class);
    private final ChallengeController challengeController;

    public PlayerTeleportListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handlePlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        challengeController.fromEntity(player).ifPresent(challenge -> challenge.setLastKnownLocation(player));
    }

}
