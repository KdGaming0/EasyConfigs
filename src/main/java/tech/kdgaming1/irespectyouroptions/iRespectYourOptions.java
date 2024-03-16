package tech.kdgaming1.irespectyouroptions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;



@Mod(modid = iRespectYourOptions.MOD_ID, version = iRespectYourOptions.VERSION)
public class iRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.2.0-1.8.9";
    private static final Logger LOGGER = LogManager.getLogger(iRespectYourOptions.class);

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        File runDir = new File(event.getModConfigurationDirectory().getParent());
        File configDir = new File(event.getModConfigurationDirectory().getParent(), "config");

        LOGGER.info("Applying default options... (iRespectYourOptions)");
        try {
            File iRespectYourOptions = new File(configDir, "iRespectYourOptions");
            if (!iRespectYourOptions.exists() && !iRespectYourOptions.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + iRespectYourOptions.getAbsolutePath());
            }
            new File(iRespectYourOptions, "options.txt").createNewFile();
            File config = new File(iRespectYourOptions, "config");
            if (!config.exists() && !config.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + config.getAbsolutePath());
            }
            new File(iRespectYourOptions, "optionsof.txt").createNewFile();
            File configof = new File(iRespectYourOptions, "config");
            if (!configof.exists() && !configof.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + config.getAbsolutePath());
            }
            
                // This makes the config file with the info that says if the mod has run before
            File configFile = new File(Loader.instance().getConfigDir(), "iRespectYourOptions.cfg");
            Configuration configuration = new Configuration(configFile);
            configuration.load();
            Property shouldCopyProperty = configuration.get("copy", "shouldCopy", false);
            boolean shouldCopy = shouldCopyProperty.getBoolean();

                // To write a massage to the console to say that the default options have already been applied and how you can override them if you want
            if (shouldCopy) {
                LOGGER.info("Default options have already been applied. If you want to override your options bake to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
            }

                // This is a check to see if the default options have already been applied
            if (!shouldCopy) {
                // Runs through all the files in the iRespectYourOptions directory and applies the default options
            Files.walk(iRespectYourOptions.toPath()).forEach(path -> {
                File file = path.normalize().toAbsolutePath().normalize().toFile();
                if (!file.isFile()) return;
                try {
                    try {
                        Path configRelative = config.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize());
                        if (configRelative.startsWith("iRespectYourOptions"))
                            throw new IllegalStateException("Illegal default config file: " + file);
                        applyDefaultOptions(new File(configDir, configRelative.normalize().toString()), file);
                    } catch (IllegalArgumentException e) {
                        System.out.println(iRespectYourOptions.toPath().toAbsolutePath().normalize());
                        System.out.println(file.toPath().toAbsolutePath().normalize());
                        System.out.println(iRespectYourOptions.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize()));
                        applyDefaultOptions(new File(runDir, iRespectYourOptions.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize()).normalize().toString()), file);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
                // Sets the shouldCopy property to true and saves the config file
            shouldCopyProperty.set(true);
            configuration.save();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }
    
    // This method is called when the mod is loaded and is used to apply the default options
    private void applyDefaultOptions(File file, File defaultFile) throws IOException {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IllegalStateException("Could not create directory: " + file.getParentFile().getAbsolutePath());
                }
                if (!defaultFile.getParentFile().exists() && !defaultFile.getParentFile().mkdirs()) {
                    throw new IllegalStateException("Could not create directory: " + defaultFile.getParentFile().getAbsolutePath());
                }
                if (!defaultFile.exists()) {
                    defaultFile.createNewFile();
                    return;
                }
                if (file.exists()) return;
                LOGGER.info("Applying default options for " + File.separator + file.toPath().toAbsolutePath().normalize().toString() + " from " + File.separator +
                            defaultFile.toPath().toAbsolutePath().normalize().toString());
                Files.copy(defaultFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }
}