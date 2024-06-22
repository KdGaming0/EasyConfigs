package tech.kdgaming1.easyconfigs.easyconfighandler;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.kdgaming1.easyconfigs.config.ECConfigs;
import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ECSetup {

    public static String runDir = Paths.get("").toAbsolutePath().toString();
    public static String configDir = Paths.get(runDir, "config").toString();
    public static String ECDir = Paths.get(runDir, "EasyConfigs").toString();
    public static String ECExport = Paths.get(ECDir, "EasyConfigExport").toString();
    public static String ECImport = Paths.get(ECDir, "EasyConfigImport").toString();

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

public static void setup() {
        try {
            File EasyConfigsFolder = new File(ECDir);
            if (!EasyConfigsFolder.exists() && !EasyConfigsFolder.mkdirs()) {
                LOGGER.error("Failed to create directory: " + EasyConfigsFolder.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + EasyConfigsFolder.getAbsolutePath());
            }

            File ECExport = new File(EasyConfigsFolder, "EasyConfigExport");
            if (!ECExport.exists() && !ECExport.mkdirs()) {
                LOGGER.error("Failed to create directory: " + ECExport.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + ECExport.getAbsolutePath());
            }

            File ECImport = new File(EasyConfigsFolder, "EasyConfigImport");
            if (!ECImport.exists() && !ECImport.mkdirs()) {
                LOGGER.error("Failed to create directory: " + ECImport.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + ECImport.getAbsolutePath());
            }

            File ECSave0 = new File(EasyConfigsFolder, "EasyConfigSave0");
            if (!ECSave0.exists() && !ECSave0.mkdirs()) {
                LOGGER.error("Failed to create directory: " + ECSave0.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + ECSave0.getAbsolutePath());
            }

            File ECSave0Config = new File(ECSave0, "config");
            if (!ECSave0Config.exists() && !ECSave0Config.mkdirs()) {
                LOGGER.error("Failed to create directory: " + ECSave0Config.getAbsolutePath());
                throw new IllegalStateException("Could not create directory: " + ECSave0Config.getAbsolutePath());
            }

            if (ECConfigs.newUser) {
                try {
                    // Get the InputStreams of the source files in the resources directory
                    InputStream sourceStream1 = Objects.requireNonNull(ECSetup.class.getClassLoader().getResourceAsStream("easyconfigs/exampleConfig_openForInstructions.txt"));
                    InputStream sourceStream2 = Objects.requireNonNull(ECSetup.class.getClassLoader().getResourceAsStream("easyconfigs/exampleConfig2_openForInstructions.txt"));


                    // Get the paths of the target directories
                    Path targetFile1 = ECSave0.toPath().resolve("exampleConfig_openForInstructions.txt");
                    Path targetFile2 = ECSave0Config.toPath().resolve("exampleConfig2_openForInstructions.txt");

                    // Copy the files
                    Files.copy(sourceStream1, targetFile1, StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(sourceStream2, targetFile2, StandardCopyOption.REPLACE_EXISTING);

                    ECConfigs.newUser = false;
                    ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "New User", true).set(ECConfigs.newUser);
                    ECConfigs.getConfiguration().save();
                } catch (IOException e) {
                    LOGGER.error("Failed to copy example files.", e);
                } catch (NullPointerException e) {
                    LOGGER.error("Failed to get the InputStreams of the source files.", e);
                }
            }
        } catch(Exception e){
            LOGGER.error("Failed to create directories.", e);
        }
    }
}
