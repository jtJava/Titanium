package me.jaden.titanium.util;

import lombok.Getter;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class Ticker {
    @Getter
    private static Ticker instance;

    private int currentTick;

    private final BukkitTask task;

    public Ticker() {
        instance = this;

        Titanium plugin = Titanium.getPlugin();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> currentTick++, 1, 1);

        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            double maxPacketsPerSecond = TitaniumConfig.getInstance().getMaxPacketsPerSecond();
            double maxPacketAllowance = maxPacketsPerSecond * 3;

            for (PlayerData value : DataManager.getInstance().getPlayerData().values()) {
                value.setPacketAllowance(Math.min(maxPacketAllowance, value.getPacketAllowance() + maxPacketsPerSecond));
                value.setPacketCount(0);
                value.setBytesSent(0);
            }
        }, 0, 20);
    }
}
