package me.jaden.titanium.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import java.util.concurrent.TimeUnit;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@CommandAlias("titanium")
public class TitaniumCommand extends BaseCommand {
    @Subcommand("info")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @CommandPermission("titanium.notification")
    public void info(Player executor, OnlinePlayer onlinePlayer) {
        Titanium plugin = Titanium.getPlugin();
        PlayerData playerData = DataManager.getInstance().getPlayerData(onlinePlayer.getPlayer().getUniqueId());

        Component message = plugin.getComponentSerializer().deserialize(
                "&7&m--------&r&7 (&eTitanium&7) &fInformation &7&m--------\n"
                        + "&eClient Version: &f" + playerData.getUser().getClientVersion().getReleaseName() + "\n"
                        + "&ePacket Count: &f" + playerData.getPacketCount() + "\n"
                        + "&ePacket Allowance: &f" + playerData.getPacketAllowance() + "\n"
                        + "&eBytes Sent: &f" + playerData.getBytesSent() + " of " + plugin.getTitaniumConfig().getMaxBytesPerSecond() + "\n"
        );

        DataManager.getInstance().getPlayerData(executor.getUniqueId()).getUser().sendMessage(message);
    }

    @Subcommand("debug")
    @CommandPermission("titanium.notification")
    public void debug(Player executor) {
        Titanium plugin = Titanium.getPlugin();

        long delta = System.currentTimeMillis() - plugin.getTicker().getLastReset();
        Component message = plugin.getComponentSerializer().deserialize(
                "&7&m--------&r&7 (&eTitanium&7) &fDebug &7&m--------\n"
                        + "&eTime Since Playerdata Reset: &f" + TimeUnit.MILLISECONDS.toSeconds(delta) + "\n"
        );

        DataManager.getInstance().getPlayerData(executor.getUniqueId()).getUser().sendMessage(message);
    }

    @Subcommand("information")
    @Syntax("[player]")
    @CommandCompletion("@players")
    @CommandPermission("titanium.notification")
    public void informationAlias(Player executor, OnlinePlayer onlinePlayer) {
        this.info(executor, onlinePlayer);
    }
}
