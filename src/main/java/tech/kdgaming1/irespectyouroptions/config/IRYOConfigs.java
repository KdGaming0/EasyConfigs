package tech.kdgaming1.irespectyouroptions.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class IRYOConfigs {

    public static boolean wantToCopy;

    public IRYOConfigs() {
        File configFile = new File(Loader.instance().getConfigDir(), "IRespectYourOptions.cfg");
        Configuration configuration = new Configuration(configFile);

        configuration.load();
        Property hasCopyProperty = configuration.get("copy", "wantToCopy", true, "Set to true to apply default options");
        wantToCopy = hasCopyProperty.getBoolean();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }


}