package me.noci.challenges.challenge.modifiers.trafficlight;

import lombok.Getter;
import me.noci.challenges.ResourcePack;
import net.kyori.adventure.text.Component;

@Getter
public enum LightStatus {
    GREEN(ResourcePack.TrafficLight.GREEN_LIGHT),
    YELLOW(ResourcePack.TrafficLight.YELLOW_LIGHT),
    RED(ResourcePack.TrafficLight.RED_LIGHT);

    private final Component texture;

    LightStatus(Component texture) {
        this.texture = texture;
    }
}
