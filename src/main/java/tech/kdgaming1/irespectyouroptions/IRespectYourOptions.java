package tech.kdgaming1.irespectyouroptions;

import tech.kdgaming1.irespectyouroptions.command.IRespectYourOptionsCommand;
import tech.kdgaming1.irespectyouroptions.config.IRespectYourOptionsConfig;
import tech.kdgaming1.irespectyouroptions.optionsapplier.DefaultOptionsApplier;

import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = IRespectYourOptions.MODID, name = IRespectYourOptions.NAME, version = IRespectYourOptions.VERSION)
public class IRespectYourOptions {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static IRespectYourOptions INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static IRespectYourOptionsConfig config;

    private static final Logger LOGGER = LogManager.getLogger(IRespectYourOptions.class);

    public static String runDir = Paths.get("").toAbsolutePath().toString();
    public static String configDir = Paths.get(runDir, "config").toString();
    public static boolean hasCopy;

    public IRespectYourOptions() {
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
                // Using streams to handle resource files
                copyResourceToFile("irespectyouroptions/exampleConfig_openForInstructions.txt", new File(iRespectYourOptionsFolder, "exampleConfig_openForInstructions.txt"));
                copyResourceToFile("irespectyouroptions/exampleConfig2_openForInstructions.txt", new File(configFolder, "exampleConfig2_openForInstructions.txt"));
            } catch (IOException e) {
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

    private void copyResourceToFile(String resourcePath, File targetFile) throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Files.copy(resourceStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }


    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new IRespectYourOptionsConfig();
        CommandManager.INSTANCE.registerCommand(new IRespectYourOptionsCommand());
    }
}
