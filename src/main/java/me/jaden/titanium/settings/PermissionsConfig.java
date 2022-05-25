package me.jaden.titanium.settings;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class PermissionsConfig {
    private final String commandBypassPermission;
    private final String notificationPermission;

    public PermissionsConfig(FileConfiguration configuration) {
        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("permissions.commandbypass", "titanium.commandbypass")
                .put("permissions.notification", "titanium.notification")
                .build());

        this.commandBypassPermission = configuration.getString("permissions.commandbypass");
        this.notificationPermission = configuration.getString("permissions.notification");
    }
}
