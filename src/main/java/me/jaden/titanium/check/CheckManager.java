package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import java.util.HashMap;
import java.util.Map;
import me.jaden.titanium.Settings;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.check.impl.book.BookA;
import me.jaden.titanium.check.impl.book.BookB;
import me.jaden.titanium.check.impl.book.BookC;
import me.jaden.titanium.check.impl.book.BookD;
import me.jaden.titanium.check.impl.crasher.CrasherA;
import me.jaden.titanium.check.impl.crasher.CrasherC;
import me.jaden.titanium.check.impl.invalid.InvalidA;
import me.jaden.titanium.check.impl.invalid.InvalidB;
import me.jaden.titanium.check.impl.invalid.InvalidC;
import me.jaden.titanium.check.impl.invalid.InvalidD;
import me.jaden.titanium.check.impl.spam.SpamA;
import me.jaden.titanium.check.impl.spam.SpamB;
import me.jaden.titanium.check.impl.spam.SpamC;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CheckManager {
    private final Map<Class<? extends PacketCheck>, PacketCheck> packetChecks = new HashMap<>();
    private final Map<Class<? extends BukkitCheck>, BukkitCheck> bukkitChecks = new HashMap<>();

    public CheckManager() {
        this.initializeListeners();

        ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

        this.addPacketChecks(
                // Spam (This should always be at the top for performance reasons)
                new SpamA(),
                new SpamB(),
                new SpamC(),

                // Invalid
                new InvalidA(),
                new InvalidB(),
                new InvalidC(),
                new InvalidD(),

                // Crasher
                new CrasherC()
        );

        if (Settings.getSettings().isNoBooks()) {
            this.addPacketChecks(new BookD());
        } else {
            if (serverVersion.isNewerThan(ServerVersion.V_1_13)) {
                this.addPacketChecks(new BookA());
            } else {
                this.addPacketChecks(new BookB(), new BookC());
            }
        }

        if (serverVersion.isNewerThan(ServerVersion.V_1_10)) {
            this.addBukkitChecks(new CrasherA());
        }
    }

    private void initializeListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new SimplePacketListenerAbstract() {
            @Override
            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
                for (PacketCheck check : packetChecks.values()) {
                    if (event.isCancelled()) {
                        return;
                    }

                    PlayerData data = DataManager.getInstance().getPlayerData(event.getUser());

                    if (data != null) {
                        check.handle(event, data);
                    }
                }
            }

            @Override
            public void onPacketPlaySend(PacketPlaySendEvent event) {
                for (PacketCheck check : packetChecks.values()) {
                    if (event.isCancelled()) {
                        return;
                    }

                    PlayerData data = DataManager.getInstance().getPlayerData(event.getUser());

                    if (data != null) {
                        check.handle(event, data);
                    }
                }
            }
        });
        Titanium plugin = Titanium.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onEvent(InventoryClickEvent event) {
                for (BukkitCheck check : bukkitChecks.values()) {
                    check.onInventoryClick(event);
                }
            }
        }, plugin);
    }

    private void addPacketChecks(PacketCheck... checks) {
        for (PacketCheck check : checks) {
            this.packetChecks.put(check.getClass(), check);
        }
    }

    private void addBukkitChecks(BukkitCheck... checks) {
        for (BukkitCheck check : checks) {
            this.bukkitChecks.put(check.getClass(), check);
        }
    }
}
