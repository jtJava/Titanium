package me.jaden.titanium.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import java.util.ArrayList;
import java.util.List;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleEditBook
public class BookA implements PacketCheck {
    private final int maxBookPageSize = TitaniumConfig.getInstance().getMaxBookPageSize(); // default paper value
    private final double maxBookTotalSizeMultiplier = TitaniumConfig.getInstance().getMaxBookTotalSizeMultiplier(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        List<String> pageList = new ArrayList<>();

        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook wrapper = new WrapperPlayClientEditBook(event);
            pageList.addAll(wrapper.getPages());
        } else if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

            // Make sure it's a book payload
            if (wrapper.getChannelName().contains("MC|BEdit") || wrapper.getChannelName().contains("MC|BSign")) {
                Object buffer = null;
                try {
                    buffer = UnpooledByteBufAllocationHelper.buffer();
                    ByteBufHelper.writeBytes(buffer, wrapper.getData());
                    PacketWrapper<?> universalWrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
                    com.github.retrooper.packetevents.protocol.item.ItemStack wrappedItemStack = universalWrapper.readItemStack();

                    pageList.addAll(this.getPages(wrappedItemStack));
                    if (invalidTitleOrAuthor(wrappedItemStack)) flag(event);
                } finally {
                    ByteBufHelper.release(buffer);
                }
            }

        } else if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                pageList.addAll(this.getPages(wrapper.getItemStack().get()));
                if (invalidTitleOrAuthor(wrapper.getItemStack().get())) flag(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);

            if (wrapper.getItemStack() != null) {
                pageList.addAll(this.getPages(wrapper.getItemStack()));
                if (invalidTitleOrAuthor(wrapper.getItemStack())) flag(event);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
            if (wrapper.getCarriedItemStack() != null) {
                pageList.addAll(this.getPages(wrapper.getCarriedItemStack()));
                if (invalidTitleOrAuthor(wrapper.getCarriedItemStack())) flag(event);
            }
        } else {
            return;
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

    private boolean invalidTitleOrAuthor(ItemStack itemStack) {
        if (itemStack.getNBT() != null) {
            String title = itemStack.getNBT().getStringTagValueOrNull("title");
            if (title != null && title.length() > 100) {
                return true;
            }

            String author = itemStack.getNBT().getStringTagValueOrNull("author");
            return author != null && author.length() > 16;
        }
        return false;
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
