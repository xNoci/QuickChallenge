package me.noci.challenges.challenge.modifiers.allitem.custommatcher;

import me.noci.quickutilities.utils.ItemBuilder;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class AbandonedCampMapMatcher extends ItemMatcher {

    private final String translationKey;

    public AbandonedCampMapMatcher(String translationKey) {
        super(Material.ABANDONED_CAMP_MAP);
        this.translationKey = translationKey;
    }

    @Override
    public boolean matches(@Nullable ItemStack itemStack) {
        if (!super.matches(itemStack)) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        if (!(itemMeta.itemName() instanceof TranslatableComponent component)) return false;
        return component.key().equals(translationKey());
    }

    @Override
    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(material(), name());
    }


    @Override
    protected String translationKey() {
        return translationKey;
    }
}
