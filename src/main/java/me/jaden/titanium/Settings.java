package me.jaden.titanium;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class Settings {
    @Getter
    private static Settings settings;

    private final int maxPacketsPerSecond;

    private final int maxExplosions;

    private final int maxSignCharactersPerLine;

    private final boolean noBooks;
    private final int maxBookPageSize;
    private final double maxBookTotalSizeMultiplier;

    private Map<PacketTypeCommon, Double> multipliedPackets = new HashMap<>();

    public Settings(Titanium plugin) {
        settings = this;

        plugin.saveDefaultConfig();

        FileConfiguration configuration = plugin.getConfig();

        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("limits.max-packets-per-second", 1000)
                .put("fireworks.max-explosions", 25)
                .put("signs.max-characters-per-line", 16)
                .put("books.max-book-page-size", 2560)
                .put("books.max-book-total-size-multiplier", 0.98D)
                .put("books.no-books", false)
                .put("spam.multipliers",
                        ImmutableMap.<String, Object>builder()
                                .put("HELD_ITEM_CHANGE", 1.0D)
                                .put("ANIMATION", 1.0D)
                                .build()
                )
                .build());

        this.maxPacketsPerSecond = configuration.getInt("limits.max-packets-per-second", 1000);

        this.maxExplosions = configuration.getInt("fireworks.max-explosions", 25);

        this.maxSignCharactersPerLine = configuration.getInt("signs.max-characters-per-line", 16);

        this.maxBookPageSize = configuration.getInt("books.max-book-page-size", 2560);
        this.maxBookTotalSizeMultiplier = configuration.getDouble("books.max-book-page-size", 0.98D);

        this.noBooks = configuration.getBoolean("books.no-books", false);

        Map<String, Object> multiplierMap = configuration.getConfigurationSection("spam.multipliers").getValues(false);
        multiplierMap.forEach((packetType, multiplier) -> {
            String normalizedPacketType = packetType.toUpperCase().replace(" ", "_");
            this.multipliedPackets.put(PacketType.Play.Client.valueOf(normalizedPacketType), (Double) multiplier);
        });

    }
}
