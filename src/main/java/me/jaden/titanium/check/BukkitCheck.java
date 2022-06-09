package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

public abstract class BukkitCheck extends BaseCheck implements Listener {
    public void flagBukkit(Player player, String info, Cancellable cancellable) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        disconnect(user);
        alert(user, info);

        cancellable.setCancelled(true);
    }

    public void flagBukkit(Player player, Cancellable cancellable) {
        this.flagBukkit(player, "", cancellable);
    }
}
