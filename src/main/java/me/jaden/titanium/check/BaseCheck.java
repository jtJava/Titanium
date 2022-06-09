package me.jaden.titanium.check;

import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.settings.MessagesConfig;
import me.jaden.titanium.settings.TitaniumConfig;

public abstract class BaseCheck implements Check {
    private final TitaniumConfig titaniumConfig = TitaniumConfig.getInstance();
    private final MessagesConfig messagesConfig = titaniumConfig.getMessagesConfig();

    @Override
    public void flagPacket(ProtocolPacketEvent<Object> event, String info) {
        event.setCancelled(true);

        User user = event.getUser();
        this.disconnect(user);
        this.alert(user, info);
    }

    protected void disconnect(User user) {
        user.sendPacket(new WrapperPlayServerDisconnect(messagesConfig.getKickMessage(this.getClass().getSimpleName())));
        user.closeConnection();
    }

    protected void alert(User user, String info) {
        DataManager.getInstance().getPlayerData().forEach((loopUser, playerData) -> {
            if (playerData.isReceivingAlerts()) {
                loopUser.sendMessage(messagesConfig.getNotification(user.getName(), this.getClass().getSimpleName(), info));
            }
        });
    }
}
