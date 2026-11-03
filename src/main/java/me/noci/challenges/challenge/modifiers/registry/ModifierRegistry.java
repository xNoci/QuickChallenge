package me.noci.challenges.challenge.modifiers.registry;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.gui.modifier.GuiTrafficLightModifier;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.ItemBuilder;
import me.noci.quickutilities.utils.Require;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModifierRegistry {

    private static final HashMap<Class<? extends ChallengeModifier>, ModifierHolder<? extends ChallengeModifier>> MODIFIERS = Maps.newHashMap();

    static {
        registerModifier(StopOnDeathModifier.class, StopOnDeathModifier::new);
        registerModifier(EnderDragonFinishModifier.class, EnderDragonFinishModifier::new);
        registerModifier(TimerModifier.class, TimerModifier::new);
        registerModifier(AllItemModifier.class, AllItemModifier::new);
        registerModifier(TrafficLightModifier.class, TrafficLightModifier::new);
    }

    public static Collection<ModifierHolder<? extends ChallengeModifier>> modifiers() {
        return MODIFIERS.values();
    }


    private static <T extends ChallengeModifier> void registerModifier(Class<T> modifierType,
                                                                       Supplier<T> creator) {
        Require.checkState(!MODIFIERS.containsKey(modifierType), "Modifier type already registered.");
        MODIFIERS.put(modifierType, new ModifierHolder<>(modifierType, creator));
    }


}
