package me.noci.challenges.challenge.modifiers.allitem;

import me.noci.quickutilities.utils.time.BukkitUnit;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record CollectedItem(@Setting("item") AllItem item,
                            @Setting("timestamp") long timestamp,
                            @Setting("collected-by") String collectedBy,
                            @Setting("collected-after-ticks") long collectedAfterTicks,
                            @Setting("skipped") boolean skipped) {

    public static CollectedItem now(AllItem item, String collectedBy, long collectedAfterTicks, boolean skipped) {
        return new CollectedItem(item, System.currentTimeMillis(), collectedBy, collectedAfterTicks, skipped);
    }

    public String collectedAfter() {
        long timePlayedSeconds = BukkitUnit.TICKS.toSeconds(collectedAfterTicks);

        long seconds = timePlayedSeconds % 60;
        long minutes = timePlayedSeconds / 60 % 60;
        long hours = timePlayedSeconds / 3600 % 24;
        long days = timePlayedSeconds / 86400;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        builder.append(minutes).append("m ");
        builder.append(seconds).append("s");

        return builder.toString();
    }

}
