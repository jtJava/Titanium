package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import java.util.Optional;
import me.jaden.titanium.data.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface Check {
    default void flag(ProtocolPacketEvent<Object> event) {
        event.setCancelled(true);
        User user = event.getUser();
        try {
            user.sendPacket(new WrapperPlayServerDisconnect(Component.text("Timed out!")));
        } catch (Exception ignored) {
        }
        user.closeConnection();
        Bukkit.getLogger().info("[Titanium] Disconnected " + user.getProfile().getName() + " for flagging " + this.getClass().getSimpleName());
    }

    default ServerVersion getLowestServerVersion() {
        return ServerVersion.V_1_8_8;
    }

    default ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion();
    }

    default void handle(PacketReceiveEvent event, PlayerData playerData) {

    }

    default void handle(PacketSendEvent event, PlayerData playerData) {

    }

    default void onEvent(Event event) {

    }

    default Optional<Player> getPlayer(ProtocolPacketEvent<Object> event) {
        return Optional.ofNullable(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
