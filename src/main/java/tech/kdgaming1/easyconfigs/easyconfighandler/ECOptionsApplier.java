package tech.kdgaming1.easyconfigs.easyconfighandler;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ECOptionsApplier {

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // Method to walk the directory three and apply default options
    public static void apply(String sourceDir, String destinationDir) {

        File sourceFolder = new File(sourceDir);

        if (!sourceFolder.exists()) {
            LOGGER.info("Source directory " + sourceDir + " does not exist. " + "Location of the save slot: " + sourceFolder.getAbsolutePath());
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
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Want To Copy", true).set(ECConfigs.wantToCopy);
            ECConfigs.getConfiguration().save();
            LOGGER.info("The copy config value have been set to false to prevent the default options from being applied again. If you want to apply the default options again do /EasyConfigs loadConfigs 0 or [1-9] (1-9 is your own saves or if the mod pack developer have multiple different saves) and restart the game.");
        } catch (Exception e) {
            LOGGER.error("Failed to walk the directory three.", e);
            LOGGER.error("Please make sure that the save slot is correct and try again. (Restart the game after choosing your options.)");
            LOGGER.error("Failed to apply options. Please check the log for more information. If you need help, please contact the mod author or mod pack creator.");
        }
    }

    //Method to save configs to a slot
    public static void saveConfigs(String destinationDir, boolean MCOptions) {
        Path sourceDir = Paths.get(ECSetup.runDir);

        // Define the specific paths to be saved
        List<String> pathsToSave = new ArrayList<>();
        if (MCOptions) {
            pathsToSave.add("options.txt");
            pathsToSave.add("optionsof.txt");
            pathsToSave.add("servers.dat");
            pathsToSave.add("servers.dat_old");
        }
        // Check if OneConfig directory exists
        File oneConfigDir = new File(sourceDir.toFile(), "OneConfig");
        if (oneConfigDir.exists() && oneConfigDir.isDirectory()) {
            pathsToSave.add("OneConfig/OneConfig.json");
            pathsToSave.add("OneConfig/Preferences.json");
            pathsToSave.add("OneConfig/profiles/Default Profile");
            pathsToSave.add("OneConfig/profiles/config");
        }
        pathsToSave.add("config");

        // Iterate over the source directory files
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String relativePath = sourceDir.relativize(path).toString();

                // Check if the current path matches any path to be saved
                for (String pathToSave : pathsToSave) {
                    if (relativePath.equals(pathToSave) || relativePath.startsWith(pathToSave)) {
                        File defaultFile = path.toFile();
                        File targetFile = new File(destinationDir, relativePath);
                        applyDefaultOptions(targetFile, defaultFile);
                        break;
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to save configs.", e);
        }
    }

    // Method to apply default options
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

    // Method to create a file with default content
    private static void createFileWithDefaultContent(File file, File defaultFile) throws IOException {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create directory: " + file.getParentFile().getAbsolutePath());
        }

        // Read default content
        String defaultContent = readContent(defaultFile);

        // Write default content to new file
        writeContent(file, defaultContent);
    }

    // Method to read content from a file
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

    // Method to write content to a file if it ends with cfg, toml, json, or txt
    private static void writeContent(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    // Method to replace a file with default content if it does not end with cfg, toml, json, or txt
    private static void replaceFile(File targetFile, File sourceFile) throws IOException {
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    // Method to get the file extension
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }

    // Method to zip a directory
    public static void zipDirectory(Path sourceDir, Path zipFile, boolean MCOptions, boolean isConfigFolder) throws IOException {
        String tempFolder = Paths.get(ECSetup.ECDir, "temp").toString();
        // Check if the sourceDir is config
        if (isConfigFolder) {
            saveConfigs(tempFolder, MCOptions);
            sourceDir = Paths.get(tempFolder);
        }
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
                Path finalSourceDir = sourceDir;
                Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        zipOutputStream.putNextEntry(new ZipEntry(finalSourceDir.relativize(file).toString()));
                        Files.copy(file, zipOutputStream);
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                        zipOutputStream.putNextEntry(new ZipEntry(finalSourceDir.relativize(dir).toString() + "/"));
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
                LOGGER.info("Directory successfully zipped.");
                // Delete the temp folder
                try {
                    if (Files.exists(Paths.get(tempFolder))) {
                    Files.walk(Paths.get(tempFolder))
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to delete temp folder", e);
                }
            }catch (IOException e) {
                LOGGER.error("Failed to zip directory.", e);
            }
}

    // Method to unzip a file
    public static void unzipFile(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = zipInputStream.read(buffer)) != -1) {
                            bos.write(buffer, 0, read);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to unzip file.", e);
        }
    }
}
