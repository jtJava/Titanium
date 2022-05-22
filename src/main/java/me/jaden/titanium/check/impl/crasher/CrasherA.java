package me.jaden.titanium.check.impl.crasher;

import me.jaden.titanium.check.PacketCheck;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

// https://github.com/PaperMC/Paper/commit/ea2c81e4b9232447f9896af2aac4cd0bf62386fd
// https://wiki.vg/Inventory
public class CrasherA implements PacketCheck {
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.LECTERN) {
            event.setCancelled(true);
        }
    }
}
