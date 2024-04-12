package me.noci.challenges.challenge.modifiers.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.noci.challenges.challenge.modifiers.*;
import me.noci.challenges.gui.GuiAcceptDialog;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.gui.modifier.GuiTrafficLightModifier;
import me.noci.challenges.gui.modifier.ModifierCreateGui;
import me.noci.challenges.gui.modifier.ModifierProvider;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.utils.QuickItemStack;
import me.noci.quickutilities.utils.Require;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModifierRegistry {

    private static final List<RegisteredModifier<? extends ChallengeModifier>> MODIFIERS = Lists.newArrayList();

    static {
        var stopOnDeathDisplay = new QuickItemStack(InventoryConstants.woodPauseSkull()).displayName(Component.text("Stop on Death", NamedTextColor.RED));
        var enderDragonFinishDisplay = new QuickItemStack(Material.DRAGON_HEAD, Component.text("Ender Dragon Finish", NamedTextColor.GOLD));
        var timerDisplay = new QuickItemStack(Material.CLOCK, Component.text("Timer", NamedTextColor.DARK_AQUA));
        var trafficLightDisplay = new QuickItemStack(Material.GREEN_WOOL, Component.text("Traffic Light", NamedTextColor.GREEN));

        registerBasic(StopOnDeathModifier.class, stopOnDeathDisplay, StopOnDeathModifier::new);
        registerBasic(EnderDragonFinishModifier.class, enderDragonFinishDisplay, EnderDragonFinishModifier::new);
        registerBasic(TimerModifier.class, timerDisplay, TimerModifier::new);
        register(TrafficLightModifier.class, trafficLightDisplay, GuiTrafficLightModifier::new);
    }

    private static <T extends ChallengeModifier> void register(Class<T> type, QuickItemStack itemStack, ModifierCreateGuiSupplier<T> guiSupplier) {
        MODIFIERS.add(
                new RegisteredModifier<>(type, itemStack, (provider, player) ->
                        guiSupplier.get(provider, modifier -> {
                                    provider.onModifierAdd(ModifierCreator.of(type, itemStack, modifier));
                                    provider.provide(player);
                                }
                        ).provide(player))
        );
    }

    private static <T extends ChallengeModifier> void registerBasic(Class<T> type, QuickItemStack itemStack, Supplier<T> modifier) {
        MODIFIERS.add(new RegisteredModifier<>(type, itemStack, (provider, player) -> openAcceptDialog(type, itemStack, modifier, player, provider)));
    }

    private static <T extends ChallengeModifier> void openAcceptDialog(Class<T> type, QuickItemStack displayItem, Supplier<T> modifier, Player player, ModifierProvider provider) {
        TextColor primary = TextColor.color(99, 128, 101);
        TextColor gray = TextColor.color(122, 120, 120);

        GuiAcceptDialog.builder()
                .title(Component.text("Modifier hinzufügen?", primary))
                .description(
                        Component.newline(),
                        Component.text("Möchtest du den Modifier wirklich hinzufügen?", gray),
                        Component.newline(),
                        Component.text("Modifier Name: ", gray).append(Component.text(displayItem.getRawDisplayName(), primary))
                )
                .acceptAction(event -> {
                    provider.onModifierAdd(ModifierCreator.of(type, displayItem, modifier));
                    provider.provide(event.getPlayer());
                })
                .declineAction(event -> provider.provide(event.getPlayer()))
                .provide(player);
    }

    public static ImmutableList<RegisteredModifier<? extends ChallengeModifier>> modifiers() {
        return ImmutableList.copyOf(MODIFIERS);
    }

    public static class RegisteredModifier<T extends ChallengeModifier> {

        @Getter private final Class<T> type;
        @Getter private final QuickItemStack displayItem;
        private final BiConsumer<ModifierProvider, Player> onModifierAdd;

        private RegisteredModifier(Class<T> type, QuickItemStack displayItem, BiConsumer<ModifierProvider, Player> onModifierAdd) {
            this.type = type;
            this.displayItem = displayItem;
            this.onModifierAdd = onModifierAdd;
        }

        public void onModifierAdd(ModifierProvider creatorAddProvider, Player player) {
            Require.nonNull(creatorAddProvider);
            Require.nonNull(player);

            onModifierAdd.accept(creatorAddProvider, player);
        }
    }

    @FunctionalInterface
    private interface ModifierCreateGuiSupplier<T extends ChallengeModifier> {

        ModifierCreateGui<T> get(GuiProvider parentGui, Consumer<Supplier<T>> onModifierCreate);

    }


}
