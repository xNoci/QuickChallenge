package me.noci.challenges.serializer;

import me.noci.challenges.ExitStrategy;
import me.noci.challenges.TimeRange;
import me.noci.challenges.challenge.modifiers.TrafficLightModifier;
import me.noci.challenges.worlds.LastKnownLocation;
import me.noci.quickutilities.utils.BukkitUnit;
import org.bukkit.World;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class TypeSerializers {

    private static final Charset CHARSET = StandardCharsets.UTF_16;

    public static final TypeSerializer<Byte> BYTE = TypeSerializer.fixed(1, ByteBuffer::get, ByteBuffer::put);
    public static final TypeSerializer<Integer> U_BYTE = TypeSerializer.fixed(1, buffer -> buffer.get() & 0xFF, (buffer, value) -> buffer.put(value.byteValue()));
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
    public static final TypeSerializer<TrafficLightModifier.LightStatus> TRAFFIC_LIGHT_STATUS = TypeSerializer.enumSerializer(TrafficLightModifier.LightStatus.class);
    public static final TypeSerializer<TimeRange> TIME_RANGE = TypeSerializer.fixed(9, data -> TimeRange.of(BUKKIT_UNIT.read(data), INTEGER.read(data), INTEGER.read(data)), (buffer, value) -> {
        BUKKIT_UNIT.write(buffer, value.unit());
        INTEGER.write(buffer, value.min());
        INTEGER.write(buffer, value.max());
    });
    public static final TypeSerializer<World.Environment> ENVIRONMENT = TypeSerializer.enumSerializer(World.Environment.class);
    public static final TypeSerializer<LastKnownLocation> LAST_KNOWN_LOCATION = TypeSerializer.fixed(33, buffer -> new LastKnownLocation(DOUBLE.read(buffer), DOUBLE.read(buffer), DOUBLE.read(buffer), FLOAT.read(buffer), FLOAT.read(buffer), ENVIRONMENT.read(buffer)), (buffer, value) -> {
        DOUBLE.write(buffer, value.x());
        DOUBLE.write(buffer, value.y());
        DOUBLE.write(buffer, value.z());
        FLOAT.write(buffer, value.pitch());
        FLOAT.write(buffer, value.yaw());
        ENVIRONMENT.write(buffer, value.environment());
    });
    public static final TypeSerializer<Map<UUID, LastKnownLocation>> LAST_KNOWN_LOCATION_MAP = TypeSerializer.map(UUID, LAST_KNOWN_LOCATION);


}