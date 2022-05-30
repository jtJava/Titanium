package me.jaden.titanium;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.check.CheckManager;
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

    private TitaniumConfig titaniumConfig;

    private Ticker ticker;

    private DataManager dataManager;
    private CheckManager checkManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(true).bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        plugin = this;

        this.titaniumConfig = new TitaniumConfig(this);

        this.ticker = new Ticker();

        this.dataManager = new DataManager();
        this.checkManager = new CheckManager();

        if (!getServer().spigot().getConfig().getBoolean("settings.late-bind", true)) {
            Bukkit.getLogger().warning("[Titanium] Late bind is disabled, this can allow players" +
                    " to join your server before the plugin loads leaving you vulnerable to crashers.");
        }

        //bStats
        new Metrics(this, 15258);

        PacketEvents.getAPI().init();
    }
}
