package me.jaden.titanium.check.impl.creative;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

public class CreativeD implements PacketCheck {
    private static final ClientVersion CLIENT_VERSION = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
            for (Enchantment enchantment : wrapper.getItemStack().getEnchantments(CLIENT_VERSION)) {
                if (enchantment.getLevel() < 0) {
                    flag(event, "Enchantment: " + enchantment.getType().getName() + " Level: " + enchantment.getLevel());
                }
            }
        }
    }
}
