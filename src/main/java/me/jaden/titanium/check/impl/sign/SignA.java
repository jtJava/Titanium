package me.jaden.titanium.check.impl.sign;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class SignA extends BaseCheck {
    // We add two to account for the " characters at the beginning and end.
    private final int maxCharactersPerLine = TitaniumConfig.getInstance().getMaxSignCharactersPerLine() + 2;

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
            for (String textLine : wrapper.getTextLines()) {
                if (textLine.length() > this.maxCharactersPerLine) {
                    flagPacket(event, "Length: " + textLine.length());
                }
            }
        }
    }
}
