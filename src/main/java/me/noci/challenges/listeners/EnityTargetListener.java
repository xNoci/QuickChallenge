package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EnityTargetListener implements Listener {

    private final ChallengeController challengeController;

    public EnityTargetListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleEntityTarget(EntityTargetEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

}
