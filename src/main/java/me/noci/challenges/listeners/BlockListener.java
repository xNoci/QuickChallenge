package me.noci.challenges.listeners;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final ChallengeController challengeController;

    public BlockListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        challengeController.fromEntity(event.getPlayer())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        challengeController.fromEntity(event.getPlayer())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

}
