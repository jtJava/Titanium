package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import me.jaden.titanium.check.CreativeCheck;

import java.util.ArrayList;
import java.util.List;

public class CreativeE implements CreativeCheck {

    //Fixes client-side crash books

    //A book with the nbt from below will crash a client when opened
    //{generation:0,pages:[0:"{translate:translation.test.invalid}",],author:"someone",title:"a",resolved:1b,}
    //{generation:0,pages:[0:"{translate:translation.test.invalid2}",],author:"someone",title:"a",resolved:1b,}

    private List<String> getPages(NBTCompound nbtCompound) {
        List<String> pageList = new ArrayList<>();
        NBTList<NBTString> nbtList = nbtCompound.getStringListTagOrNull("pages");
        if (nbtList != null) {
            for (NBTString tag : nbtList.getTags()) {
                pageList.add(tag.getValue());
            }
        }
        return pageList;
    }
    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound nbtCompound) {
        List<String> pages = getPages(nbtCompound);
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

}
