package me.jaden.titanium.data;

import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.Settings;

@Setter
@Getter
public class PlayerData {
    // Spam
    private int lastBookEditTick;
    private int lastDropItemTick;
    private int lastCraftRequestTick;
    private int dropCount;

    private double packetAllowance = Settings.getSettings().getMaxPacketsPerSecond();
    private double packetCount;

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
