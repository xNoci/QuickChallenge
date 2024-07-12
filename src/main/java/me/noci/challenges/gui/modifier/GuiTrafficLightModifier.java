package me.noci.challenges.gui.modifier;

import me.noci.challenges.QuickChallenge;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.inventory.GuiItem;
import me.noci.quickutilities.inventory.GuiProvider;
import me.noci.quickutilities.inventory.InventoryContent;
import me.noci.quickutilities.inventory.Slot;
import me.noci.quickutilities.utils.BukkitUnit;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.QuickItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiTrafficLightModifier extends ModifierCreateGui<TrafficLightModifier> {


    private BukkitUnit greenDurationUnit = BukkitUnit.SECONDS;
    private int greenDurationMin = 90;
    private int greenDurationMax = 300;

    private BukkitUnit yellowDurationUnit = BukkitUnit.TICKS;
    private int yellowDurationMin = 30;
    private int yellowDurationMax = 100;

    private BukkitUnit redDurationUnit = BukkitUnit.SECONDS;
    private int redDurationMin = 5;
    private int redDurationMax = 30;

    public GuiTrafficLightModifier(ModifierApplier modifierApplier) {
        super(modifierApplier, Option.Gui.TrafficLightModifier.TITLE.get(), InventoryConstants.FULL_SIZE);
    }

    @Override
    public void init(Player player, InventoryContent content) {
        content.fill(InventoryConstants.GLAS_PANE);

        content.setItem(Slot.getSlot(6, 4), new QuickItemStack(Material.RED_WOOL, Option.Gui.TrafficLightModifier.CANCEL.get()).asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            modifierApplier.cancel();
        }));

        content.setItem(Slot.getSlot(6, 6), new QuickItemStack(Material.GREEN_WOOL, Option.Gui.TrafficLightModifier.ADD_MODIFIER.get()).asGuiItem(event -> {
            if (event.getClick() != ClickType.LEFT) return;
            var greenDuration = TimeRange.of(greenDurationUnit, greenDurationMin, greenDurationMax);
            var yellowDuration = TimeRange.of(yellowDurationUnit, yellowDurationMin, yellowDurationMax);
            var redDuration = TimeRange.of(redDurationUnit, redDurationMin, redDurationMax);

            modifierApplier.apply(new TrafficLightModifier(greenDuration, yellowDuration, redDuration, LightStatus.GREEN));
        }));
    }

    @Override
    public void update(Player player, InventoryContent content) {
        settingColumn(
                content,
                Option.Gui.TrafficLightModifier.GREEN_PHASE.get(),
                3,
                SettingValues.of(() -> greenDurationUnit, unit -> greenDurationUnit = unit),
                SettingValues.of(() -> greenDurationMin, min -> greenDurationMin = min),
                SettingValues.of(() -> greenDurationMax, max -> greenDurationMax = max)
        );
        settingColumn(
                content,
                Option.Gui.TrafficLightModifier.YELLOW_PHASE.get(),
                5,
                SettingValues.of(() -> yellowDurationUnit, unit -> yellowDurationUnit = unit),
                SettingValues.of(() -> yellowDurationMin, min -> yellowDurationMin = min),
                SettingValues.of(() -> yellowDurationMax, max -> yellowDurationMax = max)
        );
        settingColumn(
                content,
                Option.Gui.TrafficLightModifier.RED_PHASE.get(),
                7,
                SettingValues.of(() -> redDurationUnit, unit -> redDurationUnit = unit),
                SettingValues.of(() -> redDurationMin, min -> redDurationMin = min),
                SettingValues.of(() -> redDurationMax, max -> redDurationMax = max)
        );
    }

    private void settingColumn(InventoryContent content, Component phase, int column, SettingValues<BukkitUnit> unit, SettingValues<Integer> min, SettingValues<Integer> max) {
        Config config = QuickChallenge.instance().config();

        Component lowDecrease = Option.Gui.TrafficLightModifier.LOW_DECREASE.get();
        Component highDecrease = Option.Gui.TrafficLightModifier.HIGH_DECREASE.get();
        Component lowIncrease = Option.Gui.TrafficLightModifier.LOW_INCREASE.get();
        Component highIncrease = Option.Gui.TrafficLightModifier.HIGH_INCREASE.get();

        GuiItem minItem = new QuickItemStack(Material.CHERRY_BUTTON)
                .displayName(config.get(Option.Gui.TrafficLightModifier.MIN_PHASE_VALUE, Placeholder.component("phase", phase)))
                .itemLore(
                        Component.empty(),
                        config.get(Option.Gui.TrafficLightModifier.CURRENT_VALUE, Formatter.number("value", min.get())),
                        Component.empty(),
                        lowDecrease,
                        highDecrease,
                        lowIncrease,
                        highIncrease
                )
                .asGuiItem(event -> {
                    int current = min.get();
                    int nextValue = switch (event.getClick()) {
                        case LEFT -> current - 1;
                        case RIGHT -> current + 1;
                        case SHIFT_LEFT -> current - 10;
                        case SHIFT_RIGHT -> current + 10;
                        default -> current;
                    };

                    nextValue = Math.clamp(nextValue, 1, 1000);
                    if (nextValue != current) {
                        min.set(nextValue);
                    }
                });

        GuiItem unitItem = new QuickItemStack(Material.GLOW_ITEM_FRAME)
                .displayName(config.get(Option.Gui.TrafficLightModifier.PHASE_TIME_UNIT, Placeholder.component("phase", phase)))
                .itemLore(
                        Component.empty(),
                        config.get(Option.Gui.TrafficLightModifier.CURRENT_VALUE, Placeholder.unparsed("value", unit.get().name())),
                        Component.empty(),
                        config.get(Option.Gui.TrafficLightModifier.PREVIOUS_UNIT, Placeholder.unparsed("unit", EnumUtils.previous(unit.get()).name())),
                        config.get(Option.Gui.TrafficLightModifier.NEXT_UNIT, Placeholder.unparsed("unit", EnumUtils.next(unit.get()).name()))
                )
                .asGuiItem(event -> {
                    BukkitUnit current = unit.get();

                    BukkitUnit newUnit = switch (event.getClick()) {
                        case LEFT -> EnumUtils.previous(current);
                        case RIGHT -> EnumUtils.next(current);
                        default -> current;
                    };

                    if (newUnit != current) {
                        unit.set(newUnit);
                    }
                });

        GuiItem maxItem = new QuickItemStack(Material.CHERRY_BUTTON)
                .displayName(config.get(Option.Gui.TrafficLightModifier.MAX_PHASE_VALUE, Placeholder.component("phase", phase)))
                .itemLore(
                        Component.empty(),
                        config.get(Option.Gui.TrafficLightModifier.CURRENT_VALUE, Formatter.number("value", max.get())),
                        Component.empty(),
                        lowDecrease,
                        highDecrease,
                        lowIncrease,
                        highIncrease
                )
                .asGuiItem(event -> {
                    int current = max.get();
                    int nextValue = switch (event.getClick()) {
                        case LEFT -> current - 1;
                        case RIGHT -> current + 1;
                        case SHIFT_LEFT -> current - 10;
                        case SHIFT_RIGHT -> current + 10;
                        default -> current;
                    };

                    nextValue = Math.clamp(nextValue, 1, 1000);

                    if (nextValue != current) {
                        max.set(nextValue);
                    }
                });

        content.setItem(Slot.getSlot(2, column), minItem);
        content.setItem(Slot.getSlot(3, column), unitItem);
        content.setItem(Slot.getSlot(4, column), maxItem);

    }

    private record SettingValues<T>(Supplier<T> getter, Consumer<T> setter) {
        public static <T> SettingValues<T> of(Supplier<T> getter, Consumer<T> setter) {
            return new SettingValues<>(getter, setter);
        }

        public T get() {
            return getter.get();
        }

        public void set(T value) {
            setter.accept(value);
        }

    }

}
