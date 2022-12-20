package me.jaden.titanium.check.impl.creative.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import me.jaden.titanium.check.impl.creative.ItemCheck;

//Fixes CrashMap exploit
public class CreativeMap implements ItemCheck {

    @Override
    public boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound) {
        if (nbtCompound.getTags().containsKey("Decorations")) {
            NBTList<NBTCompound> decorations = nbtCompound.getCompoundListTagOrNull("Decorations");
            for (int i = 0; i < decorations.size(); i++) {
                NBTCompound decoration = decorations.getTag(i);
                if (decoration.getTags().containsKey("type")) {
                    NBTByte nbtByte = decoration.getTagOfTypeOrNull("type", NBTType.BYTE.getNBTClass());
                    if (nbtByte == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
