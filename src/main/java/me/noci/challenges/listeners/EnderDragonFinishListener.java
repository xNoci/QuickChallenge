package me.noci.challenges.listeners;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

import java.util.Optional;

public class EnderDragonFinishListener implements Listener {

    private final ChallengeController challengeController;

    public EnderDragonFinishListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }


    @EventHandler
    public void onEnderDragonPhaseChange(EnderDragonChangePhaseEvent event) {
        if (event.getNewPhase() != EnderDragon.Phase.DYING) return;
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        if (!challenge.hasModifier(EnderDragonFinishModifier.class)) return;

        Optional<String> playedTime = challenge.modifier(TimerModifier.class, TimerModifier::playedTimeAsString);
        Option<Component> component = playedTime.isPresent() ? Option.EnderDragonFinish.WITH_TIME :
                Option.EnderDragonFinish.WITHOUT_TIME;

        Bukkit.broadcast(component.resolve(Placeholder.unparsed("time", playedTime.orElse(""))));

    }


}
