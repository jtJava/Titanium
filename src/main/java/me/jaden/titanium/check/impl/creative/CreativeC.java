package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

public class CreativeC implements PacketCheck {

    //Fixes CrashMap exploit
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
            if (wrapper.getItemStack() != null) {
                if (invalid(wrapper.getItemStack())) {
                    flag(event);
                }
            }
        }
    }

    private boolean invalid(ItemStack itemStack) {
        if (itemStack.getNBT() == null) {
            return false;
        }
        NBTCompound compound = itemStack.getNBT();
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
