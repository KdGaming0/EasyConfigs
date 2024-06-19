package tech.kdgaming1.irespectyouroptions;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.kdgaming1.irespectyouroptions.config.IRYOConfigs;
import tech.kdgaming1.irespectyouroptions.keybinds.IRYOKeyBindings;
import tech.kdgaming1.irespectyouroptions.command.IRYOCommands;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Objects;

@Mod(modid = IRespectYourOptions.MOD_ID, version = IRespectYourOptions.VERSION, guiFactory = "tech.kdgaming1.irespectyouroptions.gui.IRespectYourOptionsGuiFactory")
public class IRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.2.3-1.8.9";

    private static final Logger LOGGER = LogManager.getLogger(IRespectYourOptions.class);

    @Mod.EventHandler
    public void preInit(FMLInitializationEvent event) {
        // Register key bindings
        MinecraftForge.EVENT_BUS.register(new IRYOKeyBindings());

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        IRYOKeyBindings.init();
        ClientCommandHandler.instance.registerCommand(new IRYOCommands());
    }

    public static String runDir = Paths.get("").toAbsolutePath().toString();
    public static String configDir = Paths.get(runDir, "config").toString();
    public static String IRYODir = Paths.get(runDir, "IRespectYourOptions").toString();

    public IRespectYourOptions() {

        new IRYOConfigs();

        LOGGER.info("Applying default options... (IRespectYourOptions)");
        try {
            File IRespectYourOptionsFolder = new File(IRYODir);
            if (!IRespectYourOptionsFolder.exists() && !IRespectYourOptionsFolder.mkdirs()) {
                LOGGER.error("Failed to create directory: " + IRespectYourOptionsFolder.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + IRespectYourOptionsFolder.getAbsolutePath());
            }

            File IRYOSave0 = new File(IRespectYourOptionsFolder, "IRYOSave0");
            if (!IRYOSave0.exists() && !IRYOSave0.mkdirs()) {
                LOGGER.error("Failed to create directory: " + IRYOSave0.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + IRYOSave0.getAbsolutePath());
            }

            File IRYOSave0Config = new File(IRYOSave0, "config");
            if (!IRYOSave0Config.exists() && !IRYOSave0Config.mkdirs()) {
                LOGGER.error("Failed to create directory: " + IRYOSave0Config.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + IRYOSave0Config.getAbsolutePath());
            }

            try {
                // Get the paths of the source files in the resources directory
                Path sourceFile1 = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("irespectyouroptions/exampleConfig_openForInstructions.txt")).toURI());
                Path sourceFile2 = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("irespectyouroptions/exampleConfig2_openForInstructions.txt")).toURI());

                // Get the paths of the target directories
                Path targetFile1 = IRYOSave0.toPath().resolve("exampleConfig_openForInstructions.txt");
                Path targetFile2 = IRYOSave0Config.toPath().resolve("exampleConfig2_openForInstructions.txt");

                // Copy the files
                Files.copy(sourceFile1, targetFile1, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(sourceFile2, targetFile2, StandardCopyOption.REPLACE_EXISTING);
            } catch (URISyntaxException | IOException e) {
                LOGGER.error("Failed to copy files.", e);
            }

            if (!IRYOConfigs.wantToCopy) {
                LOGGER.info("Configs is set to not apply, if you want to apply the default options again do /IRYO loadConfigs 0 or [1-9] (1-9 is your own saves or if the mod pack developer have multiple different saves) and restart the game.\");  .");
            } else {
                IRYODefaultOptionsApplier.apply();
                LOGGER.info("Copying of config files have been set to true. IRYO is now copying the config files from your chosen IRYOConfig slot or the default config slot to the config folder.");
                IRYOConfigs.wantToCopy = false;
                LOGGER.info("The copy config value have been set to false to prevent the default options from being applied again. If you want to apply the default options again do /IRYO loadConfigs 0 or [1-9] (1-9 is your own saves or if the mod pack developer have multiple different saves) and restart the game.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }
}
