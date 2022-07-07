package me.jaden.titanium.check.impl.crasher;

import me.jaden.titanium.Titanium;
import me.jaden.titanium.check.BukkitCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CrasherB extends BukkitCheck {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!(event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)) return;
        Player player = event.getPlayer();
        Location location = event.getTo();
        if(location == null) return;
        event.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(Titanium.getPlugin(), () -> player.teleport(location), 5);
    }

}
