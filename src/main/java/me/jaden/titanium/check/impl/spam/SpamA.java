package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.util.Ticker;

public class SpamA extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            if (invalid(data)) flagPacket(event);
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            // Make sure it's a book payload
            if (!(wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign"))) {
                return;
            }

            if (invalid(data)) flagPacket(event);
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);
            if (wrapper.getItemStack().isPresent()) {
                ItemStack itemStack = wrapper.getItemStack().get();

                if (itemStack.getType() == ItemTypes.WRITABLE_BOOK || itemStack.getType() == ItemTypes.WRITTEN_BOOK) {
                    if (invalid(data)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);

            ItemStack itemStack = wrapper.getCarriedItemStack();

            if (itemStack.getType() == ItemTypes.WRITABLE_BOOK || itemStack.getType() == ItemTypes.WRITTEN_BOOK) {
                if (invalid(data)) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);

            ItemStack itemStack = wrapper.getItemStack();

            if (itemStack.getType() == ItemTypes.WRITABLE_BOOK || itemStack.getType() == ItemTypes.WRITTEN_BOOK) {
                if (invalid(data)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean invalid(PlayerData data) {
        int currentTick = Ticker.getInstance().getCurrentTick();
        if (data.getLastBookEditTick() + 20 > currentTick) {
            return true;
        } else {
            data.setLastBookEditTick(currentTick);
            return false;
        }
    }
}