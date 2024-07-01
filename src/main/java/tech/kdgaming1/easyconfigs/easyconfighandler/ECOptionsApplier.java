package tech.kdgaming1.easyconfigs.easyconfighandler;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

public class ECOptionsApplier {

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final String BLOCKLIST_FILE = "easyconfigs_blocklist.txt";
    private static Set<String> blocklist = new HashSet<>();

    public static void apply(String sourceDir, String destinationDir, boolean MCOptions) {
        File sourceFolder = new File(sourceDir);
        if (!sourceFolder.exists()) {
            LOGGER.info("Source directory {} does not exist. Location: {}", sourceDir, sourceFolder.getAbsolutePath());
            return;
        }

        if (ECConfigs.useBlocklist) {
            loadBlocklist();
        }

        List<String> pathsToExclude = Arrays.asList("options.txt", "optionsof.txt", "servers.dat", "servers.dat_old");

        try (Stream<Path> paths = Files.walk(sourceFolder.toPath()).parallel()) {
            paths.forEach(path -> {
                File defaultFile = path.toFile();
                if (!defaultFile.isFile()) return;

                String relativePath = sourceFolder.toPath().relativize(path).toString();
                if (!MCOptions && pathsToExclude.contains(relativePath)) return;

                if (!isBlocked(relativePath)) {
                    try {
                        File targetFile = new File(destinationDir, relativePath);
                        applyDefaultOptions(targetFile, defaultFile);
                    } catch (Exception e) {
                        LOGGER.error("Error applying default options for file: {}", defaultFile.getAbsolutePath(), e);
                    }
                } else {
                    if (ECConfigs.advancedLogging) {
                        LOGGER.debug("Skipping blocked file or directory: {}", relativePath);
                    }
                }
            });
            LOGGER.info("Options have been successfully applied.");
            updateConfig();
        } catch (Exception e) {
            LOGGER.error("Failed to walk the directory tree.", e);
        }
    }

    private static void loadBlocklist() {
        File blocklistFile = new File(ECSetup.configDir, BLOCKLIST_FILE);
        if (!blocklistFile.exists()) {
            LOGGER.warn("Blocklist file not found: {}. Blocklist will be empty.", blocklistFile.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(blocklistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    blocklist.add(line);
                }
            }
            if (ECConfigs.advancedLogging) {
            LOGGER.info("Loaded {} entries from blocklist file.", blocklist.size());
            }
        } catch (IOException e) {
            LOGGER.error("Error reading blocklist file", e);
        }
    }

