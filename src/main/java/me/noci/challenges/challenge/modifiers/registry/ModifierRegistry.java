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
import me.noci.challenges.gui.modifier.DisplayItemSupplier;
import me.noci.challenges.gui.modifier.GuiTrafficLightModifier;
import me.noci.challenges.gui.modifier.ModifierApplier;
import me.noci.challenges.gui.modifier.ModifierCreateGui;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.utils.QuickItemStack;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModifierRegistry {

    private static final HashMap<Class<? extends ChallengeModifier>, Modifier> MODIFIERS = Maps.newHashMap();
    private static final HashMap<Class<? extends ChallengeModifier>, DisplayItemSupplier> DISPLAY_ITEMS = Maps.newHashMap();

    static {
        registerBasic(StopOnDeathModifier.class, StopOnDeathModifier::new, () -> InventoryConstants.woodPauseSkull().displayName(Option.Modifiers.STOP_ON_DEATH.get()));
        registerBasic(EnderDragonFinishModifier.class, EnderDragonFinishModifier::new, () -> new QuickItemStack(Material.DRAGON_HEAD, Option.Modifiers.ENDER_DRAGON_FINISH.get()));
        registerBasic(TimerModifier.class, TimerModifier::new, () -> new QuickItemStack(Material.CLOCK, Option.Modifiers.TIMER.get()));
        registerBasic(AllItemModifier.class, AllItemModifier::new, () -> new QuickItemStack(Material.CHEST, Option.Modifiers.ALL_ITEM.get()));
        register(TrafficLightModifier.class, GuiTrafficLightModifier::new, () -> new QuickItemStack(Material.GREEN_WOOL, Option.Modifiers.TRAFFIC_LIGHT.get()));
    }

    public static Stream<Modifier> modifiers() {
        return MODIFIERS.values().stream();
    }

    public static QuickItemStack displayItem(Class<? extends ChallengeModifier> modifier) {
        return DISPLAY_ITEMS.get(modifier).item();
    }

    private static <T extends ChallengeModifier> void register(Class<T> type, ModifierGuiSupplier<T> guiSupplier, DisplayItemSupplier displayItemSupplier) {
        registerDisplayItem(type, displayItemSupplier);

        ModifierCreator creator = (player, modifierApplier) -> guiSupplier.get(modifierApplier).provide(player);
        registerModifier(type, creator);
    }

    private static <T extends ChallengeModifier> void registerBasic(Class<T> type, Supplier<T> modifier, DisplayItemSupplier displayItemSupplier) {
        registerDisplayItem(type, displayItemSupplier);

        ModifierCreator creator = (player, modifierApplier) -> GuiAcceptDialog.builder()
                .title(Option.Gui.ModifierAcceptDialog.TITLE.get())
                .description(
                        Component.empty(),
                        Option.Gui.ModifierAcceptDialog.DESCRIPTION.get(),
                        Component.empty(),
                        Option.Gui.ModifierAcceptDialog.MODIFIER_NAME.resolve(Placeholder.unparsed("modifier_name", displayItemSupplier.item().getRawDisplayName()))
                )
                .acceptAction(event -> modifierApplier.apply(modifier.get()))
                .declineAction(event -> modifierApplier.cancel())
                .provide(player);

        registerModifier(type, creator);
    }


    private static <T extends ChallengeModifier> void registerModifier(Class<T> modifierType, ModifierCreator modifierCreator) {
        Require.checkState(!MODIFIERS.containsKey(modifierType), "Modifier type already registered.");
        MODIFIERS.put(modifierType, new Modifier(modifierType, modifierCreator));
    }

    private static void registerDisplayItem(Class<? extends ChallengeModifier> modifierType, DisplayItemSupplier displayItemSupplier) {
        Require.checkState(!DISPLAY_ITEMS.containsKey(modifierType), "Modifier type already registered.");
        DISPLAY_ITEMS.put(modifierType, displayItemSupplier);
    }

    @FunctionalInterface
    private interface ModifierGuiSupplier<T extends ChallengeModifier> {
        ModifierCreateGui<T> get(ModifierApplier applier);
    }


}
