package me.jaden.titanium.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.data.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class BukkitJoinListener implements Listener {
    private final Titanium titanium = Titanium.getPlugin();

    @EventHandler(ignoreCancelled = true)
    void onJoin(PlayerJoinEvent event) {
        DataManager dataManager = this.titanium.getDataManager();

        if (event.getPlayer().hasPermission(this.titanium.getTitaniumConfig().getPermissionsConfig().getNotificationPermission()) || event.getPlayer().isOp()) {
            dataManager.getPlayerData().keySet().stream()
                    .filter(user -> user.getUUID().equals(event.getPlayer().getUniqueId())).findFirst()
                    .ifPresent(user -> dataManager.getPlayerData(user).setReceivingAlerts(true));
        }

        User user = PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer());
        dataManager.getPlayerData(user).setClientVersion(user.getClientVersion());
    }
}
