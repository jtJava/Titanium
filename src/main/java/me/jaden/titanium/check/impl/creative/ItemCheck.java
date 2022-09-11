package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;

public interface ItemCheck {
    boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound);
}
