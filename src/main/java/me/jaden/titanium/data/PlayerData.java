package me.jaden.titanium.data;

import lombok.Getter;
import lombok.Setter;
import me.jaden.titanium.Settings;

@Setter
@Getter
public class PlayerData {
    // Crasher
    private boolean possiblyViewingLectern;

    // Spam
    private int lastBookEditTick;
    private int lastDropItemTick;
    private int dropCount;
    private int packetAllowance = Settings.getSettings().getMaxPacketsPerSecond();
    private int packetCount;

    public int incrementDropCount() {
        return dropCount++;
    }

    public int incrementPacketCount() {
        return packetCount++;
    }

    public int decrementPacketAllowance() {
        return packetAllowance--;
    }
}
