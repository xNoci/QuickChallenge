package me.noci.challenges.listeners;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemDropListener implements Listener {

    private final ChallengeController challengeController;

    public ItemDropListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleItemPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) return;

        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

    @EventHandler
    public void handleItemDrop(EntityDropItemEvent event) {
        if (event.getEntity() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) return;

        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }
}