package tech.kdgaming1.irespectyouroptions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Mod(modid = iRespectYourOptions.MOD_ID, version = iRespectYourOptions.VERSION, dependencies = "before:*")
public class iRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.2.3-1.8.9";
    private static final Logger LOGGER = LogManager.getLogger(iRespectYourOptions.class);

    public static String runDir = Paths.get("").toAbsolutePath().toString();
    public static String configDir = Paths.get(runDir, "config").toString();
    public static boolean hasCopy;

    public iRespectYourOptions() {
        LOGGER.info("Applying default options... (iRespectYourOptions)");
        try {
            File iRespectYourOptionsFolder = new File(configDir, "iRespectYourOptions");
            if (!iRespectYourOptionsFolder.exists() && !iRespectYourOptionsFolder.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + iRespectYourOptionsFolder.getAbsolutePath());
            }

            createExampleFile(iRespectYourOptionsFolder, "exampleConfig_openForInstructions.txt");
            createExampleFile(iRespectYourOptionsFolder, "exampleConfig2_openForInstructions.txt");

            File config = new File(iRespectYourOptionsFolder, "config");
            if (!config.exists() && !config.mkdirs()) {
                LOGGER.info("Remember to put content in to the options.txt and optionsof.txt files in the iRespectYourOptions folder in the config folder.");
                throw new IllegalStateException("Could not create directory: " + config.getAbsolutePath());
            }

            File configFile = new File(Loader.instance().getConfigDir(), "iRespectYourOptions.cfg");
            Configuration configuration = new Configuration(configFile);
            configuration.load();
            Property hasCopyProperty = configuration.get("copy", "hasCopy", false, "Set to true to apply default options");
            hasCopy = hasCopyProperty.getBoolean();

            if (hasCopy) {
                LOGGER.info("Default options have already been applied. If you want to override your options back to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
            } else {
                DefaultOptionsApplier.apply();
                hasCopyProperty.set(true);
                configuration.save();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }

    private void createExampleFile(File folder, String fileName) throws IOException {
        File file = new File(folder, fileName);
        if (file.createNewFile()) {
            String content = "This is the text content to write into file";
            Files.write(Paths.get(file.getPath()), content.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
