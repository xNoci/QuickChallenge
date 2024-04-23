package me.noci.challenges.challenge.modifiers.allitem;

public record CollectedItem(AllItem item, long timestamp) {
    public static CollectedItem now(AllItem item) {
        return new CollectedItem(item, System.currentTimeMillis());
    }
}
