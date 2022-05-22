package me.jaden.titanium.check.impl.book;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import java.util.List;
import me.jaden.titanium.Settings;
import me.jaden.titanium.check.Check;
import me.jaden.titanium.data.PlayerData;

// PaperMC
// net.minecraft.server.network.ServerGamePacketListenerImpl#handleEditBook
public class BookA implements Check {
    private final int maxBookPageSize = Settings.getSettings().getMaxBookPageSize(); // default paper value
    private final double maxBookTotalSizeMultiplier = Settings.getSettings().getMaxBookTotalSizeMultiplier(); // default paper value

    @Override
    public void handle(PacketReceiveEvent event, PlayerData data) {
        if (event.getPacketType() == PacketType.Play.Client.EDIT_BOOK) {
            WrapperPlayClientEditBook wrapper = new WrapperPlayClientEditBook(event);
            List<String> pageList = wrapper.getPages();
            long byteTotal = 0;
            double multiplier = Math.min(1D, this.maxBookTotalSizeMultiplier);
            long byteAllowed = this.maxBookPageSize;
            for (String testString : pageList) {
                int byteLength = testString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
                if (byteLength > 256 * 4) {
                    // page too large
                    System.out.println("kicked");
                    event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
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
                System.out.println("kicked");
                event.getUser().sendPacket(new WrapperPlayServerDisconnect("You are sending too many packets!"));
            }
        }

    }
}
