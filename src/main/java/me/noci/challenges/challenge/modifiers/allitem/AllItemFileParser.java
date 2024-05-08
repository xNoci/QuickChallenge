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

    //All-Items spreadsheet https://docs.google.com/spreadsheets/d/1KOeIRDNQ3wyDcof5HucwPX1ITayj7Go6SdOafVQRyvk/edit?usp=sharing

    public static void main(String[] args) {
        AllItemFileParser parser = new AllItemFileParser();
        List<ItemData> itemData = parser.readFile("/all_items.csv");

        String enumConstants = parser.generateEnumConstants(itemData);
        parser.writeToFile("allItem_gen/enumConstants.text", enumConstants);

        String fontFile = parser.generateFontFile(itemData);
        parser.writeToFile("allItem_gen/all_items.json", fontFile);
    }

    private static String stringOrNull(String string) {
        if (string == null || string.isBlank()) return null;
        return string;
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
        final int charUnicode = 0xE001;

        while (iterator.hasNext()) {
            ItemData data = iterator.next();
            String hexValue = Integer.toHexString(charUnicode + itemDataList.indexOf(data)).toUpperCase();
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

            builder.append(data.enumName())
                    .append("(\"")
                    .append(data.displayName())
                    .append("\", Material.")
                    .append(data.bukkitMaterialName());

            if (data.potionType() != null) {
                builder.append(", CustomMatcher.potion(PotionType.");
                builder.append(data.potionType());
                builder.append(")");
            } else if (data.translationKey() != null) {
                builder.append(", CustomMatcher.translation(\"");
                builder.append(data.translationKey());
                builder.append("\")");
            }

            builder.append(")");


            builder.append(iterator.hasNext() ? "," : ";");
            builder.append(System.lineSeparator());

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
        try (BufferedReader input = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            //Skip header
            if (input.ready()) {
                input.readLine();
            }

            while (input.ready()) {
                String line = input.readLine();
                String[] values = line.split(",", 6);

                data.add(new ItemData(values[0], values[1], values[2], values[3], stringOrNull(values[4]), stringOrNull(values[5])));
            }

            return Collections.unmodifiableList(data);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed reading input stream: ", e);
            return List.of();
        }
    }

    private record ItemData(String enumName, String bukkitMaterialName, String displayName, String fileName,
                            @Nullable String potionType, @Nullable String translationKey) {
    }

}
