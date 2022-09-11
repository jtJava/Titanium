package me.jaden.titanium.check.impl.crasher;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import me.jaden.titanium.check.BaseCheck;
import me.jaden.titanium.data.PlayerData;

// https://github.com/PaperMC/Paper/commit/ea2c81e4b9232447f9896af2aac4cd0bf62386fd
// https://wiki.vg/Inventory
// https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/crash/CrashD.java
public class Lectern extends BaseCheck {
    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            WrapperPlayClientClickWindow click = new WrapperPlayClientClickWindow(event);
            int clickType = click.getWindowClickType().ordinal();
            int button = click.getButton();
            int windowId = click.getWindowId();
            if (playerData.getOpenWindowType() == 16 && windowId > 0 && windowId == playerData.getOpenWindowContainer()) {
                flagPacket(event, "Click Type: " + clickType + " Button: " + button);
            }
        }
    }

    @Override
    public void handle(PacketSendEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow window = new WrapperPlayServerOpenWindow(event);
            playerData.setOpenWindowType(window.getType());
            if (playerData.getOpenWindowType() == 16) playerData.setOpenWindowContainer(window.getContainerId());
        }
    }
}
