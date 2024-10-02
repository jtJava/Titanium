package me.jaden.titanium.check;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import java.util.Optional;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.MessagesConfig;
import me.jaden.titanium.settings.TitaniumConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class BaseCheck implements Check {
    private final TitaniumConfig titaniumConfig = TitaniumConfig.getInstance();
    private final MessagesConfig messagesConfig = titaniumConfig.getMessagesConfig();

    @Override
    public void flagPacket(ProtocolPacketEvent event, String info, boolean kick) {
        event.setCancelled(true);

        User user = event.getUser();
        this.alert(user, info);
        if (kick) {
            this.disconnect(user);
        }
    }

    @Override
    public void flagPacket(ProtocolPacketEvent event, String info) {
        this.flagPacket(event, info, true);
    }

    @Override
    public void flagPacket(ProtocolPacketEvent event, boolean kick) {
        this.flagPacket(event, "", kick);
    }

    protected void disconnect(User user) {
        user.sendPacket(new WrapperPlayServerDisconnect(messagesConfig.getKickMessage(this.getClass().getSimpleName())));
        user.closeConnection();
    }

    protected void alert(User user, String info) {
        Component component = messagesConfig.getNotification(user.getName(), this.getClass().getSimpleName(), info);
        Bukkit.getLogger().info(messagesConfig.getComponentSerializer().serialize(component));
        for (PlayerData playerData : DataManager.getInstance().getPlayerData().values()) {
            if (playerData.isReceivingAlerts()) {
                playerData.getUser().sendMessage(component);
            }
        }
    }

    protected Player getPlayer(ProtocolPacketEvent event) {
        return Optional.ofNullable((Player) event.getPlayer()).orElse(Bukkit.getPlayer(event.getUser().getUUID()));
    }
}
