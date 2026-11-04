package me.noci.challenges.challenge.modifiers.allitem;

import me.noci.challenges.utils.TimerUtils;
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
        return TimerUtils.convertTicksToText(collectedAfterTicks);
    }

}
