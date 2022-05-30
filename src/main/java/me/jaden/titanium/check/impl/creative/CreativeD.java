package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.CreativeCheck;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

public class CreativeD implements CreativeCheck {
    private static final ClientVersion CLIENT_VERSION = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();

    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound nbtCompound) {
        if (nbtCompound.getTags().containsKey("ench")) {
            NBTList<NBTCompound> enchantments = nbtCompound.getCompoundListTagOrNull("ench");
            for (int i = 0; i < enchantments.size(); i++) {
                NBTCompound enchantment = enchantments.getTag(i);
                if (enchantment.getTags().containsKey("lvl")) {
                    NBTNumber number = enchantment.getNumberTagOrNull("lvl");
                    System.out.println(number.getAsInt());
                    if (number.getAsInt() < 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
