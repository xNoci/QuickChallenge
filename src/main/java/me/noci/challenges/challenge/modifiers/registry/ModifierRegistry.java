package me.noci.challenges.challenge.modifiers.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.ChallengeModifier;
import me.noci.challenges.challenge.modifiers.EnderDragonFinishModifier;
import me.noci.challenges.challenge.modifiers.StopOnDeathModifier;
import me.noci.challenges.challenge.modifiers.TimerModifier;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.gui.GuiAcceptDialog;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.gui.modifier.GuiTrafficLightModifier;
import me.noci.challenges.gui.modifier.ModifierCreateGui;
import me.noci.challenges.gui.modifier.ModifierProvider;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.utils.QuickItemStack;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModifierRegistry {

    //TODO REWRITE REGISTRY

    private static final List<RegisteredModifier<? extends ChallengeModifier>> MODIFIERS = Lists.newArrayList();

    static {
        Supplier<QuickItemStack> stopOnDeathDisplay = () -> InventoryConstants.woodPauseSkull().displayName(Option.Modifiers.STOP_ON_DEATH.get());
        Supplier<QuickItemStack> enderDragonFinishDisplay = () -> new QuickItemStack(Material.DRAGON_HEAD, Option.Modifiers.ENDER_DRAGON_FINISH.get());
        Supplier<QuickItemStack> timerDisplay = () -> new QuickItemStack(Material.CLOCK, Option.Modifiers.TIMER.get());
        Supplier<QuickItemStack> allItemDisplay = () -> new QuickItemStack(Material.CHEST, Option.Modifiers.ALL_ITEM.get());
        Supplier<QuickItemStack> trafficLightDisplay = () -> new QuickItemStack(Material.GREEN_WOOL, Option.Modifiers.TRAFFIC_LIGHT.get());

        registerBasic(StopOnDeathModifier.class, stopOnDeathDisplay, StopOnDeathModifier::new);
        registerBasic(EnderDragonFinishModifier.class, enderDragonFinishDisplay, EnderDragonFinishModifier::new);
        registerBasic(TimerModifier.class, timerDisplay, TimerModifier::new);
        registerBasic(AllItemModifier.class, allItemDisplay, AllItemModifier::new);
        register(TrafficLightModifier.class, trafficLightDisplay, GuiTrafficLightModifier::new);
    }

    private static <T extends ChallengeModifier> void register(Class<T> type, Supplier<QuickItemStack> itemSupplier, ModifierCreateGuiSupplier<T> guiSupplier) {
        MODIFIERS.add(
                new RegisteredModifier<>(type, itemSupplier, (provider, player) ->
                        guiSupplier.get(provider, modifier -> {
                                    provider.onModifierAdd(ModifierCreator.of(type, itemSupplier.get(), modifier));
                                    provider.provide(player);
                                }
                        ).provide(player))
        );
    }

    private static <T extends ChallengeModifier> void registerBasic(Class<T> type, Supplier<QuickItemStack> itemStack, Supplier<T> modifier) {
        MODIFIERS.add(new RegisteredModifier<>(type, itemStack, (provider, player) -> openAcceptDialog(type, itemStack, modifier, player, provider)));
    }

    private static <T extends ChallengeModifier> void openAcceptDialog(Class<T> type, Supplier<QuickItemStack> displayItem, Supplier<T> modifier, Player player, ModifierProvider provider) {
        Config config = QuickChallenge.instance().config();
        QuickItemStack item = displayItem.get();
        GuiAcceptDialog.builder()
                .title(config.get(Option.Gui.ModifierAcceptDialog.TITLE))
                .description(
                        Component.newline(),
                        config.get(Option.Gui.ModifierAcceptDialog.DESCRIPTION),
                        Component.newline(),
                        config.get(Option.Gui.ModifierAcceptDialog.DESCRIPTION, Placeholder.unparsed("modifier_name", item.getRawDisplayName()))
                )
                .acceptAction(event -> {
                    provider.onModifierAdd(ModifierCreator.of(type, item, modifier));
                    provider.provide(event.getPlayer());
                })
                .declineAction(event -> provider.provide(event.getPlayer()))
                .provide(player);
    }

    public static ImmutableList<RegisteredModifier<? extends ChallengeModifier>> modifiers() {
        return ImmutableList.copyOf(MODIFIERS);
    }

    @FunctionalInterface
    private interface ModifierCreateGuiSupplier<T extends ChallengeModifier> {

        ModifierCreateGui<T> get(GuiProvider parentGui, Consumer<Supplier<T>> onModifierCreate);

    }

    public static class RegisteredModifier<T extends ChallengeModifier> {

        @Getter private final Class<T> type;
        private final Supplier<QuickItemStack> itemSupplier;
        private final BiConsumer<ModifierProvider, Player> onModifierAdd;

        private RegisteredModifier(Class<T> type, Supplier<QuickItemStack> itemSupplier, BiConsumer<ModifierProvider, Player> onModifierAdd) {
            this.type = type;
            this.itemSupplier = itemSupplier;
            this.onModifierAdd = onModifierAdd;
        }

        public void onModifierAdd(ModifierProvider creatorAddProvider, Player player) {
            Require.nonNull(creatorAddProvider);
            Require.nonNull(player);

            onModifierAdd.accept(creatorAddProvider, player);
        }

        public QuickItemStack displayItem() {
            return itemSupplier.get();
        }

    }


}
