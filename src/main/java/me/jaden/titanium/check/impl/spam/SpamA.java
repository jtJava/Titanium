package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.util.Ticker;

public class SpamA implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            int currentTick = Ticker.getInstance().getCurrentTick();
            if (data.getLastBookEditTick() + 20 > currentTick) {
                flag(event);            } else {
                data.setLastBookEditTick(currentTick);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            // Make sure it's a book payload
            if (!(wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign"))) {
                return;
            }

            int currentTick = Ticker.getInstance().getCurrentTick();
            if (data.getLastBookEditTick() + 20 > currentTick) {
                flag(event);            } else {
                data.setLastBookEditTick(currentTick);
            }
        }

    }
}