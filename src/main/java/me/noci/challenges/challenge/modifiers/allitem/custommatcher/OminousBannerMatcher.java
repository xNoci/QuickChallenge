package me.noci.challenges.challenge.modifiers.allitem.custommatcher;

import com.google.common.collect.ImmutableList;
import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OminousBannerMatcher extends ItemMatcher {

    private static final List<Pattern> OMINOUS_PATTERNS = ImmutableList.of(
            new Pattern(DyeColor.CYAN, PatternType.RHOMBUS),
            new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM),
            new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER),
            new Pattern(DyeColor.LIGHT_GRAY, PatternType.BORDER),
            new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE),
            new Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL),
            new Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE),
            new Pattern(DyeColor.BLACK, PatternType.BORDER)
    );

    public OminousBannerMatcher() {
        super(Material.WHITE_BANNER);
    }

    @Override
    public boolean matches(@Nullable ItemStack itemStack) {
        if (!super.matches(itemStack)) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        if (!(itemMeta.displayName() instanceof TranslatableComponent component)) return false;
        return component.key().equals(translationKey());
    }

    @Override
    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(material(), name())
                .rarity(ItemRarity.UNCOMMON)
                .itemFlags()
                .bannerPatterns(OMINOUS_PATTERNS);
    }

    @Override
    protected String translationKey() {
        return "block.minecraft.ominous_banner";
    }
}
