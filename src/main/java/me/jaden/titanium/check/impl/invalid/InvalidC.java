package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.entity.Player;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handlePickItem
public class InvalidC implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.PICK_ITEM) {
            WrapperPlayClientHeldItemChange wrapper = new WrapperPlayClientHeldItemChange(event);

            if (!this.getPlayer(event).isPresent()) return;

            Player player = this.getPlayer(event).get();

            if (!(wrapper.getSlot() >= 0 && wrapper.getSlot() < player.getInventory().getContents().length)) {
                flag(event);
            }
        }
    }
}
