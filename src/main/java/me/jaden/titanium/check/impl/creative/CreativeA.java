package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

import java.util.Base64;
import java.util.UUID;

public class CreativeA implements PacketCheck {


    //Fixes crash head / glitch head
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
            if (wrapper.getItemStack() != null) {
                if (wrapper.getItemStack().getNBT() != null) {
                    if (invalid(wrapper.getItemStack())) {
                        flag(event);
                    }
                }
            }
        }
    }

    //https://minecraft.fandom.com/wiki/Head
    private boolean invalid(ItemStack itemStack) {
        NBTCompound nbtCompound = itemStack.getNBT();
        if (nbtCompound == null) {
            return false;
        }
        if (!nbtCompound.getTags().containsKey("SkullOwner")) {
            return false;
        }
        NBTCompound skullOwner = nbtCompound.getCompoundTagOrNull("SkullOwner");
        if(skullOwner == null){
            System.out.println("skullowner == null");
            return true;
        }
        if (skullOwner.getTags().containsKey("Id")) {
            try {
                UUID.fromString(skullOwner.getStringTagValueOrNull("Id"));
            } catch (Exception e) {
                return true;
            }
        }
        if (skullOwner.getTags().containsKey("Properties")) {
            NBTCompound properties = skullOwner.getCompoundTagOrNull("Properties");
            if (properties == null) {
                System.out.println("props null");
                return true;
            }
            NBTList<NBTCompound> textures = properties.getCompoundListTagOrNull("textures");
            if (textures == null) {
                System.out.println("textures null");
                return true;
            }
            for (int i = 0; i < textures.size(); i++) {
                NBTCompound texture = textures.getTag(i);
                if (texture == null) {
                    return true;
                }
                if (!texture.getTags().containsKey("Value")) {
                    return true;
                }
                String value = texture.getStringTagValueOrNull("Value");
                String decoded;
                try {
                    decoded = new String(Base64.getDecoder().decode(value));
                } catch (Exception e) {
                    return true;
                }
                JsonObject jsonObject;
                try {
                    jsonObject = JsonParser.parseString(decoded).getAsJsonObject();
                } catch (Exception e) {
                    return true;
                }
                if (!jsonObject.has("textures")) {
                    return true;
                }
                jsonObject = jsonObject.getAsJsonObject("textures");
                if (!jsonObject.has("SKIN")) {
                    return true;
                }
                jsonObject = jsonObject.getAsJsonObject("SKIN");
                if (!jsonObject.has("url")) {
                    return true;
                }
                String url = jsonObject.get("url").getAsString();
                if (url.trim().length() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
