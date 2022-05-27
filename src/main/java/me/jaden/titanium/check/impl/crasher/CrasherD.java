package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class CrasherD implements PacketCheck {

    //Value from ExploitFixer config
    //https://github.com/2lstudios-mc/ExploitFixer/blob/master/resources/config.yml
    private final int maxBytes = TitaniumConfig.getInstance().getMaxBytes() == -1 ? 9999999 : TitaniumConfig.getInstance().getMaxBytes();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        //https://netty.io/4.1/api/io/netty/buffer/ByteBuf.html
        //Sequential Access Indexing
        int capacity = ByteBufHelper.capacity(event.getByteBuf());
        if (capacity > maxBytes) {
            flag(event, "Bytes: " + capacity + " Max Bytes: " + maxBytes);
        }
    }
}
