package me.jaden.titanium.settings;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.Titanium;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class TitaniumConfig {
    @Getter
    private static TitaniumConfig instance;

    private final MessagesConfig messagesConfig;
    private final PermissionsConfig permissionsConfig;
    private final CreativeConfig creativeConfig;

    private final int maxPacketsPerSecond;

    private final int maxExplosions;

    private final int maxSignCharactersPerLine;

    private final boolean noBooks;
    private final int maxBookPageSize;
    private final double maxBookTotalSizeMultiplier;

    private final int maxBytes;
    private final int maxBytesPerSecond;

    private List<String> disallowedCommands;

    private Map<PacketTypeCommon, Double> multipliedPackets = new HashMap<>();

    public TitaniumConfig(Titanium plugin) {
        instance = this;

        plugin.saveDefaultConfig();

        FileConfiguration configuration = plugin.getConfig();
        this.messagesConfig = new MessagesConfig(configuration);
        this.permissionsConfig = new PermissionsConfig(configuration);
        this.creativeConfig = new CreativeConfig(configuration);

        configuration.addDefaults(ImmutableMap.<String, Object>builder()

                .put("limits.max-packets-per-second", 1000)
                .put("limits.max-bytes", 64000)

                .put("fireworks.max-explosions", 25)

                .put("signs.max-characters-per-line", 16)

                .put("books.max-book-page-size", 2560)
                .put("books.max-book-total-size-multiplier", 0.98D)
                .put("books.no-books", false)

                .put("spam.multipliers",
                        ImmutableMap.<String, Object>builder()
                                .put("PLAYER_POSITION", 0.5D)
                                .put("PLAYER_POSITION_AND_ROTATION", 0.5D)
                                .put("PLAYER_ROTATION", 0.5D)
                                .put("PLAYER_FLYING", 0.5D)
                                .put("HELD_ITEM_CHANGE", 1.0D)
                                .put("ANIMATION", 1.0D)
                                .build()
                )

                .put("commands", Arrays.asList(
                                "//calc",
                                "//calculate",
                                "//eval",
                                "//evaluate",
                                "//solve",
                                "//asc",
                                "//ascend",
                                "//desc",
                                "//descend",
                                "/to",
                                "/hd readtext",
                                "/hologram readtext",
                                "/holographicdisplays readtext",
                                "/pex promote",
                                "/pex demote",
                                "/promote",
                                "/demote",
                                "/execute"
                        )
                )
                .build());

        this.maxPacketsPerSecond = configuration.getInt("limits.max-packets-per-second", 1000);
        this.maxBytes = configuration.getInt("limits.max-bytes", 64000);
        this.maxBytesPerSecond = configuration.getInt("limits.max-bytes-per-second", 64000);

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

        this.disallowedCommands = configuration.getStringList("commands");
    }
}
