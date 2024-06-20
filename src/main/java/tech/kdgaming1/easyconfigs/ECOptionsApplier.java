package tech.kdgaming1.easyconfigs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.kdgaming1.easyconfigs.command.ECCommands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

public class ECOptionsApplier {

    private static final Logger LOGGER = LogManager.getLogger(EasyConfigs.class);

    public static void apply() {
        String runDir = EasyConfigs.runDir;
        String ECDir = EasyConfigs.ECDir;
        String ECSave = Paths.get(ECDir, "EasyConfigsSave" + ECCommands.slot).toString();
        File EasyConfigsFolder = new File(ECSave);

        try {
            Files.walk(EasyConfigsFolder.toPath()).forEach(path -> {
                File defaultFile = path.normalize().toAbsolutePath().normalize().toFile();
                if (!defaultFile.isFile()) return;
                try {
                    String relativePath = EasyConfigsFolder.toPath().relativize(path).toString();
                    File targetFile = new File(runDir, relativePath);
                    applyDefaultOptions(targetFile, defaultFile);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            LOGGER.info("Options have been successfully applied. If you want to override your options back to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
        } catch (Exception e) {
            LOGGER.error("Failed to walk the directory three.", e);
        }
    }

    public static void applyDefaultOptions(File file, File defaultFile) throws IOException {
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + file.getParentFile().getAbsolutePath());
            }
            if (!defaultFile.getParentFile().exists() && !defaultFile.getParentFile().mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + defaultFile.getParentFile().getAbsolutePath());
            }
            if (!defaultFile.exists()) {
                LOGGER.info("Default file does not exist: " + defaultFile.getAbsolutePath());
                defaultFile.createNewFile();
                return;
            }
            LOGGER.info("Applying default options for " + file.toPath().toAbsolutePath().normalize() + " from " + defaultFile.toPath().toAbsolutePath().normalize());
            Files.copy(defaultFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (FileSystemException e) {
            LOGGER.error("File is being used by another process: " + defaultFile.getAbsolutePath(), e);
        } catch (FileNotFoundException e) {
            LOGGER.error("Default file does not exist: " + defaultFile.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }

    public static void saveConfigs(int slot) throws IOException {
        Path configDir = Paths.get(EasyConfigs.configDir);
        Path saveDir = Paths.get(EasyConfigs.ECDir, "save" + slot);

        if (Files.exists(saveDir)) {
            throw new FileAlreadyExistsException("Save slot " + slot + " already exists.");
        }

        Files.createDirectories(saveDir);
        Files.walk(configDir).forEach(sourcePath -> {
            try {
                Path targetPath = saveDir.resolve(configDir.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                LOGGER.error("Error saving configs: ", e);
            }
        });
    }

    public static void loadConfigs(int slot) throws IOException {
        Path configDir = Paths.get(EasyConfigs.configDir);
        Path loadDir = Paths.get(EasyConfigs.ECDir, "save" + slot);

        if (!Files.exists(loadDir)) {
            throw new FileNotFoundException("Save slot " + slot + " does not exist.");
        }

        Files.walk(loadDir).forEach(sourcePath -> {
            try {
                Path targetPath = configDir.resolve(loadDir.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                LOGGER.error("Error loading configs: ", e);
            }
        });
    }
}
