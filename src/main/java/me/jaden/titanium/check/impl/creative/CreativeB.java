package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

import java.util.UUID;

public class CreativeB implements PacketCheck {

    //Fixes kick hopper item
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
        if (compound.getTags().containsKey("BlockEntityTag")) {
            NBTCompound blockEntityCompound = compound.getCompoundTagOrNull("BlockEntityTag");
            if (blockEntityCompound.getTags().containsKey("Items")) {
                NBTList<NBTCompound> items = blockEntityCompound.getCompoundListTagOrNull("Items");
                for (int i = 0; i < items.size(); i++) {
                    NBTCompound item = items.getTag(i);
                    if (item.getTags().containsKey("tag")) {
                        NBTCompound tag = item.getCompoundTagOrNull("tag");
                        if (tag.getTags().containsKey("SkullOwner")) {
                            NBTCompound skullOwner = tag.getCompoundTagOrNull("SkullOwner");
                            if (skullOwner == null) {
                                return true;
                            }
                            if (skullOwner.getTags().containsKey("Id")) {
                                try {
                                    UUID.fromString(skullOwner.getStringTagValueOrNull("Id"));
                                } catch (Exception e) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
