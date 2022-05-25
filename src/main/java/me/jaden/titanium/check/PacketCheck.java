package me.jaden.titanium.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatPosition;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import java.util.Optional;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.MessagesConfig;
import me.jaden.titanium.settings.PermissionsConfig;
import me.jaden.titanium.settings.TitaniumConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface PacketCheck extends BukkitCheck {

    default void flag(ProtocolPacketEvent<Object> event) {
        flag(event, "");
    }

    default void flag(ProtocolPacketEvent<Object> event, String info) {
        TitaniumConfig titaniumConfig = TitaniumConfig.getInstance();
        MessagesConfig messagesConfig = titaniumConfig.getMessagesConfig();
        PermissionsConfig permissionsConfig = titaniumConfig.getPermissionsConfig();

        User user = event.getUser();
        try {
            user.sendPacket(new WrapperPlayServerDisconnect(messagesConfig.getKickMessage(this.getClass().getSimpleName())));
        } catch (Exception ignored) {
        }
        user.closeConnection();

        for (User loopUser : DataManager.getInstance().getPlayerData().keySet()) {
            Player player = Bukkit.getPlayer(loopUser.getUUID());

            if (player == null) break;

            if (player.hasPermission(permissionsConfig.getNotificationPermission()) || player.isOp()) {
                loopUser.sendPacket(new WrapperPlayServerChatMessage(messagesConfig.getNotification(user.getName(), this.getClass().getSimpleName(), info), ChatPosition.CHAT));
            }
        }

        event.setCancelled(true);
    }

    default void handle(PacketReceiveEvent event, PlayerData playerData) {

    }

    default void handle(PacketSendEvent event, PlayerData playerData) {

    }

    default Optional<Player> getPlayer(ProtocolPacketEvent<Object> event) {
        return Optional.ofNullable(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
