package me.jaden.titanium.check;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import java.util.HashMap;
import java.util.Map;
import me.jaden.titanium.Titanium;
import me.jaden.titanium.check.impl.book.BookA;
import me.jaden.titanium.check.impl.book.BookB;
import me.jaden.titanium.check.impl.command.CommandA;
import me.jaden.titanium.check.impl.crasher.CrasherA;
import me.jaden.titanium.check.impl.crasher.CrasherC;
import me.jaden.titanium.check.impl.crasher.CrasherD;
import me.jaden.titanium.check.impl.crasher.CrasherE;
import me.jaden.titanium.check.impl.creative.CreativeCheck;
import me.jaden.titanium.check.impl.creative.CreativeCheckRunner;
import me.jaden.titanium.check.impl.creative.impl.CreativeAnvil;
import me.jaden.titanium.check.impl.creative.impl.CreativeClientBookCrash;
import me.jaden.titanium.check.impl.creative.impl.CreativeMap;
import me.jaden.titanium.check.impl.creative.impl.CreativeSkull;
import me.jaden.titanium.check.impl.creative.impl.EnchantLimit;
import me.jaden.titanium.check.impl.creative.impl.PotionLimit;
import me.jaden.titanium.check.impl.firework.FireworkA;
import me.jaden.titanium.check.impl.invalid.InvalidA;
import me.jaden.titanium.check.impl.invalid.InvalidB;
import me.jaden.titanium.check.impl.invalid.InvalidC;
import me.jaden.titanium.check.impl.invalid.InvalidD;
import me.jaden.titanium.check.impl.invalid.InvalidE;
import me.jaden.titanium.check.impl.sign.SignA;
import me.jaden.titanium.check.impl.spam.SpamA;
import me.jaden.titanium.check.impl.spam.SpamB;
import me.jaden.titanium.check.impl.spam.SpamC;
import me.jaden.titanium.check.impl.spam.SpamD;
import me.jaden.titanium.data.DataManager;
import me.jaden.titanium.data.PlayerData;
import me.jaden.titanium.settings.TitaniumConfig;

public class CheckManager {
    private final Map<Class<? extends BaseCheck>, BaseCheck> packetChecks = new HashMap<>();
    private final Map<Class<? extends BukkitCheck>, BukkitCheck> bukkitChecks = new HashMap<>();
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
                new SpamA(),
                new SpamB(),
                new SpamC(),
                new SpamD(),

                // Invalid
                new InvalidA(),
                new InvalidB(),
                new InvalidC(),
                new InvalidD(),
                new InvalidE(),

                // Crasher
                new CrasherC(),
                new CrasherE(),

                new CommandA(),

                // Firework
                new FireworkA(),

                // Sign
                new SignA()
        );

        if (TitaniumConfig.getInstance().isNoBooks()) {
            this.addChecks(new BookB());
        } else {
            this.addChecks(new BookA());
        }

        if (TitaniumConfig.getInstance().getMaxBytes() != -1) {
            this.addChecks(new CrasherD());
        }

        if (TitaniumConfig.getInstance().getMaxBytesPerSecond() != -1) {
            this.addChecks(new CrasherE());
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
            this.addChecks(new CrasherA());
        }

        this.addChecks(new CreativeCheckRunner(creativeChecks.values()));
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

                    if (!check.preconditions(event)) {
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

                    if (!check.preconditions(event)) {
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
        this.bukkitChecks.forEach((loopClass, bukkitCheck) -> plugin.getServer().getPluginManager().registerEvents(bukkitCheck, plugin));
    }

    private void addChecks(BaseCheck... checks) {
        for (BaseCheck check : checks) {
            this.packetChecks.put(check.getClass(), check);

            if (check instanceof CreativeCheck) {
                this.addCreativeChecks((CreativeCheck) check);
            }
        }
    }

    private void addBukkitChecks(BukkitCheck... checks) {
        for (BukkitCheck check : checks) {
            this.bukkitChecks.put(check.getClass(), check);
        }
    }

    private void addCreativeChecks(CreativeCheck... checks) {
        for (CreativeCheck check : checks) {
            this.creativeChecks.put(check.getClass(), check);
        }
    }
}
