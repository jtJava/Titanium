package me.jaden.titanium;

import co.aikar.commands.PaperCommandManager;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.check.CheckManager;
import me.jaden.titanium.command.TitaniumCommand;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.settings.TitaniumConfig;
import me.jaden.titanium.util.Ticker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class Titanium extends JavaPlugin {
    @Getter
    private static Titanium plugin;

    private final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.AMPERSAND_CHAR)
            .hexCharacter(LegacyComponentSerializer.HEX_CHAR).build();

    private TitaniumConfig titaniumConfig;

    private Ticker ticker;

    private DataManager dataManager;
    private CheckManager checkManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        plugin = this;

        this.titaniumConfig = new TitaniumConfig(this);

        this.ticker = new Ticker();

        this.dataManager = new DataManager();
        this.checkManager = new CheckManager();

        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new TitaniumCommand());

        if (!getServer().spigot().getConfig().getBoolean("settings.late-bind", true)) {
            Bukkit.getLogger().warning("[Titanium] Late bind is disabled, this can allow players" +
                    " to join your server before the plugin loads leaving you vulnerable to crashers.");
        }

        //bStats
        new Metrics(this, 15258);
    }

    @Override
    public void onDisable() {
        this.ticker.getTask().cancel();
    }
}
