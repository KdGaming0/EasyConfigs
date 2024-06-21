package tech.kdgaming1.easyconfigs.easyconfighandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ECOptionsApplier {

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void apply(String sourceDir, String destinationDir) {

        File sourceFolder = new File(sourceDir);

        if (!sourceFolder.exists()) {
            LOGGER.info("Source directory " + sourceDir + " does not exist. " + "Loacation of the save slot: " + sourceFolder.getAbsolutePath());
            return;
        }

        try {
            Files.walk(sourceFolder.toPath()).forEach(path -> {
                File defaultFile = path.normalize().toAbsolutePath().normalize().toFile();
                if (!defaultFile.isFile()) return;
                try {
                    String relativePath = sourceFolder.toPath().relativize(path).toString();
                    File targetFile = new File(destinationDir, relativePath);
                    applyDefaultOptions(targetFile, defaultFile);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            LOGGER.info("Options have been successfully applied.");
            ECConfigs.wantToCopy = false;
            LOGGER.info("The copy value is now: {}", ECConfigs.wantToCopy);
            ECConfigs.getConfiguration().save();
            LOGGER.info("The copy config value have been set to false to prevent the default options from being applied again. If you want to apply the default options again do /EasyConfigs loadConfigs 0 or [1-9] (1-9 is your own saves or if the mod pack developer have multiple different saves) and restart the game.");
        } catch (Exception e) {
            LOGGER.error("Failed to walk the directory three.", e);
            LOGGER.error("Failed to apply options. Please check the log for more information. If you need help, please contact the mod author or mod pack creator.");
            LOGGER.error("Please make sure that the save slot is correct and try again. (Restart the game after choosing your options.)");
        }
    }

    public static void applyDefaultOptions(File file, File defaultFile) {
        try {
            if (!file.exists()) {
                LOGGER.info("Target file does not exist: {}", file.getAbsolutePath());
                LOGGER.info("Creating new file with default content: {}", defaultFile.getAbsolutePath());
                createFileWithDefaultContent(file, defaultFile);
                return;
            }

            // Check if the file extension is one of cfg, toml, json, or txt
            String fileExtension = getFileExtension(file);
            boolean shouldModify = fileExtension != null && (fileExtension.equals("cfg") || fileExtension.equals("toml") || fileExtension.equals("json") || fileExtension.equals("txt"));

            if (shouldModify) {
                LOGGER.info("Modifying content of {} based on {}", file.getAbsolutePath(), defaultFile.getAbsolutePath());
                // Read default content
                String defaultContent = readContent(defaultFile);

                // Write default content to target file
                writeContent(file, defaultContent);
            } else {
                // If not one of the specified extensions, replace the file entirely
                LOGGER.info("Replacing content of {} with {}", file.getAbsolutePath(), defaultFile.getAbsolutePath());
                replaceFile(file, defaultFile);
            }

            LOGGER.info("Default options successfully applied for {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Failed to apply default options.", e);
        }
    }

    private static void createFileWithDefaultContent(File file, File defaultFile) throws IOException {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create directory: " + file.getParentFile().getAbsolutePath());
        }

        // Read default content
        String defaultContent = readContent(defaultFile);

        // Write default content to new file
        writeContent(file, defaultContent);
    }

    private static String readContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    private static void writeContent(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    private static void replaceFile(File targetFile, File sourceFile) throws IOException {
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }
}
