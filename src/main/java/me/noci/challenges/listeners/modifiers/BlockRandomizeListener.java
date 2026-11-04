package me.noci.challenges.listeners.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.randomizer.BlockRandomizerModifier;
import me.noci.challenges.team.Team;
import me.noci.quickutilities.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

public class BlockRandomizeListener implements Listener {

    private final ChallengeController challengeController;

    public BlockRandomizeListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        BlockRandomizerModifier modifier = challenge.modifier(BlockRandomizerModifier.class).orElse(null);
        if (modifier == null) return;

        //TODO Change
        Team team = new Team(event.getPlayer().getUniqueId());

        for (Item drop : event.getItems()) {
            ItemBuilder builder = ItemBuilder.edit(drop.getItemStack());
            Material newMaterial = modifier.randomize(team, drop.getItemStack().getType());

            builder.material(newMaterial);
            drop.setItemStack(builder.build());
        }
    }
}
