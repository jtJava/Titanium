package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.util.Ticker;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handlePlayerAction
public class SpamD implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.CRAFT_RECIPE_REQUEST) {
            int currentTick = Ticker.getInstance().getCurrentTick();
            if (data.getLastCraftRequestTick() + 10 > currentTick) {
                flag(event);
            } else {
                data.setLastCraftRequestTick(currentTick);
            }
        }
    }
}
