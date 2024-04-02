package me.noci.challenges.challenge.modifiers;

import me.noci.challenges.challenge.Challenge;

public abstract class DefaultChallengeModifier implements ChallengeModifier {

    protected final Challenge challenge;

    public DefaultChallengeModifier(Challenge challenge) {
        this.challenge = challenge;
    }

}
