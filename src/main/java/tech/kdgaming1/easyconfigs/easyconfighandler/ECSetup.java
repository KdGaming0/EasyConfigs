package tech.kdgaming1.easyconfigs.easyconfighandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ECSetup {

    public static String runDir = Paths.get("").toAbsolutePath().toString();
    public static String configDir = Paths.get(runDir, "config").toString();
    public static String ECDir = Paths.get(runDir, "EasyConfigs").toString();
    public static String ECExport = Paths.get(ECDir, "EasyConfigExport").toString();
    public static String ECImport = Paths.get(ECDir, "EasyConfigImport").toString();

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final String BLOCKLIST_FILE = "easyconfigs_blocklist.txt";
    private static final String DEFAULT_BLOCKLIST_RESOURCE = "/assets/easyconfigs/easyconfigs_blocklist.txt";
    private static final String MODPACK_INFO_FILE = "modpack_config_info.txt";

    public static void setup() {
        try {
            createDirectories();
            createBlocklistFile();
            createModpackInfoFile();
        } catch(Exception e){
            LOGGER.error("Failed to set up EasyConfigs.", e);
        }
    }

    private static void createDirectories() throws IllegalStateException {
        createDirectory(new File(ECDir), "EasyConfigs");
        createDirectory(new File(ECExport), "EasyConfigExport");
        createDirectory(new File(ECImport), "EasyConfigImport");
        File ECSave0 = createDirectory(new File(ECDir, "EasyConfigSave0"), "EasyConfigSave0");
        createDirectory(new File(ECSave0, "config"), "EasyConfigSave0/config");
    }

    private static File createDirectory(File directory, String name) throws IllegalStateException {
        if (!directory.exists() && !directory.mkdirs()) {
            LOGGER.error("Failed to create directory: " + directory.getAbsolutePath());
            throw new IllegalStateException("Could not create directory: " + directory.getAbsolutePath());
        }
        return directory;
    }

    private static void createBlocklistFile() {
        File blocklistFile = new File(configDir, BLOCKLIST_FILE);
        if (!blocklistFile.exists()) {
            try (InputStream is = ECSetup.class.getResourceAsStream(DEFAULT_BLOCKLIST_RESOURCE);
                 OutputStream os = Files.newOutputStream(blocklistFile.toPath())) {
                if (is == null) {
                    LOGGER.error("Default blocklist resource not found");
                    return;
                }
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                LOGGER.info("Created default blocklist file: " + blocklistFile.getAbsolutePath());
            } catch (Exception e) {
                LOGGER.error("Failed to create blocklist file", e);
            }
        }
    }

    private static void createModpackInfoFile() {
        File modpackInfoFile = new File(ECDir, MODPACK_INFO_FILE);
        if (!modpackInfoFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(modpackInfoFile))) {
                writer.println("# Modpack Configuration Information");
                writer.println("# Modpack creators: Replace this content with information about your modpack's configurations.");
                writer.println("# Explain which configurations users should load for different setups.");
                LOGGER.info("Created modpack info file: " + modpackInfoFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Failed to create modpack info file", e);
            }
        }
    }

    public static String readModpackInfo() {
        File modpackInfoFile = new File(ECDir, MODPACK_INFO_FILE);
        if (modpackInfoFile.exists()) {
            try {
                return new String(Files.readAllBytes(modpackInfoFile.toPath()));
            } catch (IOException e) {
                LOGGER.error("Failed to read modpack info file", e);
            }
        }
        return "No modpack-specific configuration information available.";
    }
}