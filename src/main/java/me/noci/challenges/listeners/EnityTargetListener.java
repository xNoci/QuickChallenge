package me.noci.challenges.listeners;

import me.noci.challenges.challenge.Challenge;
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
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setTarget(null)
                );
    }

    @EventHandler
    public void handleEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setTarget(null)
                );
    }

}
