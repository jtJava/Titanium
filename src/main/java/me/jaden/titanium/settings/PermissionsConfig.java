package me.jaden.titanium.settings;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class PermissionsConfig {
    @Getter
    private final String notificationPermission;

    public PermissionsConfig(FileConfiguration configuration) {
        configuration.addDefaults(ImmutableMap.<String, Object>builder()
                .put("permissions.notification", "titanium.notification")
                .build());

        this.notificationPermission = configuration.getString("permissions.notification");
    }
}
