package me.jaden.titanium.check.impl.crasher;

import me.jaden.titanium.check.BukkitCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

// https://github.com/PaperMC/Paper/commit/ea2c81e4b9232447f9896af2aac4cd0bf62386fd
// https://wiki.vg/Inventory
public class CrasherA extends BukkitCheck {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.LECTERN) {
            if (event.getWhoClicked() instanceof Player) {
                flagBukkit((Player) event.getWhoClicked(), "Inventory: Lectern", event);
            }
        }
    }
}
