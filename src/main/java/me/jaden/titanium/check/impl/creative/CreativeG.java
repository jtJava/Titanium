package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

public class CreativeG implements PacketCheck {

    //This prevents the creation of buggy anvils that crash the client when placed
    //https://bugs.mojang.com/browse/MC-82677

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
        if (itemStack.getType() == ItemTypes.ANVIL) {
            return itemStack.getLegacyData() < 0 || itemStack.getLegacyData() > 2;
        }
        return false;
    }

}
