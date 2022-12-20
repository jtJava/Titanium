package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ImpossiblePacket extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() == null) {
                return;
            }

            Material packetMaterial = SpigotReflectionUtil.encodeBukkitItemStack(wrapper.getCarriedItemStack()).getType();

            Player player = getPlayer(event);
            if (!player.getInventory().contains(packetMaterial)) {
                flagPacket(event, "Clicked Item Material: " + packetMaterial, false);
                player.updateInventory();
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (!wrapper.getItemStack().isPresent()) {
                return;
            }

            Material packetMaterial = SpigotReflectionUtil.encodeBukkitItemStack(wrapper.getItemStack().get()).getType();

            boolean modern = PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_8_8);
            List<Material> possibleItemStacks = modern ?
                    Arrays.asList(getPlayer(event).getInventory().getItemInMainHand().getType(), getPlayer(event).getInventory().getItemInOffHand().getType()) :
                    Collections.singletonList(getPlayer(event).getItemInHand().getType());

            if (!possibleItemStacks.contains(packetMaterial)) {
                wrapper.getItemStack().get().setNBT(new NBTCompound());
                flagPacket(event, "Placed Item Material: " + packetMaterial, false);
            }
        }
    }
}
