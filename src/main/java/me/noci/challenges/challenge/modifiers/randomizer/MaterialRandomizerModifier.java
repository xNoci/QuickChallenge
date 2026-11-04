package me.noci.challenges.challenge.modifiers.randomizer;

import com.google.common.collect.Maps;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public abstract class MaterialRandomizerModifier extends RandomizerModifier<Material, Material> {

    private static final Supplier<Map<Material, Material>> CACHE_SUPPLIER = () -> Maps.newEnumMap(Material.class);
    private static final Material[] MATERIALS = Arrays.stream(Material.values())
            .filter(Predicate.not(Material::isLegacy)).filter(Material::isItem).toArray(Material[]::new);

    public MaterialRandomizerModifier(String name) {
        super(name, CACHE_SUPPLIER);
    }


    @Override
    protected Material randomize(Long seed, Material value, RandomProvider generator) {
        RandomGenerator randomGenerator = generator.create(seed + value.ordinal());
        return MATERIALS[randomGenerator.nextInt(MATERIALS.length)];
    }

}
