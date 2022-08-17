package me.jaden.titanium.data;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.UserConnectEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

public class DataManager {
    @Getter
    private static DataManager instance;
    @Getter
    private final Map<User, PlayerData> playerData = new HashMap<>();

    public DataManager() {
        instance = this;

        this.initializePacketListeners();
    }

    private void initializePacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerCommon() {
            @Override
            public void onUserConnect(UserConnectEvent event) {
                addPlayerData(event.getUser());
            }

            @Override
            public void onUserDisconnect(UserDisconnectEvent event) {
                removePlayerData(event.getUser());
            }
        });
    }

    public PlayerData getPlayerData(User user) {
        return this.playerData.get(user);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.keySet().stream().filter(user -> user.getUUID() == uuid).findFirst().map(this.playerData::get).orElse(null);
    }

    public void addPlayerData(User user) {
        this.playerData.put(user, new PlayerData(user));
    }

    public void removePlayerData(User user) {
        this.playerData.remove(user);
    }
}
