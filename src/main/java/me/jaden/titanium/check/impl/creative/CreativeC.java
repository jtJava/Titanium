package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import me.jaden.titanium.check.CreativeCheck;

public class CreativeC implements CreativeCheck {

    //Fixes CrashMap exploit
    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound compound) {
        if (compound.getTags().containsKey("Decorations")) {
            NBTList<NBTCompound> decorations = compound.getCompoundListTagOrNull("Decorations");
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
