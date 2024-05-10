package me.noci.challenges.serializer;

import me.noci.challenges.challenge.modifiers.allitem.AllItem;
import me.noci.challenges.challenge.modifiers.allitem.CollectedItem;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TimeRange;
import me.noci.quickutilities.utils.BukkitUnit;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    public static final TypeSerializer<String> STRING = TypeSerializer.dynamic(s -> s.getBytes(CHARSET).length, data -> new String(data.array(), CHARSET), (buffer, s) -> buffer.put(s.getBytes(CHARSET)));

    public static final TypeSerializer<BukkitUnit> BUKKIT_UNIT = TypeSerializer.enumSerializer(BukkitUnit.class);
    public static final TypeSerializer<LightStatus> TRAFFIC_LIGHT_STATUS = TypeSerializer.enumSerializer(LightStatus.class);
    public static final TypeSerializer<TimeRange> TIME_RANGE = TypeSerializer.fixed(9, data -> TimeRange.of(BUKKIT_UNIT.read(data), INTEGER.read(data), INTEGER.read(data)), (buffer, value) -> {
        BUKKIT_UNIT.write(buffer, value.unit());
        INTEGER.write(buffer, value.minDelay());
        INTEGER.write(buffer, value.maxDelay());
    });
    public static final TypeSerializer<AllItem> ALL_ITEM = TypeSerializer.enumSerializer(AllItem.class, U_SHORT);
    public static final TypeSerializer<CollectedItem> COLLECTED_ITEM_V1 = TypeSerializer.fixed(10,
            data -> new CollectedItem(ALL_ITEM.read(data), LONG.read(data), "Unbekannt", -1, false),
            (buffer, value) -> {
                ALL_ITEM.write(buffer, value.item());
                LONG.write(buffer, value.timestamp());
            }
    );
    public static final TypeSerializer<CollectedItem> COLLECTED_ITEM_V2 = TypeSerializer.dynamic(
            value -> ALL_ITEM.byteSize(value.item())
                    + LONG.byteSize(value.timestamp())
                    + STRING.byteSize(value.collectedBy())
                    + LONG.byteSize(value.collectedAfterTicks())
                    + BOOLEAN.byteSize(value.skipped()),
            data -> new CollectedItem(ALL_ITEM.read(data), LONG.read(data), STRING.read(data), LONG.read(data), BOOLEAN.read(data)),
            (buffer, value) -> {
                ALL_ITEM.write(buffer, value.item());
                LONG.write(buffer, value.timestamp());
                STRING.write(buffer, value.collectedBy());
                LONG.write(buffer, value.collectedAfterTicks());
                BOOLEAN.write(buffer, value.skipped());
            }
    );
    public static final TypeSerializer<List<CollectedItem>> COLLECTED_ITEM_LIST = TypeSerializer.list(COLLECTED_ITEM_V1);
    public static final TypeSerializer<List<CollectedItem>> COLLECTED_ITEM_LIST_V2 = TypeSerializer.list(COLLECTED_ITEM_V1, TypeSerializers.INTEGER);
    public static final TypeSerializer<List<CollectedItem>> COLLECTED_ITEM_LIST_V3 = TypeSerializer.list(COLLECTED_ITEM_V2, TypeSerializers.INTEGER);


}