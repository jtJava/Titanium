package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class CreativeE implements PacketCheck {

    //Fixes client-side crash books

    //A book with the nbt from below will crash a client when opened
    //{generation:0,pages:[0:"{translate:translation.test.invalid}",],author:"someone",title:"a",resolved:1b,}
    //{generation:0,pages:[0:"{translate:translation.test.invalid2}",],author:"someone",title:"a",resolved:1b,}

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
        List<String> pages = getPages(itemStack);
        if (pages.isEmpty()) {
            return false;
        }
        for (String page : pages) {
            String withOutSpaces = page.replaceAll("\\s", "");
            if (withOutSpaces.toLowerCase().contains("{translate:translation.test.invalid}") || withOutSpaces.contains("{translate:translation.test.invalid2}")) {
                return true;
            }
        }
        return false;
    }

    private List<String> getPages(ItemStack itemStack) {
        List<String> pageList = new ArrayList<>();

        if (itemStack.getNBT() != null) {
            NBTList<NBTString> nbtList = itemStack.getNBT().getStringListTagOrNull("pages");
            if (nbtList != null) {
                for (NBTString tag : nbtList.getTags()) {
                    pageList.add(tag.getValue());
                }
            }
        }

        return pageList;
    }

}
