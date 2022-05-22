package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleSetCarriedItem
public class InvalidD implements Check {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);

            if (!this.getPlayer(event).isPresent()) return;

            if (wrapper.getSlot() < 0 || wrapper.getSlot() >= 9) {
                flag(event);            }
        }
    }
}
