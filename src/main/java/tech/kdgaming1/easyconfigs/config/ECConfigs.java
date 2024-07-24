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
    public static boolean wantToIncludeMCOptions = true;
    public static boolean configButtonOnPause = true;
    public static boolean configButtonOnOptions = true;
    public static boolean advancedLogging = false;
    public static boolean useBlocklist = true;
    public static boolean showFirstTimeScreen = true;

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
        wantToIncludeMCOptions = configuration.getBoolean("Include MCOptions", Configuration.CATEGORY_GENERAL, true, "Set to true to include MCOptions, false to not include MCOptions. (MCOptions are options that are not in the config folder options.txt, servers.dta, ect.)");
        configButtonOnPause = configuration.getBoolean("Config Button On Pause Menu", Configuration.CATEGORY_GENERAL, true, "Set to true to show the Easy Configs button on the pause menu, false to hide it.");
        configButtonOnOptions = configuration.getBoolean("Config Button On Options Menu", Configuration.CATEGORY_GENERAL, true, "Set to true to show the Easy Configs button on the options menu, false to hide it.");
        advancedLogging = configuration.getBoolean("Advanced Logging", Configuration.CATEGORY_GENERAL, false, "Set to true to enable advanced logging, false to disable it. (This will log more information to the console. Good to haven when you are having issues with EasyConfigs.)");
        useBlocklist = configuration.getBoolean("Use Blocklist", Configuration.CATEGORY_GENERAL, true, "Set to true to use the blocklist file for excluding directories and files, false to disable it.");
        showFirstTimeScreen = configuration.getBoolean("Show First Time Screen", Configuration.CATEGORY_GENERAL, true, "Set to false to not show the first time screen on join worlds.");

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