package me.noci.challenges.serializer;

import me.noci.challenges.ExitStrategy;
import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.challenges.worlds.ChallengeLocation;
import me.noci.challenges.worlds.RespawnLocation;
import me.noci.quickutilities.utils.BukkitUnit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeSerializers {

    private static final Charset CHARSET = StandardCharsets.UTF_16;

    public static final TypeSerializer<Byte> BYTE = TypeSerializer.fixed(1, ByteBuffer::get, ByteBuffer::put);
    public static final TypeSerializer<Integer> U_BYTE = TypeSerializer.fixed(1, buffer -> buffer.get() & 0xFF, (buffer, value) -> buffer.put(value.byteValue()));
    public static final TypeSerializer<Integer> U_SHORT = TypeSerializer.fixed(2, buffer -> buffer.getShort() & 0xFFFF, (buffer, value) -> buffer.putShort(value.shortValue()));
    public static final TypeSerializer<Short> SHORT = TypeSerializer.fixed(2, ByteBuffer::getShort, ByteBuffer::putShort);
    public static final TypeSerializer<Integer> INTEGER = TypeSerializer.fixed(4, ByteBuffer::getInt, ByteBuffer::putInt);
    public static final TypeSerializer<Long> LONG = TypeSerializer.fixed(8, ByteBuffer::getLong, ByteBuffer::putLong);
    public static final TypeSerializer<Float> FLOAT = TypeSerializer.fixed(4, ByteBuffer::getFloat, ByteBuffer::putFloat);
    public static final TypeSerializer<Double> DOUBLE = TypeSerializer.fixed(8, ByteBuffer::getDouble, ByteBuffer::putDouble);
    public static final TypeSerializer<Boolean> BOOLEAN = TypeSerializer.fixed(1, buffer -> buffer.get() != 0, (buffer, value) -> buffer.put((byte) (value ? 1 : 0)));
    public static final TypeSerializer<UUID> UUID = TypeSerializer.fixed(16, buffer -> new UUID(buffer.getLong(), buffer.getLong()), (buffer, uuid) -> {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
    });
    public static final TypeSerializer<String> STRING = TypeSerializer.dynamic(s -> s.getBytes(CHARSET).length, data -> new String(data.array(), CHARSET), (buffer, s) -> buffer.put(s.getBytes(CHARSET)));
    public static final TypeSerializer<BukkitUnit> BUKKIT_UNIT = TypeSerializer.enumSerializer(BukkitUnit.class);
    public static final TypeSerializer<ExitStrategy> EXIT_STRATEGY = TypeSerializer.enumSerializer(ExitStrategy.class);
    public static final TypeSerializer<LightStatus> TRAFFIC_LIGHT_STATUS = TypeSerializer.enumSerializer(LightStatus.class);
    public static final TypeSerializer<RespawnLocation.Type> RESPAWN_TYPE = TypeSerializer.enumSerializer(RespawnLocation.Type.class);
    public static final TypeSerializer<TimeRange> TIME_RANGE = TypeSerializer.fixed(9, data -> TimeRange.of(BUKKIT_UNIT.read(data), INTEGER.read(data), INTEGER.read(data)), (buffer, value) -> {
        BUKKIT_UNIT.write(buffer, value.unit());
        INTEGER.write(buffer, value.min());
        INTEGER.write(buffer, value.max());
    });
    public static final TypeSerializer<World.Environment> ENVIRONMENT = TypeSerializer.enumSerializer(World.Environment.class);
    public static final TypeSerializer<ChallengeLocation> CHALLENGE_LOCATION = TypeSerializer.fixed(33, buffer -> new ChallengeLocation(DOUBLE.read(buffer), DOUBLE.read(buffer), DOUBLE.read(buffer), FLOAT.read(buffer), FLOAT.read(buffer), ENVIRONMENT.read(buffer)), (buffer, value) -> {
        DOUBLE.write(buffer, value.x());
        DOUBLE.write(buffer, value.y());
        DOUBLE.write(buffer, value.z());
        FLOAT.write(buffer, value.yaw());
        FLOAT.write(buffer, value.pitch());
        ENVIRONMENT.write(buffer, value.environment());
    });
    public static final TypeSerializer<RespawnLocation> RESPAWN_LOCATION = TypeSerializer.fixed(34, buffer -> new RespawnLocation(CHALLENGE_LOCATION.read(buffer), RESPAWN_TYPE.read(buffer)), (buffer, value) -> {
        CHALLENGE_LOCATION.write(buffer, value.challengeLocation());
        RESPAWN_TYPE.write(buffer, value.type());
    });
    public static final TypeSerializer<Map<UUID, ChallengeLocation>> CHALLENGE_LOCATION_MAP = TypeSerializer.map(UUID, CHALLENGE_LOCATION);
    public static final TypeSerializer<Map<UUID, RespawnLocation>> RESPAWN_LOCATION_MAP = TypeSerializer.map(UUID, RESPAWN_LOCATION);
    public static final TypeSerializer<ItemStack> ITEM_STACK = TypeSerializer.dynamic(
            value -> ItemStackSerialization.toBytes(value).length,
            data -> ItemStackSerialization.fromBytes(data.array()),
            (buffer, value) -> buffer.put(ItemStackSerialization.toBytes(value))
    );
    public static final TypeSerializer<List<ItemStack>> ITEM_STACK_LIST = TypeSerializer.list(ITEM_STACK);
    public static final TypeSerializer<Map<UUID, List<ItemStack>>> ITEM_STACK_LIST_MAP = TypeSerializer.map(UUID, ITEM_STACK_LIST);
    public static final TypeSerializer<AllItem> ALL_ITEM = TypeSerializer.enumSerializer(AllItem.class, U_SHORT);
    public static final TypeSerializer<CollectedItem> COLLECTED_ITEM = TypeSerializer.fixed(10,
            data -> new CollectedItem(ALL_ITEM.read(data), LONG.read(data)),
            (buffer, value) -> {
                ALL_ITEM.write(buffer, value.item());
                LONG.write(buffer, value.timestamp());
            }
    );
    public static final TypeSerializer<List<CollectedItem>> COLLECTED_ITEM_LIST = TypeSerializer.list(COLLECTED_ITEM);


}