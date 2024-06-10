package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    private final ChallengeController challengeController;

    public FoodLevelChangeListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

}
