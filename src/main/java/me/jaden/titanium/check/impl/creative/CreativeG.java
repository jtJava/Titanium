package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import me.jaden.titanium.check.CreativeCheck;

public class CreativeG implements CreativeCheck {

    //This prevents the creation of buggy anvils that crash the client when placed
    //https://bugs.mojang.com/browse/MC-82677

    private boolean invalid(ItemStack itemStack) {
        if (itemStack.getType() == ItemTypes.ANVIL) {
            return itemStack.getLegacyData() < 0 || itemStack.getLegacyData() > 2;
        }
        return false;
    }

    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound nbtCompound) {
        if (invalid(clickedStack)) {
            return true;
        }
        if (nbtCompound.getTags().containsKey("id")) {
            String id = nbtCompound.getStringTagValueOrNull("id");
            if (id.contains("anvil")) {
                if (nbtCompound.getTags().containsKey("Damage")) {
                    NBTNumber damage = nbtCompound.getNumberTagOrNull("Damage");
                    return damage.getAsInt() > 3 || damage.getAsInt() < 0;
                }
            }
        }
        return false;
    }

}
