package me.noci.challenges.listeners;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private final ChallengeController challengeController;

    public EntityDamageListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleEntityDamage(EntityDamageEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

    @EventHandler
    public void handleEntityDamageByEntity(EntityDamageByEntityEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

    @EventHandler
    public void handleEntityDamageByBlock(EntityDamageByBlockEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

}
