package me.jaden.titanium;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class Settings {
    @Getter
    private static Settings settings;

    private final boolean noBooks;

    private final int maxPacketsPerSecond;

    private final int maxBookPageSize; // default paper value
    private final double maxBookTotalSizeMultiplier; // default paper value

    public Settings(Titanium plugin) {
        settings = this;

        plugin.saveDefaultConfig();

        FileConfiguration configuration = plugin.getConfig();

        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("limits.max-packets-per-second", 1000)
                .put("books.max-book-page-size", 2560)
                .put("books.max-book-total-size-multiplier", 0.98D)
                .put("books.no-books", false)
                .build());

        this.maxPacketsPerSecond = configuration.getInt("limits.max-packets-per-second", 1000);
        this.maxBookPageSize = configuration.getInt("books.max-book-page-size", 2560);
        this.maxBookTotalSizeMultiplier = configuration.getDouble("books.max-book-page-size", 0.98D);
        this.noBooks = configuration.getBoolean("books.no-books", false);
    }
}
