package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.worlds.ChallengeWorld;
import me.noci.challenges.worlds.RespawnLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class PlayerRespawnListener implements Listener {

    private static final Logger LOGGER = LogManager.getLogger("RespawnListener");
    private final ChallengeController challengeController;

    public PlayerRespawnListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        var environment = player.getWorld().getEnvironment();

        //TODO "You have no home bed or respawn anchor, or it was obstructed"
        // When anchor or bad is destroyed/obstruceted

        challengeController.fromEntity(player)
                .flatMap(challenge -> {
                    ChallengeWorld world = challenge.challengeWorld().orElse(null);
                    if (world == null) return Optional.empty();
                    return challenge.respawnLocation(player)
                            .flatMap(location -> location.tryFindRespawnLocation(world))
                            .or(() -> world.worldByEnvironment(environment).map(World::getSpawnLocation));
                })
                .ifPresent(event::setRespawnLocation);
    }

    @EventHandler
    public void handleSpawnpoint(PlayerSetSpawnEvent event) {
        Player player = event.getPlayer();
        Location location = event.getLocation();

        challengeController.fromEntity(player)
                .ifPresent(challenge -> {
                    if (location == null) return;

                    Location loc = switch (event.getCause()) {
                        case RESPAWN_ANCHOR ->
                                Optional.ofNullable(player.getTargetBlockExact(5)).map(Block::getLocation).orElse(location);
                        case BED -> {
                            Block block = player.getTargetBlockExact(5);
                            if (block == null || !(block.getBlockData() instanceof Bed bed)) yield location;
                            yield block.getRelative(bed.getFacing(), bed.getPart() == Bed.Part.HEAD ? 0 : 1).getLocation();
                        }
                        default -> location;
                    };

                    RespawnLocation.Type type = switch (event.getCause()) {
                        case RESPAWN_ANCHOR -> RespawnLocation.Type.RESPAWN_ANCHOR;
                        case BED -> RespawnLocation.Type.BED;
                        default -> RespawnLocation.Type.OTHER;
                    };

                    challenge.respawnLocation(player, loc, type);
                    LOGGER.info("Set spawn location for uuid '{}' in challenge '{}' at '{}' with type '{}'", player.getUniqueId(), challenge.handle(), loc.toString(), type);
                });
    }

}
