package me.noci.challenges.listeners.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StopOnDeathListener implements Listener {

    private final ChallengeController challengeController;

    public StopOnDeathListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void onEnderDragonPhaseChange(PlayerDeathEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        if (!challenge.hasModifier(StopOnDeathModifier.class)) return;

        challenge.paused(true);
    }


}
