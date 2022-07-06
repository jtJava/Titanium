package me.jaden.titanium.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import java.util.Optional;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface Check {

    default void flagPacket(ProtocolPacketEvent<Object> event) {
        flagPacket(event, "");
    }

    void flagPacket(ProtocolPacketEvent<Object> event, String info);

    default void handle(PacketReceiveEvent event, PlayerData playerData) {

    }

    default void handle(PacketSendEvent event, PlayerData playerData) {

    }

    default Player getPlayer(ProtocolPacketEvent<Object> event) {
        return Optional.ofNullable((Player) event.getPlayer()).orElse(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
