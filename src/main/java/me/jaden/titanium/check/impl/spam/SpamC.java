package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;

public class SpamC implements Check {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (data.incrementPacketCount() > data.getPacketAllowance()) {
            System.out.println("kicked");
            event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
        } else {
            data.decrementPacketAllowance();
        }
    }
}
