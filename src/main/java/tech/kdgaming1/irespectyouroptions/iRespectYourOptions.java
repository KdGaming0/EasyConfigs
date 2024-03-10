package tech.kdgaming1.irespectyouroptions;

import java.io.File;
import java.io.IOException;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = iRespectYourOptions.MOD_ID, version = iRespectYourOptions.VERSION)
public class iRespectYourOptions {
    public static final String MOD_ID = "irespectyouroptions";
    public static final String VERSION = "0.1.0-1.8.9";

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        // Alerts the user that the mod is applying the default config
        System.out.println("I Respect Your Options is applying the default config");

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
        File defaultConfigFolder = new File(event.getModConfigurationDirectory().getParent(), "config/iRespectYourOptions");
        File userConfigFolder = new File(event.getModConfigurationDirectory().getParent(), "config");

        // Print the default config folder and the user config folder paths to the console
        System.out.println("Default Config Folder: " + defaultConfigFolder);
        System.out.println("User Config Folder: " + userConfigFolder);

        // create the default config folder if it doesn't exist and the user config folder if it doesn't exist
        if (!userConfigFolder.exists()) {
            userConfigFolder.mkdirs();
        }
        if (!defaultConfigFolder.exists()) {
            defaultConfigFolder.mkdirs();
        }

        // Apply the default configs if any exist
        if (defaultConfigFolder.listFiles().length > 0) {
            // Copy the files in the default config folder to the user config folder
            try {
                for (File file : defaultConfigFolder.listFiles()) {
                    File userConfigFile = new File(userConfigFolder, file.getName());
                    copyFunction(file, userConfigFile, userConfigFolder);
                    System.out.println("Copied " + file + " to " + userConfigFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to apply the default configs.");
            }
            // Create the file to say that the mod has run before
            try {
                iRespectYourOptionsHasRunBefore.createNewFile();
                System.out.println("Created the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before. So it will not overwrite the default config again. Delete this file if you want to apply the default config again.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before.");
            }
        } else {
            // If the default config folder is empty, print a message to the console and return
            System.out.println("The iRespectYourOptions directory is empty or does not exist. Cat't apply any configs.");
            // Create the file to say that the mod has run before
            try {
                // Create the file to say that the mod has run before
                iRespectYourOptionsHasRunBefore.createNewFile();
                System.out.println("Created the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before. So it will not apply the default config again.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to create the file " + iRespectYourOptionsHasRunBefore + " to say that the mod has run before.");
            }
            return;
        }
    }

    // Create the copy function
    public void copyFunction(File source, File destination, File userConfigFolder) throws IOException {
        // If the source is a directory, create the destination directory if it doesn't exist
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            // Copy the files in the source directory to the destination directory
            String files[] = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                // Special case for the minecraft options file and the optifine options file
                if (source.getName().equals("minecraft_options") && (file.equals("options.txt") || file.equals("optionsof.txt"))) {
                    destFile = new File(userConfigFolder.getParent(), file);
                }
                // Recursively copy the files in the source directory to the destination directory
                copyFunction(srcFile, destFile, userConfigFolder);
            }
            // If the source is a file, copy the file to the destination
        } else {
                java.nio.file.Files.copy(
                        source.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                        java.nio.file.LinkOption.NOFOLLOW_LINKS
                );
        }
    }
}