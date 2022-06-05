package me.jaden.titanium.check.impl.command;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import java.util.List;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;
import org.bukkit.entity.Player;

public class CommandA implements PacketCheck {
    private final List<String> disallowedCommands = TitaniumConfig.getInstance().getDisallowedCommands();

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (this.getPlayer(event).isPresent()) {
            Player player = this.getPlayer(event).get();
            if (player.hasPermission(TitaniumConfig.getInstance().getPermissionsConfig().getCommandBypassPermission()) || player.isOp()) {
                return;
            }
        }

        if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
            WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
            for (String disallowedCommand : disallowedCommands) {
                if (wrapper.getText().toLowerCase().startsWith(disallowedCommand)) {
                    event.setCancelled(true);
                    break;
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
            for (String disallowedCommand : disallowedCommands) {
                if (wrapper.getMessage().toLowerCase().startsWith(disallowedCommand)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
