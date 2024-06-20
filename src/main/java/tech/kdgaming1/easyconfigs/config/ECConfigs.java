package tech.kdgaming1.easyconfigs.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ECConfigs {

    public static boolean wantToCopy;
    private static Configuration configuration;

    public static void init(File configFile) {
        // Initialize the configuration object
        configuration = new Configuration(configFile);
        loadConfig();
    }

    private static void loadConfig() {
        try {
            configuration.load();
            Property hasCopyProperty = configuration.get("copy", "wantToCopy", true, "Set to true to apply default options");
            wantToCopy = hasCopyProperty.getBoolean();
        } catch (Exception e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }

    public static void syncConfig() {
        loadConfig();
    }

    // Method to update the wantToCopy value and save the configuration
    public static void setWantToCopy(boolean value) {
        wantToCopy = value;
        if (configuration != null) {
            Property hasCopyProperty = configuration.get("copy", "wantToCopy", true, "Set to true to apply default options");
            hasCopyProperty.set(value);
            configuration.save();
        }
    }
}
