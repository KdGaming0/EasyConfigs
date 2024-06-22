package tech.kdgaming1.easyconfigs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.kdgaming1.easyconfigs.chatutils.ECChatUtils;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

public class ECCommands extends CommandBase {

    public static int slot = 0;

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public String getCommandName() {
        return "EasyConfigs";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: /EasyConfigs <loadDefaultConfigs|loadConfigs|saveConfigs|exportConfigs|importConfigs> [1-9]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("EasyConfigs");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cPlease use an argument"));
        } else {
            switch (args[0]) {
                case "saveConfigs":
                    handleSaveConfigs(sender, args);
                    break;
                case "loadConfigs":
                    handleLoadConfigs(sender, args);
                    break;
                case "loadDefaultConfigs":
                    if (args.length == 2 && args[1].equals("confirm")) {
                        ECConfigs.wantToCopy = true;
                        ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Want To Copy", true).set(ECConfigs.wantToCopy);
                        ECConfigs.copySlot = 0;
                        ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Config Slot", 0).set(ECConfigs.copySlot);
                        ECConfigs.getConfiguration().save();
                        ECChatUtils.printChatMessage("§aDefault configs loaded. Restart the game to apply the options.");
                    } else {
                        sender.addChatMessage(new ChatComponentText("§cThe loading of default configs this will overwrite your configs! Click to confirm!")
                                .setChatStyle(new ChatStyle()
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/EasyConfigs loadDefaultConfigs confirm"))));
                        ECChatUtils.printChatMessage("§cRestart to apply the default options.");
                    }
                    break;
                case "confirmOverwrite":
                    handleConfirmOverwrite(sender, args);
                    break;
                case "exportConfigs":
                    handleExportConfigs(sender, args);
                    break;
                case "importConfigs":
                    handleImportConfigs(sender, args);
                    break;
                case "confirmImportOverwrite":
                    handleConfirmImportOverwrite(sender, args);
                    break;
                default:
                    sender.addChatMessage(new ChatComponentText("§cUnknown subcommand"));
                    break;
            }
        }
    }

    private void handleSaveConfigs(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot (1-9)"));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify if you want to add the Minecraft options to the save slot by defult only the contend inside the config folder. \n [ addMCOptions | don'tAddMCOptions ]"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            }

            Path saveDir = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);
            if (Files.exists(saveDir)) {
                // Prompt the user with a clickable confirmation message
                sender.addChatMessage(new ChatComponentText("§cSlot have already been saved to. Click to confirm overwrite.")
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/EasyConfigs confirmOverwrite " + slot + " " + args[2]))));
            } else {
                ECConfigFileManager.saveConfigs(slot, false, args[2].equalsIgnoreCase("addMCOptions"));
                sender.addChatMessage(new ChatComponentText("§aConfigs saved to slot " + slot));
            }

        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while saving the configs: " + e.getMessage()));
            e.printStackTrace(); // Print stack trace to log for debugging
        }
    }

    public void handleConfirmOverwrite(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot (1-9)"));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify if you want to add the Minecraft options to the save slot by defult only the contend inside the config folder. \n [ addMCOptions | don'tAddMCOptions ]"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            } else {
                ECConfigFileManager.saveConfigs(slot, true, args[2].equalsIgnoreCase("addMCOptions"));
                sender.addChatMessage(new ChatComponentText("§aConfigs saved to slot " + slot));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while saving the configs: " + e.getMessage()));
            e.printStackTrace(); // Print stack trace to log for debugging
        }
    }

    private void handleLoadConfigs(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a load slot (1-9)"));
            return;
        }

        Path saveDir = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);
        if (!Files.exists(saveDir)) {
            sender.addChatMessage(new ChatComponentText("§cSave slot does not exist"));
            return;
        }
        try {
            slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            }

            ECConfigFileManager.loadConfigs(slot);
            sender.addChatMessage(new ChatComponentText("§aConfigs loaded from slot " + slot));
            sender.addChatMessage(new ChatComponentText("§aRestart the game to apply the options."));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            LOGGER.error("Error loading configs: ", e);
            sender.addChatMessage(new ChatComponentText("§cError loading configs. Check logs for details."));
        }
    }

    private void handleExportConfigs(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a source folder (EasyConfigSave[1-9] or config for your config folder)."));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a name for your zip file. It can NOT have spaces in the name."));
            return;
        }

        String sourceFolder = args[1];
        String zipFileName;
        boolean addMCOptions = false;

        if (args[2].equalsIgnoreCase("addMCOptions") || args[2].equalsIgnoreCase("don'tAddMCOptions")) {
            if (args[2].equalsIgnoreCase("addMCOptions")) {
                addMCOptions = true;
            }
            if (args.length < 4) {
                sender.addChatMessage(new ChatComponentText("§cPlease specify a name for your zip file. It can NOT have spaces in the name."));
                return;
            }
            zipFileName = args[3];
        } else {
            zipFileName = args[2];
        }

        try {
            String zipFilePath = Paths.get(ECSetup.ECExport, zipFileName + ".zip").toString();

            ECConfigFileManager.exportConfigs(sourceFolder, zipFileName, addMCOptions);
            sender.addChatMessage(new ChatComponentText("§aConfigs exported to " + Paths.get(ECSetup.ECExport, zipFileName + ".zip").toString()));

            // Create a clickable chat message
            ChatComponentText clickableMessage = new ChatComponentText(EnumChatFormatting.GREEN + "Click here to open the location.");
            clickableMessage.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, zipFilePath))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Open folder"))));

            sender.addChatMessage(clickableMessage);

        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while exporting the configs: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void handleImportConfigs(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot for your import. [1-9]"));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify if you want to set the imported configs as current (requires restart) or only import to the save slot. [setAsCurrentConfigs|onlyImportToSaveSlot]"));
            return;
        }
        if (args.length < 4) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify the name of the import file. Remember to put it inside the EasyConfigs/EasyConfigImport folder. DO NOT include the .zip extension to the name."));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            }

            Path saveDir = Paths.get(ECSetup.ECDir, "EasyConfigSave" + slot);
            if (Files.exists(saveDir)) {
                // Prompt the user with a clickable confirmation message
                sender.addChatMessage(new ChatComponentText("§cSlot already contains configs. Click to confirm overwrite.")
                        .setChatStyle(new ChatStyle()
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/EasyConfigs confirmImportOverwrite " + slot + " " + args[2] + " " + args[3]))));
            } else {
                Path importPath = Paths.get(ECSetup.ECImport, args[3]);
                sender.addChatMessage(new ChatComponentText("§aImport path: " + importPath.toString()));
                boolean setAsCurrentConfigs = args.length > 3 && args[2].equalsIgnoreCase("setAsCurrentConfigs");

                ECConfigFileManager.importConfigs(slot, importPath, setAsCurrentConfigs);
                sender.addChatMessage(new ChatComponentText("§aConfigs imported to slot " + slot + "."));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while importing the configs: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void handleConfirmImportOverwrite(ICommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot, setAsCurrentConfigs or onlyImportToSaveSlot, and the import file name."));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            } else {
                Path importPath = Paths.get(ECSetup.ECImport, args[3]);
                sender.addChatMessage(new ChatComponentText("§aImport path: " + importPath.toString()));
                boolean setAsCurrentConfigs = args[2].equalsIgnoreCase("setAsCurrentConfigs");

                ECConfigFileManager.importConfigs(slot, importPath, setAsCurrentConfigs);
                sender.addChatMessage(new ChatComponentText("§aConfigs imported to slot " + slot + "."));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while importing the configs: " + e.getMessage()));
            e.printStackTrace();
        }
    }



    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "loadDefaultConfigs", "loadConfigs", "saveConfigs", "exportConfigs", "importConfigs");
        } else if (args.length == 2) {
            switch (args[0]) {
                case "saveConfigs":
                    return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "7", "8", "9");
                case "loadConfigs":
                    return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "7", "8", "9");
                case "exportConfigs":
                    return getListOfStringsMatchingLastWord(args, "config", "EasyConfigSave1", "EasyConfigSave2", "EasyConfigSave3", "EasyConfigSave4", "EasyConfigSave5", "EasyConfigSave6", "EasyConfigSave7", "EasyConfigSave8", "EasyConfigSave9");
                case "importConfigs":
                    return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "7", "8", "9");
            }
        } else if (args.length == 3) {
            if (args[0].equals("exportConfigs") && args[1].equals("config")) {
                return getListOfStringsMatchingLastWord(args, "addMCOptions", "don'tAddMCOptions");
            }
            switch (args[0]) {
                case "importConfigs":
                    return getListOfStringsMatchingLastWord(args, "setAsCurrentConfigs", "onlyImportToSaveSlot");
                case "saveConfigs":
                    return getListOfStringsMatchingLastWord(args, "addMCOptions", "don'tAddMCOptions");
            }
        }
        return Arrays.asList();
    }
}
