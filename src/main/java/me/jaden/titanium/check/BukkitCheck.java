package me.jaden.titanium.check;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface BukkitCheck extends Listener {
    @EventHandler
    default void onInventoryClick(InventoryClickEvent event) {

    }
}
