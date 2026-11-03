package me.noci.challenges.gui.modifier;

import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.gui.InventoryConstants;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.gui.Gui;
import me.noci.quickutilities.gui.GuiItem;
import me.noci.quickutilities.utils.EnumUtils;
import me.noci.quickutilities.utils.InventoryPattern;
import me.noci.quickutilities.utils.ItemBuilder;
import me.noci.quickutilities.utils.time.BukkitUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiTrafficLightModifier {

    private final Gui gui;
    private final ModifierApplier modifierApplier;

    private BukkitUnit greenDurationUnit = BukkitUnit.SECONDS;
    private int greenDurationMin = 90;
    private int greenDurationMax = 300;

    private BukkitUnit yellowDurationUnit = BukkitUnit.TICKS;
    private int yellowDurationMin = 30;
    private int yellowDurationMax = 100;

    private BukkitUnit redDurationUnit = BukkitUnit.SECONDS;
    private int redDurationMin = 5;
    private int redDurationMax = 30;


    public GuiTrafficLightModifier(HumanEntity entity, ModifierApplier modifierApplier) {
        this.gui = createGui();
        this.modifierApplier = modifierApplier;
        updateSettingColumns();
        gui.open(entity);
    }

    private Gui createGui() {
        Gui gui = Gui.gui()
                .rows(6)
                .title(Option.Gui.TrafficLightModifier.TITLE.get())
                .create();

        gui.fillPattern(InventoryConstants.GLAS_PANE, InventoryPattern.FULL);
        gui.setItem(4, 6,
                ItemBuilder.of(Material.RED_WOOL, Option.Gui.TrafficLightModifier.CANCEL.get()).asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    modifierApplier.cancel(event.getWhoClicked());
                }));
        gui.setItem(6, 6, ItemBuilder.of(Material.GREEN_WOOL, Option.Gui.TrafficLightModifier.ADD_MODIFIER.get())
                .asGuiItem(event -> {
                    if (event.getClick() != ClickType.LEFT) return;
                    TimeRange greenDuration = TimeRange.of(greenDurationUnit, greenDurationMin, greenDurationMax);
                    TimeRange yellowDuration = TimeRange.of(yellowDurationUnit, yellowDurationMin, yellowDurationMax);
                    TimeRange redDuration = TimeRange.of(redDurationUnit, redDurationMin, redDurationMax);

                    TrafficLightModifier modifier = new TrafficLightModifier(greenDuration, yellowDuration, redDuration,
                            LightStatus.GREEN);

                    modifierApplier.apply(event.getWhoClicked(), modifier);
                }));

        return gui;
    }

    private void updateSettingColumns() {
        settingColumn(
                Option.Gui.TrafficLightModifier.GREEN_PHASE.get(),
                3,
                SettingValues.of(() -> greenDurationUnit, unit -> greenDurationUnit = unit),
                SettingValues.of(() -> greenDurationMin, min -> greenDurationMin = min),
                SettingValues.of(() -> greenDurationMax, max -> greenDurationMax = max)
        );
        settingColumn(
                Option.Gui.TrafficLightModifier.YELLOW_PHASE.get(),
                5,
                SettingValues.of(() -> yellowDurationUnit, unit -> yellowDurationUnit = unit),
                SettingValues.of(() -> yellowDurationMin, min -> yellowDurationMin = min),
                SettingValues.of(() -> yellowDurationMax, max -> yellowDurationMax = max)
        );
        settingColumn(
                Option.Gui.TrafficLightModifier.RED_PHASE.get(),
                7,
                SettingValues.of(() -> redDurationUnit, unit -> redDurationUnit = unit),
                SettingValues.of(() -> redDurationMin, min -> redDurationMin = min),
                SettingValues.of(() -> redDurationMax, max -> redDurationMax = max)
        );

        gui.update();
    }

    private void settingColumn(Component phase,
                               int column,
                               SettingValues<BukkitUnit> unit,
                               SettingValues<Integer> min,
                               SettingValues<Integer> max) {
        Component lowDecrease = Option.Gui.TrafficLightModifier.LOW_DECREASE.get();
        Component highDecrease = Option.Gui.TrafficLightModifier.HIGH_DECREASE.get();
        Component lowIncrease = Option.Gui.TrafficLightModifier.LOW_INCREASE.get();
        Component highIncrease = Option.Gui.TrafficLightModifier.HIGH_INCREASE.get();

        GuiItem minItem = ItemBuilder.of(Material.CHERRY_BUTTON)
                .displayName(
                        Option.Gui.TrafficLightModifier.MIN_PHASE_VALUE.resolve(Placeholder.component("phase", phase))
                )
                .lore(currentValueLore(lowDecrease, highDecrease, lowIncrease, highIncrease, min.get()))
                .asGuiItem(event -> updateValue(event, min));

        GuiItem unitItem = ItemBuilder.of(Material.GLOW_ITEM_FRAME)
                .displayName(
                        Option.Gui.TrafficLightModifier.PHASE_TIME_UNIT.resolve(Placeholder.component("phase", phase)))
                .lore(unitLore(unit.get()))
                .asGuiItem(event -> updateUnit(event, unit));

        GuiItem maxItem = ItemBuilder.of(Material.CHERRY_BUTTON)
                .displayName(
                        Option.Gui.TrafficLightModifier.MAX_PHASE_VALUE.resolve(Placeholder.component("phase", phase))
                )
                .lore(currentValueLore(lowDecrease, highDecrease, lowIncrease, highIncrease, max.get()))
                .asGuiItem(event -> updateValue(event, max));

        gui.setItem(column, 2, minItem);
        gui.setItem(column, 3, unitItem);
        gui.setItem(column, 4, maxItem);

    }

    private void updateValue(InventoryClickEvent event, SettingValues<Integer> value) {
        int current = value.get();
        int nextValue = switch (event.getClick()) {
            case LEFT -> current - 1;
            case RIGHT -> current + 1;
            case SHIFT_LEFT -> current - 10;
            case SHIFT_RIGHT -> current + 10;
            default -> current;
        };

        nextValue = Math.clamp(nextValue, 1, 1000);
        if (nextValue != current) {
            value.set(nextValue);
        }

        updateSettingColumns();
    }

    private void updateUnit(InventoryClickEvent event, SettingValues<BukkitUnit> value) {
        BukkitUnit current = value.get();

        BukkitUnit newUnit = switch (event.getClick()) {
            case LEFT -> EnumUtils.previous(current);
            case RIGHT -> EnumUtils.next(current);
            default -> current;
        };

        if (newUnit != current) {
            value.set(newUnit);
        }

        updateSettingColumns();
    }

    private List<Component> unitLore(BukkitUnit unit) {
        return List.of(
                Component.empty(),
                Option.Gui.TrafficLightModifier.CURRENT_VALUE.resolve(
                        Placeholder.unparsed("value", unit.name())
                ),
                Component.empty(),
                Option.Gui.TrafficLightModifier.PREVIOUS_UNIT.resolve(
                        Placeholder.unparsed("unit", EnumUtils.previous(unit).name())
                ),
                Option.Gui.TrafficLightModifier.NEXT_UNIT.resolve(
                        Placeholder.unparsed("unit", EnumUtils.next(unit).name())
                )
        );
    }

    private List<Component> currentValueLore(Component lowDecrease,
                                             Component highDecrease,
                                             Component lowIncrease,
                                             Component highIncrease, int currentValue) {
        return List.of(Component.empty(),
                Option.Gui.TrafficLightModifier.CURRENT_VALUE.resolve(Formatter.number("value", currentValue)),
                Component.empty(),
                lowDecrease,
                highDecrease,
                lowIncrease,
                highIncrease);
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
