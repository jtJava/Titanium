package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

// https://github.com/PaperMC/Paper/commit/ea2c81e4b9232447f9896af2aac4cd0bf62386fd
// https://wiki.vg/Inventory
public class CrasherA implements Check {
    @Override
    public void onEvent(Event event) {
        if (event instanceof InventoryClickEvent) {
            InventoryClickEvent inventoryClickEvent = (InventoryClickEvent) event;
            if (inventoryClickEvent.getClickedInventory() != null && inventoryClickEvent.getClickedInventory().getType() == InventoryType.LECTERN) {
                inventoryClickEvent.setCancelled(true);
            }
        }
    }
}
