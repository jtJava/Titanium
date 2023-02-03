package me.jaden.titanium.settings;

import com.google.common.collect.ImmutableMap;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import me.jaden.titanium.Titanium;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesConfig {
    private final String staffNotification;
    private final String staffNotificationInfo;
    private final String disconnectMessage;
    private final String blockedCommandMessage;

    @Getter
    private final LegacyComponentSerializer componentSerializer;

    public MessagesConfig(FileConfiguration configuration) {
        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("messages.staff-notification", "&7(&eTitanium&7) &8» &f%player% &7disconnected for flagging &f%checkname%")
                .put("messages.staff-notification-info", "&7(&e%info%&7)")
                .put("messages.disconnect-message", "&7(&eTitanium&7) &8» &fYou have been disconnected due \n&fto sending harmful packets.")
                .put("messages.blocked-command-message", "Unknown command. Type \"/help\" for help.")
                .build());

        this.staffNotification = configuration.getString("messages.staff-notification", "&7(&eTitanium&7) &8» &f%player% &7disconnected for flagging &f%checkname%");
        this.staffNotificationInfo = configuration.getString("messages.staff-notication-info", "&7(&e%info%&7)");
        this.disconnectMessage = configuration.getString("messages.disconnect-message", "&7(&eTitanium&7) &8» &fYou have been disconnected due \n&fto sending harmful packets.");
        this.blockedCommandMessage = configuration.getString("messages.blocked-command-message", "Unknown command. Type \"/help\" for help.");

        this.componentSerializer = Titanium.getPlugin().getComponentSerializer();
    }

    public Component getNotification(String playerName, String checkName, String info) {
        Component notification = componentSerializer.deserialize(staffNotification.replaceAll("%player%", playerName).replaceAll("%checkname%", checkName));

        if (!info.equals("")) {
            return notification.append(this.getInfo(info));
        } else {
            return notification;
        }
    }

    private Component getInfo(String info) {
        return componentSerializer.deserialize(staffNotificationInfo.replaceAll("%info%", info));
    }

    public Component getKickMessage(String checkName) {
        return componentSerializer.deserialize(
                disconnectMessage.replaceAll("%checkname%", checkName)
        );
    }

    public Component getBlockedCommandMessage() {
        return componentSerializer.deserialize(blockedCommandMessage);
    }
}
