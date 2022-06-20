package me.jaden.titanium.data;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.settings.TitaniumConfig;

@Setter
@Getter
public class PlayerData {
    private ClientVersion clientVersion = ClientVersion.V_1_7_10;

    private final Set<String> channels = new HashSet<>();
    private boolean receivingAlerts = false;
    private int lastBookEditTick;
    private int lastDropItemTick;
    private int lastCraftRequestTick;
    private int dropCount;
    private int recursionCount;

    private double packetAllowance = TitaniumConfig.getInstance().getMaxPacketsPerSecond();
    private double packetCount;

    private int bytesSent;

    public int incrementRecursionCount() {
        return recursionCount++;
    }

    public void resetRecursion() {
        recursionCount = 0;
    }

    public int incrementBytesSent(int amount) {
        return bytesSent += amount;
    }

    public int incrementDropCount() {
        return dropCount++;
    }

    public double incrementPacketCount(double multiplier) {
        double packetCount = Math.max(this.packetCount, 1);
        return packetCount * multiplier;
    }

    public double decrementPacketAllowance() {
        return packetAllowance--;
    }
}
