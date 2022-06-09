package me.jaden.titanium.check.impl.creative.impl;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import me.jaden.titanium.check.impl.creative.CreativeCheck;
import me.jaden.titanium.settings.TitaniumConfig;

public class PotionLimit implements CreativeCheck {
    //This prevents hacked potions that can do all sorts of annoying things (KillerPotions, NoRespawnPotions, TrollPotions)
    private final int maxPotionEffects = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffects();
    private final boolean allowNegativeAmplifiers = TitaniumConfig.getInstance().getCreativeConfig().isAllowNegativeAmplifiers();
    private final int maxPotionEffectAmplifier = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffectAmplifier();
    private final int maxPotionEffectDuration = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffectDuration();

    @Override
    public boolean handleCheck(ItemStack clickedStack, NBTCompound compound) {
        if (!compound.getTags().containsKey("CustomPotionEffects")) {
            return false;
        }

        NBTList<NBTCompound> potionEffects = compound.getCompoundListTagOrNull("CustomPotionEffects");

        //Limit how many custom potion effects a potion can have
        if (potionEffects.size() >= maxPotionEffects) {
            return true;
        }

        for (int i = 0; i < potionEffects.size(); i++) {
            NBTCompound effect = potionEffects.getTag(i);

            if (effect.getTags().containsKey("Duration")) {
                NBTNumber nbtNumber = effect.getNumberTagOrNull("Duration");
                if (nbtNumber != null) {
                    if (nbtNumber.getAsInt() >= maxPotionEffectDuration) {
                        return true;
                    }
                }
            }

            if (effect.getTags().containsKey("Amplifier")) {
                //This is weird, in wiki it says this is a byte,
                //but trying to get the byte tag allows hacked clients to bypass this check for some reason
                //It flags however, if they attempt to open their inventory after creating the potion
                NBTNumber nbtNumber = effect.getNumberTagOrNull("Amplifier");
                if (nbtNumber != null) {
                    if (nbtNumber.getAsInt() < 0 && !allowNegativeAmplifiers) {
                        return true;
                    }
                    if (nbtNumber.getAsInt() > maxPotionEffectAmplifier) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
