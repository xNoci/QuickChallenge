package me.noci.challenges.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LightStatusArgument implements CustomArgumentType.Converted<LightStatus, String> {

    public static final LightStatusArgument ARGUMENT = new LightStatusArgument();

    private static final DynamicCommandExceptionType ERROR_INVALID_FLAVOR = new DynamicCommandExceptionType(
            light -> new LiteralMessage(light + " is not a valid light status!"));

    @Override
    public LightStatus convert(String nativeType) throws CommandSyntaxException {
        try {
            return LightStatus.valueOf(nativeType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw ERROR_INVALID_FLAVOR.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Arrays.stream(LightStatus.values())
                .map(LightStatus::name)
                .filter(light -> light.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
