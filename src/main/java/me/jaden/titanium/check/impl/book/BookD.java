package me.jaden.titanium.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

// This is BookA for legacy servers which don't have the edit book packet
public class BookD implements PacketCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                com.github.retrooper.packetevents.protocol.item.ItemStack wrappedItemStack = wrapper.getItemStack().get();

                if (wrappedItemStack.getType() == ItemTypes.BOOK || wrappedItemStack.getType() == ItemTypes.WRITTEN_BOOK || wrappedItemStack.getType() == ItemTypes.WRITABLE_BOOK) {
                    flag(event);                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            // Make sure it's a book payload
            if (wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign")) {
                flag(event);            }
        } else if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            flag(event);        }
    }
}
