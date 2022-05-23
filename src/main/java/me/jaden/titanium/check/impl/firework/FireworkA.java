package me.jaden.titanium.check.impl.firework;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import me.jaden.titanium.Settings;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleEditBook
public class FireworkA implements PacketCheck {
    private final int maxExplosions = Settings.getSettings().getMaxExplosions(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                if (this.invalid(wrapper.getItemStack().get())) flag(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);

            if (wrapper.getItemStack() != null) {
                if (this.invalid(wrapper.getItemStack())) flag(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() != null) {
                if (this.invalid(wrapper.getCarriedItemStack())) flag(event);
            }
        }
    }

    private boolean invalid(ItemStack itemStack) {
        if (itemStack.getNBT() != null) {
            NBTCompound fireworkNBT = itemStack.getNBT().getCompoundTagOrNull("Fireworks");
            if (fireworkNBT != null) {
                NBTList<NBTCompound> explosionsNBT = fireworkNBT.getCompoundListTagOrNull("Explosions");
                return explosionsNBT.size() <= this.maxExplosions;
            }
        }
        return false;
    }
}
