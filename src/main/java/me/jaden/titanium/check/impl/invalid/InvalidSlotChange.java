package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleSetCarriedItem
public class InvalidSlotChange extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);

            if (wrapper.getSlot() < 0 || wrapper.getSlot() >= 9) {
                flagPacket(event);
            }
        }
    }
}
