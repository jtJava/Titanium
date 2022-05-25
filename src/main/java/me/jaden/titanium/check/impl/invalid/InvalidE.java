package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.google.common.base.Charsets;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

// Paper 1.8.8
// org/bukkit/craftbukkit/entity/CraftPlayer.java:1209
public class InvalidE implements PacketCheck {

    //Fixes console spammer with register/unregister payloads

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            String payload = new String(wrapper.getData(), Charsets.UTF_8);

            String[] channels = payload.split("\0");

            if (wrapper.getChannelName().equals("REGISTER")) {
                if (playerData.getChannels().size() + channels.length > 124) {
                    flag(event);
                } else {
                    for (String channel : channels) {
                        playerData.getChannels().add(channel);
                    }
                }

                if (payload.split("\0").length > 124) {
                    flag(event);
                }
            } else if (wrapper.getChannelName().equals("UNREGISTER")) {
                for (String channel : channels) {
                    playerData.getChannels().remove(channel);
                }
            }
        }
    }
}
