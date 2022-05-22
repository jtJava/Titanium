package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

// https://github.com/PaperMC/Paper/commit/ea2c81e4b9232447f9896af2aac4cd0bf62386fd
// https://wiki.vg/Inventory
public class CrasherA implements Check {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);

            if (!this.getPlayer(event).isPresent()) return;

            Player player = this.getPlayer(event).get();

            boolean packetCheck = data.isPossiblyViewingLectern();
            boolean bukkitCheck = player.getOpenInventory().getTopInventory().getType() == InventoryType.LECTERN;

            boolean playerInventory = wrapper.getWindowId() == 0;

            if ((packetCheck || bukkitCheck) && !playerInventory && wrapper.getWindowClickType() == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) {
                flag(event);            }
        }
    }

    @Override
    public void handle(PacketSendEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(event);
            data.setPossiblyViewingLectern(wrapper.getType() == 16 || wrapper.getLegacyType().equals("minecraft:lectern"));
        }
    }
}
