package me.noci.challenges.challenge.modifiers.allitem.custommatcher;

import lombok.Getter;
import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class PotionMatcher extends ItemMatcher {

    private final String prefix;
    private final String suffix;
    @Getter
    private final PotionType type;
    private final boolean isStrong;
    private final boolean isLong;

    public PotionMatcher(Material material, PotionType type) {
        super(material);
        this.type = type;
        this.prefix = switch (material) {
            case POTION -> "potion";
            case SPLASH_POTION -> "splash_potion";
            case LINGERING_POTION -> "lingering_potion";
            case TIPPED_ARROW -> "tipped_arrow";
            default -> throw new IllegalArgumentException("Invalid material for potion matcher");
        };
        this.isStrong = type.getPotionEffects().stream()
                .anyMatch(effect -> effect.getAmplifier() > 0);
        this.isLong = type.name().startsWith("LONG_");

        String key = type.getKey().getKey();
        if (key.startsWith("long_")) {
            key = key.substring(5);
        } else if (key.startsWith("strong")) {
            key = key.substring(7);
        }
        this.suffix = key;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (!super.matches(itemStack)) return false;
        if (!(itemStack.getItemMeta() instanceof PotionMeta potionMeta)) return false;
        return potionMeta.getBasePotionType() == type;
    }

    @Override
    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(material(), name()).potionType(type);
    }

    @Override
    public Component name() {
        Component name = super.name();

        if (isStrong) {
            name = name.append(Component.text(" (II)"));
        }

        if (isLong) {
            name = name.append(Component.text(" (II)"));
        }

        return name;
    }

    @Override
    protected String translationKey() {
        return "item.minecraft." + prefix + ".effect." + suffix;
    }
}
