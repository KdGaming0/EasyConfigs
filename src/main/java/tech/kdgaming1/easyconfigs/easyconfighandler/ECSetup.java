package tech.kdgaming1.easyconfigs.easyconfighandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

import java.io.File;
import java.nio.file.Paths;

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
        } catch(Exception e){
            LOGGER.error("Failed to create directories.", e);
        }
    }
}
