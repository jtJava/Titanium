package me.jaden.titanium.settings;

import com.github.retrooper.packetevents.util.AdventureSerializer;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesConfig {
    private static final LegacyComponentSerializer componentSerializer = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.AMPERSAND_CHAR)
            .hexCharacter(LegacyComponentSerializer.HEX_CHAR).build();

    private final String kickNotification;
    private final String disconnectMessage;

    public MessagesConfig(FileConfiguration configuration) {
        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("messages.kick-notification", "[Titanium] Disconnected %player% for flagging %checkname% (%info%)")
                .put("messages.disconnect-message", "Timed out")
                .build());

        this.kickNotification = configuration.getString("messages.kick-notification", "[Titanium] Disconnected %player% for flagging %checkname% (%info%)");
        this.disconnectMessage = configuration.getString("messages.disconnect-message", "Timed out");
    }

    public Component getNotification(String playerName, String checkname, String info) {
        return AdventureSerializer.getGsonSerializer().deserialize(
                kickNotification.replaceAll("%player%", playerName)
                        .replaceAll("%checkname%", checkname)
                        .replaceAll("%info%", info)
        );
    }

    public Component getKickMessage(String checkName) {
        return AdventureSerializer.getGsonSerializer().deserialize(
                disconnectMessage.replaceAll("%checkname%", checkName)
        );
    }
}
