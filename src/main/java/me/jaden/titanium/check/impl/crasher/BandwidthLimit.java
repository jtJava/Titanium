package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class BandwidthLimit extends BaseCheck {

    //Value from ExploitFixer config
    //https://github.com/2lstudios-mc/ExploitFixer/blob/master/resources/config.yml
    private final int maxBytesPerSecond = TitaniumConfig.getInstance().getMaxBytesPerSecond();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        //https://netty.io/4.1/api/io/netty/buffer/ByteBuf.html
        //Sequential Access Indexing
        int readableBytes = ByteBufHelper.readableBytes(event.getByteBuf());
        int maxBytesPerSecond = this.maxBytesPerSecond * (event.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_8) ? 2 : 1);

        if (playerData.incrementBytesSent(readableBytes) > maxBytesPerSecond) {
            flagPacket(event, "Bytes Sent: " + playerData.getBytesSent() + " Max Bytes/s: " + maxBytesPerSecond);
        }
    }
}
