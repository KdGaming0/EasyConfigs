package tech.kdgaming1.easyconfigs.easyconfighandler;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;
import tech.kdgaming1.easyconfigs.chatutils.ECChatUtils;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;


public class ECConfigFileManager {

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    static String exportDir = ECSetup.ECExport;

    public static void saveConfigs(int slot, boolean overwrite, boolean MCOptions) throws IOException {
        boolean addMCOptions = MCOptions;
        Path saveDirPath = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);

        LOGGER.info("Saving configs to slot " + slot + "...");
        if (Files.exists(saveDirPath)) {
            if (!overwrite) {
                throw new FileAlreadyExistsException("Save slot " + slot + " already exists.");
            } else {
                // Delete existing directory and its contents
                try {
                    Files.walk(saveDirPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete save slot " + slot + ".", e);
                    throw e;
                }
            }
        }
        // Save the config files
        ECOptionsApplier.saveConfigs(saveDirPath.toString(), addMCOptions);
        LOGGER.info("Configs saved to slot " + slot + ".");
    }

    public static void loadConfigs(int slot, boolean MCOptions) throws IOException {
        Path loadDir = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);

        if (!Files.exists(loadDir)) {
            throw new FileNotFoundException("Save slot " + slot + " does not exist.");
        } else {
            ECConfigs.wantToCopy = true;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Want To Copy", true).set(ECConfigs.wantToCopy);
            ECConfigs.copySlot = slot;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Config Slot", 0).set(ECConfigs.copySlot);
            ECConfigs.wantToIncludeMCOptions = MCOptions;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Include MCOptions", true).set(ECConfigs.wantToIncludeMCOptions);
            ECConfigs.getConfiguration().save();
            LOGGER.info("The copy config value have been set to true to allow the default options to be applied again. Restart the game to apply the default options. The value will be set to false after the default options have been applied.");
            ECChatUtils.printChatMessage("§aConfigs set to load from slot " + slot + ". Restart the game to apply the options.");
        }
    }

    public static void exportConfigs(String sourceFolder, String zipFileName, boolean addMCOptions) throws IOException {
        Path exportDirPath = Paths.get(exportDir);
        Path sourcePath;
        boolean isConfigFolder;

        if (!Files.exists(exportDirPath)) {
            Files.createDirectories(exportDirPath);
        }

        if (Objects.equals(sourceFolder, "config")) {
            isConfigFolder = true;
            sourcePath = Paths.get(ECSetup.runDir, sourceFolder);
        } else {
            isConfigFolder = false;
            sourcePath = Paths.get(ECSetup.ECDir, sourceFolder);
        }

        Path zipFilePath = exportDirPath.resolve(zipFileName + ".zip");

        // Check if the file already exists
        if (Files.exists(zipFilePath)) {
            throw new FileAlreadyExistsException("File " + zipFileName + ".zip already exists in the export directory. Please choose a different name.");
        }

        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("Source folder " + sourceFolder + " does not exist.");
        } else {
            ECOptionsApplier.zipDirectory(Paths.get(sourcePath.toString()), Paths.get(zipFilePath.toString()), addMCOptions, isConfigFolder);
            }
        LOGGER.info("Configs exported to " + zipFilePath.toString());
    }

    public static void importConfigs(int slot, Path importPath, boolean setAsCurrentConfigs, boolean addMCOptions) throws IOException {
        Path saveDirPath = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);

        if (!Files.exists(saveDirPath)) {
            Files.createDirectories(saveDirPath);
        }

        Path importSavePath = Paths.get(importPath + ".zip");

        // Use the unzipFile method from ECOptionsApplier to extract the zip file
        ECOptionsApplier.unzipFile(importSavePath, saveDirPath);

        if (setAsCurrentConfigs) {
            ECConfigs.wantToCopy = true;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Want To Copy", true).set(ECConfigs.wantToCopy);
            ECConfigs.copySlot = slot;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Config Slot", 0).set(ECConfigs.copySlot);
            ECConfigs.wantToIncludeMCOptions = addMCOptions;
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Include MCOptions", true).set(ECConfigs.wantToIncludeMCOptions);
            ECConfigs.getConfiguration().save();
            LOGGER.info("The copy config value have been set to true to allow the default options to be applied again. Restart the game to apply the default options. The value will be set to false after the default options have been applied.");
            ECChatUtils.printChatMessage("§aConfigs set to load from slot " + slot + ". Restart the game to apply the options.");
        }

        LOGGER.info("Configs imported from " + importPath.toString() + " to slot " + slot);
    }
}
