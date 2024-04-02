package me.noci.challenges;

import org.apache.logging.log4j.LogManager;

import java.util.function.Predicate;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomHolder {

    public static RandomGenerator random() {
        return Random.RANDOM;
    }

    private static class Random {
        private static final RandomGenerator RANDOM;

        static {
            var creator = RandomGeneratorFactory.all()
                    .filter(Predicate.not(RandomGeneratorFactory::isDeprecated))
                    .filter(RandomGeneratorFactory::isSplittable)
                    .findFirst().orElse(RandomGeneratorFactory.of("SplittableRandom"));

            LogManager.getLogger("RandomHolder").info("Using '%s' as random generator.".formatted(creator.name()));

            RANDOM = creator.create();
        }

    }

}
