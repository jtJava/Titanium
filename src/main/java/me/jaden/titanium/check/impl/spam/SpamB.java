package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.util.Ticker;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handlePlayerAction
public class SpamB implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

            if (wrapper.getAction() != DiggingAction.DROP_ITEM) return;

            if (!this.getPlayer(event).isPresent()) return;

            Player player = this.getPlayer(event).get();

            int currentTick = Ticker.getInstance().getCurrentTick();

            if (player.getGameMode() != GameMode.SPECTATOR) {
                // limit how quickly items can be dropped
                // If the ticks aren't the same then the count starts from 0 and we update the lastDropTick.
                if (data.getLastDropItemTick() != currentTick) {
                    data.setDropCount(0);
                    data.setLastDropItemTick(currentTick);
                } else {
                    // Else we increment the drop count and check the amount.
                    data.incrementDropCount();
                    if (data.getDropCount() >= 20) {
                        flag(event);                    }
                }
            }
        }
    }
}
