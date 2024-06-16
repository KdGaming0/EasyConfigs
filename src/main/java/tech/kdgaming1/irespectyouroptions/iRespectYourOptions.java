package tech.kdgaming1.irespectyouroptions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Objects;

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

            File configFolder = new File(iRespectYourOptionsFolder, "config");
            if (!configFolder.exists() && !configFolder.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + configFolder.getAbsolutePath());
            }

            try {
                // Get the paths of the source files in the resources directory
                Path sourceFile1 = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("irespectyouroptions/exampleConfig_openForInstructions.txt")).toURI());
                Path sourceFile2 = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("irespectyouroptions/exampleConfig2_openForInstructions.txt")).toURI());

                // Get the paths of the target directories
                Path targetFile1 = iRespectYourOptionsFolder.toPath().resolve("exampleConfig_openForInstructions.txt");
                Path targetFile2 = configFolder.toPath().resolve("exampleConfig2_openForInstructions.txt");

                // Copy the files
                Files.copy(sourceFile1, targetFile1, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(sourceFile2, targetFile2, StandardCopyOption.REPLACE_EXISTING);
            } catch (URISyntaxException | IOException e) {
                LOGGER.error("Failed to copy files.", e);
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
}
