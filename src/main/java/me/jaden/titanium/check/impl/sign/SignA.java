package me.jaden.titanium.check.impl.sign;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class SignA implements PacketCheck {
    private final int maxCharactersPerLine = TitaniumConfig.getInstance().getMaxSignCharactersPerLine();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.UPDATE_SIGN) {
            WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
            for (String textLine : wrapper.getTextLines()) {
                if (textLine.length() > this.maxCharactersPerLine) { // Magic number, you can only put 15 letters on a sign though.
                    flag(event, "length: " + textLine.length());
                }
            }
        }
    }
}
