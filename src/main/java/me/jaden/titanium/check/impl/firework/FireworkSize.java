package me.jaden.titanium.check.impl.firework;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.check.impl.creative.ItemCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

// PaperMC
public class FireworkSize extends BaseCheck implements ItemCheck {
    private final int maxExplosions = TitaniumConfig.getInstance().getMaxExplosions(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                if (this.invalid(wrapper.getItemStack().get())) flagPacket(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() != null) {
                if (this.invalid(wrapper.getCarriedItemStack())) flagPacket(event);
            }
        }
    }

    @Override
    public boolean handleCheck(PacketReceiveEvent event, ItemStack clickedStack, NBTCompound nbtCompound) {
        return invalid(clickedStack);
    }

    private boolean invalid(ItemStack itemStack) {
        if (itemStack.getNBT() != null) {
            NBTCompound fireworkNBT = itemStack.getNBT().getCompoundTagOrNull("Fireworks");
            if (fireworkNBT != null) {
                NBTList<NBTCompound> explosionsNBT = fireworkNBT.getCompoundListTagOrNull("Explosions");
                return explosionsNBT.size() >= this.maxExplosions;
            }
        }
        return false;
    }
}
