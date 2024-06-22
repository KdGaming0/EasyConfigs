package tech.kdgaming1.easyconfigs.config;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.kdgaming1.easyconfigs.EasyConfigs;

import java.io.File;

public class ECConfigs {

    public static Configuration configuration;

    public static boolean wantToCopy = true;
    public static int copySlot = 0;
    public static boolean newUser = true;

    public static void init(String configDir) {

        if (configuration == null) {
            File path = new File(configDir + "/" + EasyConfigs.MOD_ID + ".cfg");

            configuration = new Configuration(path);
            loadConfigurations();
        }
    }

    private static void loadConfigurations() {

        wantToCopy = configuration.getBoolean("Want To Copy", Configuration.CATEGORY_GENERAL, true, "Set to true to copy options from the config slot you want, false to not copy (After the game have started and the copy was successful it will be set to false.)");
        copySlot = configuration.getInt("Config Slot", Configuration.CATEGORY_GENERAL, 0, 0, 9, "Choose a config slot to copy from (0-9)");
        newUser = configuration.getBoolean("New User", Configuration.CATEGORY_GENERAL, true, "Set to true if you are a new user, false if you are not a new user or false if you ship the mod in a mod pack.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(EasyConfigs.MOD_ID)) {
            loadConfigurations();
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

}
