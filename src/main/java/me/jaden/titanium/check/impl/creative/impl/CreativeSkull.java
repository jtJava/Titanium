package me.jaden.titanium.check.impl.creative.impl;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Base64;
import java.util.UUID;
import me.jaden.titanium.check.impl.creative.CreativeCheck;

//Fixes crash head / glitch head
public class CreativeSkull implements CreativeCheck {

    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound nbtCompound) {
        if (nbtCompound == null) {
            return false;
        }

        if (!nbtCompound.getTags().containsKey("SkullOwner")) {
            return false;
        }

        NBTCompound skullOwner = nbtCompound.getCompoundTagOrNull("SkullOwner");
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

        if (skullOwner.getTags().containsKey("Properties")) {
            NBTCompound properties = skullOwner.getCompoundTagOrNull("Properties");
            if (properties == null) {
                return true;
            }

            NBTList<NBTCompound> textures = properties.getCompoundListTagOrNull("textures");
            if (textures == null) {
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

                if (!(url.startsWith("http://textures.minecraft.net/texture/") || url.startsWith("https://textures.minecraft.net/texture/"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
