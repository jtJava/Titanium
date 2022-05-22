package me.jaden.titanium.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import java.util.ArrayList;
import java.util.List;
import me.jaden.titanium.Settings;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleEditBook
public class BookA implements PacketCheck {
    private final int maxBookPageSize = Settings.getSettings().getMaxBookPageSize(); // default paper value
    private final double maxBookTotalSizeMultiplier = Settings.getSettings().getMaxBookTotalSizeMultiplier(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        List<String> pageList = new ArrayList<>();

        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook wrapper = new WrapperPlayClientEditBook(event);
            pageList.addAll(wrapper.getPages());
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

            // Make sure it's a book payload
            if (!(wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign")))
                return;

            ItemStack wrappedItemStack = wrapper.readItemStack();

            pageList.addAll(this.getPages(wrappedItemStack));
        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                pageList.addAll(this.getPages(wrapper.getItemStack().get()));
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);

            if (wrapper.getItemStack() != null) {
                pageList.addAll(this.getPages(wrapper.getItemStack()));
            }
        }

        long byteTotal = 0;
        double multiplier = Math.min(1D, this.maxBookTotalSizeMultiplier);
        long byteAllowed = this.maxBookPageSize;

        for (String testString : pageList) {
            int byteLength = testString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            if (byteLength > 256 * 4) {
                // page too large
                flag(event);
                return;
            }
            byteTotal += byteLength;
            int length = testString.length();
            int multibytes = 0;
            if (byteLength != length) {
                for (char c : testString.toCharArray()) {
                    if (c > 127) {
                        multibytes++;
                    }
                }
            }
            byteAllowed += (this.maxBookPageSize * Math.min(1, Math.max(0.1D, (double) length / 255D))) * multiplier;

            if (multibytes > 1) {
                // penalize MB
                byteAllowed -= multibytes;
            }
        }

        if (byteTotal > byteAllowed) {
            // book too large
            flag(event);
        }
    }

    private List<String> getPages(ItemStack itemStack) {
        List<String> pageList = new ArrayList<>();

        if (itemStack.getNBT() != null) {
            NBTList<NBTString> nbtList = itemStack.getNBT().getStringListTagOrNull("pages");
            if (nbtList != null) {
                for (NBTString tag : nbtList.getTags()) {
                    pageList.add(tag.getValue());
                }
            }
        }

        return pageList;
    }
}
