package me.noci.challenges.team;

import lombok.Getter;

import java.util.UUID;

public class Team {
    @Getter
    private final UUID uuid;

    public Team(UUID uuid) {
        this.uuid = uuid;
    }
}
