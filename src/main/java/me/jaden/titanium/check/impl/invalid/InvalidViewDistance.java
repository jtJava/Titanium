package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;

// https://github.com/PaperMC/Paper/commit/e3997543203bc1d86b58b6f1e751b0593228ca7b
public class InvalidViewDistance extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            wrapper.setViewDistance(Math.max(0, wrapper.getViewDistance()));
        }
    }
}
