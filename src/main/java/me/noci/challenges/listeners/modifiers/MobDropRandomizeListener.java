package me.noci.challenges.listeners.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.randomizer.MobDropRandomizerModifier;
import me.noci.challenges.team.Team;
import me.noci.quickutilities.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MobDropRandomizeListener implements Listener {

    private final ChallengeController challengeController;

    public MobDropRandomizeListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void onItemDrop(EntityDeathEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        MobDropRandomizerModifier modifier = challenge.modifier(MobDropRandomizerModifier.class).orElse(null);
        if (modifier == null) return;

        if (event.getEntity().getKiller() != null) {
            //TODO Change
            Team team = new Team(event.getEntity().getKiller().getUniqueId());

            List<ItemStack> newDrops = new ArrayList<>();
            for (ItemStack drop : event.getDrops()) {
                ItemBuilder builder = ItemBuilder.edit(drop);
                Material newMaterial = modifier.randomize(team, drop.getType());

                builder.material(newMaterial);
                newDrops.add(builder.build());
            }

            event.getDrops().clear();
            event.getDrops().addAll(newDrops);
        } else {
            event.getDrops().clear();
        }

    }
}
