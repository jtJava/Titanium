package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import java.util.HashMap;
import java.util.Map;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.check.impl.book.Book;
import me.jaden.titanium.check.impl.book.MassiveBook;
import me.jaden.titanium.check.impl.command.BlockedCommand;
import me.jaden.titanium.check.impl.crasher.BandwidthLimit;
import me.jaden.titanium.check.impl.crasher.Lectern;
import me.jaden.titanium.check.impl.crasher.Log4J;
import me.jaden.titanium.check.impl.crasher.PacketSize;
import me.jaden.titanium.check.impl.creative.CreativeCheck;
import me.jaden.titanium.check.impl.creative.CreativeCheckRunner;
import me.jaden.titanium.check.impl.creative.impl.CreativeAnvil;
import me.jaden.titanium.check.impl.creative.impl.CreativeClientBookCrash;
import me.jaden.titanium.check.impl.creative.impl.CreativeMap;
import me.jaden.titanium.check.impl.creative.impl.CreativeSkull;
import me.jaden.titanium.check.impl.creative.impl.EnchantLimit;
import me.jaden.titanium.check.impl.creative.impl.PotionLimit;
import me.jaden.titanium.check.impl.firework.FireworkSize;
import me.jaden.titanium.check.impl.invalid.ChannelCount;
import me.jaden.titanium.check.impl.invalid.InvalidMove;
import me.jaden.titanium.check.impl.invalid.InvalidPickItem;
import me.jaden.titanium.check.impl.invalid.InvalidSlotChange;
import me.jaden.titanium.check.impl.invalid.InvalidViewDistance;
import me.jaden.titanium.check.impl.sign.SignLength;
import me.jaden.titanium.check.impl.spam.BookSpam;
import me.jaden.titanium.check.impl.spam.CraftSpam;
import me.jaden.titanium.check.impl.spam.DropSpam;
import me.jaden.titanium.check.impl.spam.PacketCount;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class CheckManager {
    private final Map<Class<? extends BaseCheck>, BaseCheck> packetChecks = new HashMap<>();
    private final Map<Class<? extends CreativeCheck>, CreativeCheck> creativeChecks = new HashMap<>();

    public CheckManager() {
        this.initializeListeners();

        ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

        //Add creative checks first, so that the creative check runner can load them
        if (TitaniumConfig.getInstance().getCreativeConfig().isEnabled()) {
            this.addCreativeChecks(
                    new CreativeSkull(),
                    new CreativeMap(),
                    new CreativeClientBookCrash(),
                    new PotionLimit(),
                    new CreativeAnvil()
            );
            if (TitaniumConfig.getInstance().getCreativeConfig().getMaxEnchantmentLevel() != -1) {
                this.addCreativeChecks(new EnchantLimit());
            }
        }

        this.addChecks(
                // Spam (This should always be at the top for performance reasons)
                new BookSpam(),
                new DropSpam(),
                new PacketCount(),
                new CraftSpam(),

                // Invalid
                new InvalidMove(),
                new InvalidViewDistance(),
                new InvalidPickItem(),
                new InvalidSlotChange(),
                new ChannelCount(),

                // Crasher
                new Log4J(),
                new BandwidthLimit(),

                new BlockedCommand(),

                // Firework
                new FireworkSize(),

                // Sign
                new SignLength()
        );

        if (TitaniumConfig.getInstance().isNoBooks()) {
            this.addChecks(new Book());
        } else {
            this.addChecks(new MassiveBook());
        }

        if (TitaniumConfig.getInstance().getMaxBytes() != -1) {
            this.addChecks(new PacketSize());
        }

        if (TitaniumConfig.getInstance().getMaxBytesPerSecond() != -1) {
            this.addChecks(new BandwidthLimit());
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            this.addChecks(new Lectern());
        }

        this.addChecks(new CreativeCheckRunner(creativeChecks.values()));

        this.removeDisabledChecks();
    }

    private void initializeListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new SimplePacketListenerAbstract() {
            //TODO: merge these into one method so there's no duplicate code.
            @Override
            public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
                for (BaseCheck check : packetChecks.values()) {
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
                for (BaseCheck check : packetChecks.values()) {
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
    }

    private void addChecks(BaseCheck... checks) {
        for (BaseCheck check : checks) {
            this.packetChecks.put(check.getClass(), check);

            if (check instanceof CreativeCheck) {
                this.addCreativeChecks((CreativeCheck) check);
            }
        }
    }

    private void addCreativeChecks(CreativeCheck... checks) {
        for (CreativeCheck check : checks) {
            this.creativeChecks.put(check.getClass(), check);
        }
    }

    private void removeDisabledChecks() {
        for (String disabledCheck : TitaniumConfig.getInstance().getDisabledChecks()) {
            this.creativeChecks.keySet().removeIf(clazz -> clazz.getName().contains(disabledCheck));
            this.packetChecks.keySet().removeIf(clazz -> clazz.getName().contains(disabledCheck));
            Titanium.getPlugin().getLogger().info(disabledCheck + " has been disabled if it exists!");
        }
    }
}
