package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.events.Events;
import me.noci.quickutilities.events.subscriber.SubscribedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

import java.util.List;
import java.util.Optional;

public class EnderDragonFinishModifier implements ChallengeModifier {


    @Override
    public void onInitialise(Logger logger, Challenge challenge) {

    }

    @Override
    public void onStop(Logger logger, Challenge challenge) {
    }

    @Override
    public void onTick(Logger logger, Challenge challenge, List<Player> players) {

    }

    @Override
    public String name() {
        return "Ender Dragon Finish";
    }
}
