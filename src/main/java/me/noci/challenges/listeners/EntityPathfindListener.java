package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.SlimePathfindEvent;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityPathfindListener implements Listener {

    private final ChallengeController challengeController;

    public EntityPathfindListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleEntityPathfind(EntityPathfindEvent event) {
        //Entities currently not working: Bat, Ghast, Glow Squid, Phantom, Squid, Vex, Spider
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

    @EventHandler
    public void handleSlimePathfind(SlimePathfindEvent event) {
        challengeController.fromEntity(event.getEntity())
                .filter(Challenge::shouldCancelEvents)
                .ifPresent(
                        challenge -> event.setCancelled(true)
                );
    }

}
