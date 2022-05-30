package me.jaden.titanium.check;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;

public interface CreativeCheck {

    boolean handleCheck(ItemStack clickedStack, NBTCompound nbtCompound);

}
