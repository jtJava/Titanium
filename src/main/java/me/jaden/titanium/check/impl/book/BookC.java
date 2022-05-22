package me.jaden.titanium.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import java.util.List;
import me.jaden.titanium.Settings;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

// This is BookA for legacy servers which don't have the edit book packet
public class BookC implements PacketCheck {
    private final int maxBookPageSize = Settings.getSettings().getMaxBookPageSize(); // default paper value
    private final double maxBookTotalSizeMultiplier = Settings.getSettings().getMaxBookTotalSizeMultiplier(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

            if (wrapper.getItemStack().isPresent()) {
                com.github.retrooper.packetevents.protocol.item.ItemStack wrappedItemStack = wrapper.getItemStack().get();
                ItemStack bukkitItemStack = SpigotReflectionUtil.encodeBukkitItemStack(wrappedItemStack);

                if (wrappedItemStack.getType() == ItemTypes.BOOK || wrappedItemStack.getType() == ItemTypes.WRITTEN_BOOK || wrappedItemStack.getType() == ItemTypes.WRITABLE_BOOK) {
                    BookMeta bookMeta = (BookMeta) bukkitItemStack.getItemMeta();

                    if (bookMeta == null) {
                        return;
                    }

                    List<String> pageList = bookMeta.getPages();
                    long byteTotal = 0;
                    double multiplier = Math.min(1D, this.maxBookTotalSizeMultiplier);
                    long byteAllowed = this.maxBookPageSize;

                    for (String testString : pageList) {
                        int byteLength = testString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
                        if (byteLength > 256 * 4) {
                            // page too large
                            flag(event);                            return;
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
                        flag(event);                    }
                }
            }
        }
    }
}
