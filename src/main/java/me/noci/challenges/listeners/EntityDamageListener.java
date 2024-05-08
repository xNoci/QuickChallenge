package me.noci.challenges.listeners;

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
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityDamageByEntity(EntityDamageByEntityEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityDamageByBlock(EntityDamageByBlockEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

}
