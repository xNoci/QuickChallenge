package me.noci.challenges.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityMoveListener implements Listener {

    private final ChallengeController challengeController;

    public EntityMoveListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleEntityMove(EntityMoveEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(challenge -> event.setCancelled(true));
    }

}
