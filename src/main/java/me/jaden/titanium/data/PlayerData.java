package me.jaden.titanium.data;

import com.github.retrooper.packetevents.protocol.player.User;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.jaden.titanium.settings.TitaniumConfig;

@Setter
@Getter
@RequiredArgsConstructor
public class PlayerData {
    private final User user;

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

    private int openWindowType;
    private int openWindowContainer;

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
        return this.packetCount += 1 * multiplier;
    }

    public double decrementPacketAllowance() {
        return packetAllowance--;
    }
}
