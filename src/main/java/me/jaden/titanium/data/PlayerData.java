package me.jaden.titanium.data;

import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.settings.TitaniumConfig;

@Setter
@Getter
public class PlayerData {
    // Spam
    private int lastBookEditTick;
    private int lastDropItemTick;
    private int lastCraftRequestTick;
    private int dropCount;

    private double packetAllowance = TitaniumConfig.getInstance().getMaxPacketsPerSecond();
    private double packetCount;

    private int bytesSent;

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
