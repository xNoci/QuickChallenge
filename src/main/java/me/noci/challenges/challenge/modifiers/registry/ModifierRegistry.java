package me.noci.challenges.challenge.modifiers.registry;

import com.google.common.collect.Maps;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.gui.GuiAcceptDialog;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.gui.modifier.GuiTrafficLightModifier;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.ItemBuilder;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class ModifierRegistry {

    private static final Map<Class<? extends ChallengeModifier>, ModifierHolder<? extends ChallengeModifier>> MODIFIERS = Maps.newHashMap();
    private static final Map<Class<? extends ChallengeModifier>, Supplier<ItemBuilder>> DISPLAY_ITEMS = Maps.newHashMap();

    private static final Namespaced NAMESPACE = () -> "challenge";
    public static final Key STOP_ON_DEATH = Key.key(NAMESPACE, "stop-on-death");
    public static final Key ENDER_DRAGON_FINISH = Key.key(NAMESPACE, "ender-dragon-finish");
    public static final Key TIMER = Key.key(NAMESPACE, "timer");
    public static final Key ALL_ITEM = Key.key(NAMESPACE, "all-item");
    public static final Key TRAFFIC_LIGHT = Key.key(NAMESPACE, "traffic-light");

    static {
        registerBasic(STOP_ON_DEATH, StopOnDeathModifier.class, StopOnDeathModifier::new,
                () -> InventoryConstants.woodPauseSkull().displayName(Option.Modifiers.STOP_ON_DEATH.get()));
        registerBasic(ENDER_DRAGON_FINISH, EnderDragonFinishModifier.class, EnderDragonFinishModifier::new,
                () -> ItemBuilder.of(Material.DRAGON_HEAD, Option.Modifiers.ENDER_DRAGON_FINISH.get()));
        registerBasic(TIMER, TimerModifier.class, TimerModifier::new,
                () -> ItemBuilder.of(Material.CLOCK, Option.Modifiers.TIMER.get()));
        registerBasic(ALL_ITEM, AllItemModifier.class, AllItemModifier::new,
                () -> ItemBuilder.of(Material.CHEST, Option.Modifiers.ALL_ITEM.get()));
        register(TRAFFIC_LIGHT, TrafficLightModifier.class, GuiTrafficLightModifier::new,
                () -> ItemBuilder.of(Material.GREEN_WOOL, Option.Modifiers.TRAFFIC_LIGHT.get()));
    }

    public static Collection<ModifierHolder<? extends ChallengeModifier>> modifiers() {
        return MODIFIERS.values();
    }

    public static ItemBuilder displayItem(Class<? extends ChallengeModifier> modifier) {
        return DISPLAY_ITEMS.get(modifier).get();
    }

    private static <T extends ChallengeModifier> void register(Key key, Class<T> type, ModifierCreator creator,
                                                               Supplier<ItemBuilder> item) {
        registerDisplayItem(type, item);
        registerModifier(key, type, creator);
    }

    private static <T extends ChallengeModifier> void registerBasic(Key key, Class<T> type, Supplier<T> modifier,
                                                                    Supplier<ItemBuilder> item) {
        ModifierCreator creator = (player, modifierApplier) -> GuiAcceptDialog.builder()
                .title(Option.Gui.ModifierAcceptDialog.TITLE.get())
                .description(
                        Component.empty(),
                        Option.Gui.ModifierAcceptDialog.DESCRIPTION.get(),
                        Component.empty(),
                        Option.Gui.ModifierAcceptDialog.MODIFIER_NAME.resolve(
                                Placeholder.component("modifier_name", item.get().build().effectiveName())
                        )
                )
                .acceptAction(_ -> modifierApplier.apply(player, modifier.get()))
                .declineAction(_ -> modifierApplier.cancel(player))
                .open(player);

        register(key, type, creator, item);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChallengeModifier> ModifierHolder<T> fromType(Class<T> type) {
        return (ModifierHolder<T>) MODIFIERS.get(type);
    }

    private static <T extends ChallengeModifier> void registerModifier(Key key, Class<T> modifierType,
                                                                       ModifierCreator creator) {
        Require.checkState(!MODIFIERS.containsKey(modifierType), "Modifier type already registered.");
        MODIFIERS.put(modifierType, new ModifierHolder<>(key, modifierType, creator));
    }

    private static void registerDisplayItem(Class<? extends ChallengeModifier> modifierType,
                                            Supplier<ItemBuilder> item) {
        Require.checkState(!DISPLAY_ITEMS.containsKey(modifierType), "Modifier type already registered.");
        DISPLAY_ITEMS.put(modifierType, item);
    }

}
