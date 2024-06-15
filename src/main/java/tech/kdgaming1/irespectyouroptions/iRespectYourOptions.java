package tech.kdgaming1.irespectyouroptions;

import tech.kdgaming1.irespectyouroptions.applyDefaultOptions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@Mod(modid = iRespectYourOptions.MOD_ID, version = iRespectYourOptions.VERSION, dependencies = "before:*")
public class iRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.2.3-1.8.9";
    private static final Logger LOGGER = LogManager.getLogger(iRespectYourOptions.class);

    public iRespectYourOptions() {

        String runDir = Paths.get("").toAbsolutePath().toString();
        String configDir = Paths.get(runDir, "config").toString();

        LOGGER.info("Applying default options... (iRespectYourOptions)");
        try {
            File iRespectYourOptions = new File(configDir, "iRespectYourOptions");
            if (!iRespectYourOptions.exists() && !iRespectYourOptions.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + iRespectYourOptions.getAbsolutePath());
            }
            new File(iRespectYourOptions, "options.txt").createNewFile();
            new File(iRespectYourOptions, "optionsof.txt").createNewFile();
            File config = new File(iRespectYourOptions, "config");
            if (!config.exists() && !config.mkdirs()) {
                LOGGER.info("Remember to put content in to the options.txt and optionsof.txt files in the iRespectYourOptions folder in the config folder.");
                throw new IllegalStateException("Could not create directory: " + config.getAbsolutePath());
            }
            
                // This makes the config file with the info that says if the mod has run before
            File configFile = new File(Loader.instance().getConfigDir(), "iRespectYourOptions.cfg");
            Configuration configuration = new Configuration(configFile);
            configuration.load();
            Property hasCopyProperty = configuration.get("copy", "hasCopy", false, "Set to true to apply default options");
            boolean hasCopy = hasCopyProperty.getBoolean();

                // To write a massage to the console to say that the default options have already been applied and how you can override them if you want
            if (hasCopy) {
                LOGGER.info("Default options have already been applied. If you want to override your options bake to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
            }

                // This is a check to see if the default options have already been applied
            if (!hasCopy) {
                // Runs through all the files in the iRespectYourOptions directory and applies the default options
                Files.walk(iRespectYourOptions.toPath()).forEach(path -> {
                    File file = path.normalize().toAbsolutePath().normalize().toFile();
                    if (!file.isFile()) return;
                    try {
                        try {
                            Path configRelative = config.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize());
                            if (configRelative.startsWith("iRespectYourOptions"))
                                throw new IllegalStateException("Illegal default config file: " + file);
                            applyDefaultOptions.applyDefaultOptions(new File(configDir, configRelative.normalize().toString()), file);
                        } catch (IllegalArgumentException e) {
                            System.out.println(iRespectYourOptions.toPath().toAbsolutePath().normalize());
                            System.out.println(file.toPath().toAbsolutePath().normalize());
                            System.out.println(iRespectYourOptions.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize()));
                            applyDefaultOptions.applyDefaultOptions(new File(runDir, iRespectYourOptions.toPath().toAbsolutePath().normalize().relativize(file.toPath().toAbsolutePath().normalize()).normalize().toString()), file);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                // Sets the hasCopy property to true and saves the config file
            hasCopyProperty.set(true);
            LOGGER.info("Default options have been successful applied. If you want to override your options bake to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
            configuration.save();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }
}