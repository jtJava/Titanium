package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import java.util.Optional;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface Check {
    default ServerVersion getLowestServerVersion() {
        return ServerVersion.V_1_8_8;
    }

    default ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion();
    }

    void handle(PacketReceiveEvent event, PlayerData playerData);

    default void handle(PacketSendEvent event, PlayerData playerData) {

    }

    default Optional<Player> getPlayer(ProtocolPacketEvent<Object> event) {
        return Optional.ofNullable(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
