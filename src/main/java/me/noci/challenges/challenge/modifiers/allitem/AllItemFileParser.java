package me.noci.challenges.challenge.modifiers.allitem;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllItemFileParser {

    private static final Logger logger = Logger.getLogger("AllItemsFormatter");
    private static final int START_CHAR = 0xE001;

    //All-Items spreadsheet https://docs.google.com/spreadsheets/d/1KOeIRDNQ3wyDcof5HucwPX1ITayj7Go6SdOafVQRyvk/edit?usp=sharing

    static void main(String[] args) {
        AllItemFileParser parser = new AllItemFileParser();
        List<ItemData> itemData = parser.readFile("/all_items.csv");

        String enumConstants = parser.generateEnumConstants(itemData);
        parser.writeToFile("allItem_gen/all_items.yml", enumConstants);

        String fontFile = parser.generateFontFile(itemData);
        parser.writeToFile("allItem_gen/all_items_font.json", fontFile);
    }

    private static String stringOrNull(String string) {
        if (string == null || string.isBlank()) return null;
        return string;
    }

    private static boolean stringToBoolean(String string) {
        return string.equalsIgnoreCase("true");
    }

    @SneakyThrows
    private void writeToFile(String fileName, String data) {
        Path path = Path.of(fileName);
        Files.createDirectories(path.getParent());
        Files.writeString(path, data, StandardCharsets.UTF_8);
    }

    private String generateFontFile(List<ItemData> itemDataList) {

        StringBuilder builder = new StringBuilder();

        var iterator = itemDataList.iterator();

        while (iterator.hasNext()) {
            ItemData data = iterator.next();
            String hexValue = Integer.toHexString(data.fontChar).toUpperCase();
            builder.append("""
                    {
                          "type":"bitmap",
                          "file":"%s",
                          "ascent":16,
                          "height":24,
                          "chars":[
                            "\\u%s"
                          ]
                        }
                    """.formatted(data.fileName(), hexValue));

            if (iterator.hasNext()) {
                builder.append(",");
            }
        }

        String jsonString = """
                {
                    "providers":[
                     {
                     "type":"space",
                     "advances":{
                   "\\uF001":16
                   }
                     },
                     %s
                    ]
                }
                """.formatted(builder.toString());

        return jsonString.replaceAll("\n", "").replaceAll(" ", "");
    }

    private String generateEnumConstants(List<ItemData> itemDataList) {
        StringBuilder builder = new StringBuilder();

        var iterator = itemDataList.iterator();

        while (iterator.hasNext()) {
            ItemData data = iterator.next();
            builder.append(data.key).append(":").append(System.lineSeparator());
            builder.append(" icon: '").append(data.fontChar).append("'").append(System.lineSeparator());

            if (data.ominousBanner) {
                builder.append(" ominous_banner: true").append(System.lineSeparator());
            } else if (data.abandonedCampMap != null) {
                builder.append(" abandoned_camp: ").append(data.abandonedCampMap).append(System.lineSeparator());
            } else if (data.potionType != null) {
                if (!data.bukkitMaterialName.equals("POTION")) {
                    builder.append(" material: ").append(data.bukkitMaterialName).append(System.lineSeparator());
                }
                builder.append(" potion: ").append(data.potionType).append(System.lineSeparator());
            } else {
                builder.append(" material: ").append(data.bukkitMaterialName).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    private List<ItemData> readFile(String resourceName) {
        var stream = AllItemFileParser.class.getResourceAsStream(resourceName);

        if (stream == null) {
            logger.info("Did not find the all_items file.");
            return List.of();
        }

        List<ItemData> data = new ArrayList<>();
        int fontChar = START_CHAR;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            //Skip header
            if (input.ready()) {
                input.readLine();
            }

            while (input.ready()) {
                String line = input.readLine();
                String[] values = line.split(",", 7);

                data.add(new ItemData(values[0], values[2], values[3], stringOrNull(values[4]),
                        stringToBoolean(values[5]), stringOrNull(values[6]), (char) (fontChar++)));
            }

            return Collections.unmodifiableList(data);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed reading input stream: ", e);
            return List.of();
        }
    }

    private record ItemData(String key, String bukkitMaterialName, String fileName,
                            @Nullable String potionType, boolean ominousBanner, @Nullable String abandonedCampMap,
                            char fontChar) {
    }

}
