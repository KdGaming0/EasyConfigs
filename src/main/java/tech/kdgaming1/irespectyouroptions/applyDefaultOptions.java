package tech.kdgaming1.irespectyouroptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class applyDefaultOptions {

    private static final Logger LOGGER = LogManager.getLogger(iRespectYourOptions.class);

    public static void apply() {
        String runDir = iRespectYourOptions.runDir;
        String configDir = iRespectYourOptions.configDir;

        try {
            Files.walk(new File(iRespectYourOptions.configDir, "iRespectYourOptions").toPath()).forEach(path -> {
                File file = path.normalize().toAbsolutePath().normalize().toFile();
                if (!file.isFile()) return;
                try {
                    applyDefaultOptions(new File(configDir, file.getName()), file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            iRespectYourOptions.hasCopy = true;
            LOGGER.info("Default options have been successful applied. If you want to override your options bake to the default, delete the iRespectYourOptions.cfg in your config folder or change the value inside it from true to false and save and start the game.");
        } catch (Exception e) {
            LOGGER.error("Failed to apply default options.", e);
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
            LOGGER.info("Applying default options for " + File.separator + file.toPath().toAbsolutePath().normalize().toString() + " from " + File.separator +
                    defaultFile.toPath().toAbsolutePath().normalize().toString());
            Files.copy(defaultFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (FileSystemException e) {
            LOGGER.error("File is being used by another process: " + defaultFile.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }
}