    private static boolean isBlocked(String relativePath) {
        if (!ECConfigs.useBlocklist) return false;
        String normalizedPath = relativePath.replace("\\", "/");
        boolean isBlocked = blocklist.stream().anyMatch(blockedPath ->
                normalizedPath.startsWith(blockedPath) || normalizedPath.equals(blockedPath));
        if (isBlocked) {
            if (ECConfigs.advancedLogging) {
                LOGGER.debug("Skipping blocked file or directory: {}", normalizedPath);
            }
        } else {
            if (ECConfigs.advancedLogging) {
                LOGGER.debug("Copying non-blocked file or directory: {}", normalizedPath);
            }
        }
        return isBlocked;
    }
    public static void saveConfigs(String destinationDir, boolean MCOptions) {
        Path sourceDir = Paths.get(ECSetup.runDir);
        List<String> pathsToSave = new ArrayList<>(Arrays.asList("config"));
        if (MCOptions) {
            pathsToSave.addAll(Arrays.asList("options.txt", "optionsof.txt", "servers.dat", "servers.dat_old"));
        }

        checkAndAddDirectories(sourceDir, pathsToSave, "OneConfig", "essential");

        // Load the blocklist
        if (ECConfigs.useBlocklist) {
            loadBlocklist();
        }

        try (Stream<Path> paths = Files.walk(sourceDir).parallel()) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                String relativePath = sourceDir.relativize(path).toString();
                if (pathsToSave.stream().anyMatch(relativePath::startsWith) && !isBlocked(relativePath)) {
                    try {
                        applyDefaultOptions(new File(destinationDir, relativePath), path.toFile());
                    } catch (IOException e) {
                        LOGGER.error("Error saving config for file: {}", path.toFile().getAbsolutePath(), e);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to save configs.", e);
        }
    }

    private static void applyDefaultOptions(File file, File defaultFile) throws IOException {
        try {
            createParentDirectories(file);
            if (!defaultFile.exists()) {
                LOGGER.info("Default file does not exist: {}", defaultFile.getAbsolutePath());
                if (!defaultFile.createNewFile()) {
                    throw new IOException("Failed to create default file: " + defaultFile.getAbsolutePath());
                }
                return;
            }
            if (ECConfigs.advancedLogging) {
                LOGGER.info("Applying default options from {} to {}", defaultFile.getAbsolutePath(), file.getAbsolutePath());
            }
            try (InputStream in = new BufferedInputStream(Files.newInputStream(defaultFile.toPath()));
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (FileSystemException e) {
            LOGGER.error("File is being used by another process: {}", defaultFile.getAbsolutePath(), e);
        } catch (IOException e) {
            LOGGER.error("Error applying default options for file: {}", defaultFile.getAbsolutePath(), e);
            throw e;
        }
    }

    public static void zipDirectory(Path sourceDir, Path zipFile, boolean MCOptions, boolean isConfigFolder) throws IOException {
        String tempFolder = Paths.get(ECSetup.ECDir, "temp").toString();
        if (isConfigFolder) {
            saveConfigs(tempFolder, MCOptions);
            sourceDir = Paths.get(tempFolder);
        }

        if (ECConfigs.useBlocklist && isConfigFolder) {
            loadBlocklist();
        }

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile.toFile().toPath())))) {
            Path finalSourceDir = sourceDir;
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    String relativePath = finalSourceDir.relativize(file).toString();
                    if (!isConfigFolder || !isBlocked(relativePath)) {
                        zipOutputStream.putNextEntry(new ZipEntry(relativePath));
                        Files.copy(file, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                    String relativePath = finalSourceDir.relativize(dir).toString();
                    if (!isConfigFolder || !isBlocked(relativePath)) {
                        zipOutputStream.putNextEntry(new ZipEntry(relativePath + "/"));
                        zipOutputStream.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            LOGGER.info("Directory successfully zipped.");
            deleteTempFolder(tempFolder);
        } catch (IOException e) {
            LOGGER.error("Failed to zip directory.", e);
            throw e;
        }
    }

    public static void unzipFile(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile.toFile())))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
            LOGGER.info("File successfully unzipped to {}", targetDir);
        } catch (IOException e) {
            LOGGER.error("Failed to unzip file.", e);
            throw e;
        }
    }

    private static void createParentDirectories(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Failed to create directory: " + parentFile.getAbsolutePath());
        }
    }

    private static void deleteTempFolder(String tempFolder) {
        try {
            Files.walk(Paths.get(tempFolder))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            LOGGER.info("Temporary folder deleted: {}", tempFolder);
        } catch (Exception e) {
            LOGGER.error("Failed to delete temp folder: {}", tempFolder, e);
        }
    }

    private static void updateConfig() {
        ECConfigs.wantToCopy = false;
        ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Want To Copy", true).set(ECConfigs.wantToCopy);
        ECConfigs.getConfiguration().save();
        LOGGER.info("Config value 'Want To Copy' set to false. This will prevent copying of config files in the future. To enable copying, go to the EasyConfigs GUI and click load configs.");
    }

    private static void checkAndAddDirectories(Path sourceDir, List<String> pathsToSave, String... directories) {
        for (String dir : directories) {
            File directory = new File(sourceDir.toFile(), dir);
            if (directory.exists() && directory.isDirectory()) {
                pathsToSave.add(dir);
            }
        }
    }
}
