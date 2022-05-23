package me.jaden.titanium.check.impl.invalid;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

//GrimAC
//https://github.com/MWHunter/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/crash/CrashB.java
public class InvalidE implements PacketCheck {

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (!this.getPlayer(event).isPresent()) return;
        Player player = this.getPlayer(event).get();
        //You can't send a creative slot packet without being in creative mode
        //Could false with latency?
        if(event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION && player.getGameMode() != GameMode.CREATIVE){
            flag(event, "gamemode: " + player.getGameMode().name());
        }
    }

}
