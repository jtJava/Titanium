package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class CrasherE extends BaseCheck {

    //Value from ExploitFixer config
    //https://github.com/2lstudios-mc/ExploitFixer/blob/master/resources/config.yml
    private final int maxBytesPerSecond = TitaniumConfig.getInstance().getMaxBytesPerSecond();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        //https://netty.io/4.1/api/io/netty/buffer/ByteBuf.html
        //Sequential Access Indexing
        int capacity = ByteBufHelper.capacity(event.getByteBuf());
        if (playerData.incrementBytesSent(capacity) > maxBytesPerSecond) {
            flagPacket(event, "Bytes Sent: " + playerData.getBytesSent() + " Max Bytes/s: " + maxBytesPerSecond);
        }
    }
}
