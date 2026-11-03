package me.noci.challenges.challenge.modifiers.registry;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Namespaced;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

public class ModifierRegistry {

    private static final HashMap<Class<? extends ChallengeModifier>, ModifierHolder<? extends ChallengeModifier>> MODIFIERS = Maps.newHashMap();

    private static final Namespaced NAMESPACE = () -> "challenge";
    public static final Key STOP_ON_DEATH = Key.key(NAMESPACE, "stop-on-death");
    public static final Key ENDER_DRAGON_FINISH = Key.key(NAMESPACE, "ender-dragon-finish");
    public static final Key TIMER = Key.key(NAMESPACE, "timer");
    public static final Key ALL_ITEM = Key.key(NAMESPACE, "all-item");
    public static final Key TRAFFIC_LIGHT = Key.key(NAMESPACE, "traffic-light");

    static {
        registerModifier(STOP_ON_DEATH, StopOnDeathModifier.class, StopOnDeathModifier::new);
        registerModifier(ENDER_DRAGON_FINISH, EnderDragonFinishModifier.class, EnderDragonFinishModifier::new);
        registerModifier(TIMER, TimerModifier.class, TimerModifier::new);
        registerModifier(ALL_ITEM, AllItemModifier.class, AllItemModifier::new);
        registerModifier(TRAFFIC_LIGHT, TrafficLightModifier.class, TrafficLightModifier::new);
    }

    public static Collection<ModifierHolder<? extends ChallengeModifier>> modifiers() {
        return MODIFIERS.values();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChallengeModifier> ModifierHolder<T> fromType(Class<T> type) {
        return (ModifierHolder<T>) MODIFIERS.get(type);
    }

    private static <T extends ChallengeModifier> void registerModifier(Key key, Class<T> modifierType,
                                                                       Supplier<T> creator) {
        Require.checkState(!MODIFIERS.containsKey(modifierType), "Modifier type already registered.");
        MODIFIERS.put(modifierType, new ModifierHolder<>(key, modifierType, creator));
    }
}
