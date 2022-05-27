package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class CreativeF implements PacketCheck {
    //This prevents hacked potions that can do all sorts of annoying things (KillerPotions, NoRespawnPotions, TrollPotions)
    private final int maxPotionEffects = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffects();
    private final boolean allowNegativeAmplifiers = TitaniumConfig.getInstance().getCreativeConfig().isAllowNegativeAmplifiers();
    private final int maxPotionEffectAmplifier = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffectAmplifier();
    private final int maxPotionEffectDuration = TitaniumConfig.getInstance().getCreativeConfig().getMaxPotionEffectDuration();

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
                NBTInt nbtInt = effect.getTagOfTypeOrNull("Duration", NBTInt.class);
                if (nbtInt != null) {
                    if (nbtInt.getAsInt() >= maxPotionEffectDuration) {
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
