package me.jaden.titanium.check.impl.command;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.MessagesConfig;
import me.jaden.titanium.settings.TitaniumConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class BlockedCommand extends BaseCheck {
    private static final Pattern PLUGIN_EXCLUSION = Pattern.compile("/(\\S+:)");
    private final List<String> disallowedCommands = TitaniumConfig.getInstance().getDisallowedCommands();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
            final String message = wrapper.getText().toLowerCase().replaceAll("\\s+", " ");
            for (String disallowedCommand : disallowedCommands) {
                if (message.contains(disallowedCommand)) {
                    if (!checkPermissions(event)) {
                        flagPacket(event, "Disallowed tab complete: " + message, false);
                    }
                    break;
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
            final String message = wrapper.getMessage().toLowerCase().replaceAll("\\s+", " ");
            for (String disallowedCommand : disallowedCommands) {
                if (message.contains(disallowedCommand)) {
                    if (!checkPermissions(event)) {
                        flagPacket(event, "Disallowed command: " + message, false);
                        final Component blockedCommandMessage = TitaniumConfig.getInstance().getMessagesConfig().getBlockedCommandMessage();
                        if(!TitaniumConfig.getInstance().getMessagesConfig().getBlockedCommandMessage().toString().equals("")) {
                            event.getUser().sendMessage(blockedCommandMessage);
                        }
                    }
                    break;
                }
                String pluginCommand = replaceGroup(PLUGIN_EXCLUSION.pattern(), message, 1, 1, "");
                if (pluginCommand.contains(disallowedCommand)) {
                    if (!checkPermissions(event)) {
                        flagPacket(event, "Disallowed command: " + pluginCommand, false);
                        final Component blockedCommandMessage = TitaniumConfig.getInstance().getMessagesConfig().getBlockedCommandMessage();
                        if(!TitaniumConfig.getInstance().getMessagesConfig().getBlockedCommandMessage().toString().equals("")) {
                            event.getUser().sendMessage(blockedCommandMessage);
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean checkPermissions(PacketReceiveEvent event) {
        Player player = this.getPlayer(event);
        if (player == null) {
            return false;
        }

        return player.hasPermission(TitaniumConfig.getInstance().getPermissionsConfig().getCommandBypassPermission()) || player.isOp();
    }

    private String replaceGroup(String regex, String source, int groupToReplace, int groupOccurrence, String replacement) {
        Matcher m = Pattern.compile(regex).matcher(source);
        for (int i = 0; i < groupOccurrence; i++)
            if (!m.find()) return source; // pattern not met, may also throw an exception here
        return new StringBuilder(source).replace(m.start(groupToReplace), m.end(groupToReplace), replacement).toString();
    }
}


