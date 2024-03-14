package tech.kdgaming1.irespectyouroptions;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = iRespectYourOptions.MOD_ID, version = iRespectYourOptions.VERSION)
public class iRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.1.1-1.8.9";

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        // Alerts the user that the mod is applying the default config
        System.out.println("I Respect Your Options is applying the default config...");

        // Create a file to say that the mod has run before
        File iRespectYourOptionsHasRunBefore = new File(event.getModConfigurationDirectory(), "I Respect Your Options Has Run Before.txt");

        // If the file exists, return
        if (iRespectYourOptionsHasRunBefore.exists()) {
            System.out.println("I Respect Your Options has run before, so it will not apply the default config.");
            System.out.println("If you want to apply the default config, delete the file " + iRespectYourOptionsHasRunBefore + " in the config folder and restart the game.");
            System.out.println("This will overwrite your current config, so make sure to back it up if you want to keep it.");
            return;
        }

        // Get the directory of the config folder and the default config folder
        Path defaultConfigFolder = Paths.get(event.getModConfigurationDirectory().getParent(), "config/iRespectYourOptions");
        Path userConfigFolder = Paths.get(event.getModConfigurationDirectory().getParent(), "config");

        // Print the default config folder and the user config folder paths to the console
        System.out.println("Default Config Folder: " + defaultConfigFolder);
        System.out.println("User Config Folder: " + userConfigFolder);

        // create the default config folder if it doesn't exist and the user config folder if it doesn't exist
        // ...

        try {
            Files.createDirectories(userConfigFolder);
            Files.createDirectories(defaultConfigFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    // Apply the default configs if any exist
        try {
        boolean isDefaultConfigFolderEmpty = isEmpty(defaultConfigFolder);
        
        if (!isDefaultConfigFolderEmpty) {
            // Copy the files in the default config folder to the user config folder
            try {
                copyDirectory(defaultConfigFolder, userConfigFolder);
                System.out.println("Copied " + defaultConfigFolder + " to " + userConfigFolder);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to apply the default configs.");
            }
            // Create the file to say that the mod has run before
            try {
                iRespectYourOptionsHasRunBefore.createNewFile();
                System.out.println("Created the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before. So it will not apply the default config again.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before.");
            }
        } else {
            // If the default config folder is empty, print a message to the console and return
                System.out.println("The iRespectYourOptions directory is empty or does not exist. Can't apply any configs.");
            // Create the file to say that the mod has run before
                try {
                    iRespectYourOptionsHasRunBefore.createNewFile();
                    System.out.println("Created the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before. So it will not apply the default config again.");
                } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before.");
                }
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to copy the default configs.");
        }
    }

    // Check if a directory is empty
    public boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to check if the directory " + path + " is empty.");
            }
        }   
        return false;
    }

    // Copy the files in the default config folder to the user config folder
    public static void copyDirectory(Path sourceDirectory, Path destinationDirectory) throws IOException {
        Files.walk(sourceDirectory)
            .forEach(source -> {
                try {
                    // Special case for the minecraft options file and the optifine options file
                    if (source.getFileName().toString().equals("minecraft_options")) {
                        Files.list(source).forEach(fileInsideMinecraftOptions -> {
                            Path destination = destinationDirectory.getParent().resolve(fileInsideMinecraftOptions.getFileName());
                            try {
                                Files.copy(fileInsideMinecraftOptions, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (!source.equals(sourceDirectory)) {
                        Path destination = destinationDirectory.resolve(sourceDirectory.relativize(source));
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
}