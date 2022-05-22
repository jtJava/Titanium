package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientNameItem;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;

// Yes, I know this isn't properly fixed. This should be fixed in the spigot.
public class CrasherC implements Check {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
            if (wrapper.getMessage().contains("${jndi:ldap")) {
                event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
            }
        } else if (event.getPacketType() == PacketType.Play.Client.NAME_ITEM) {
            WrapperPlayClientNameItem wrapper = new WrapperPlayClientNameItem(event);
            if (wrapper.getItemName().contains("$jndi:ldap")) {
                event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            if (wrapper.getChannelName().contains("$jndi:ldap")) {
                event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
            }
        }
    }

    @Override
    public void handle(PacketSendEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {
            WrapperPlayServerChatMessage wrapper = new WrapperPlayServerChatMessage(event);
            if (wrapper.getChatComponentJson().contains("$jndi:ldap")) {
                event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
            }
        }
    }
}
