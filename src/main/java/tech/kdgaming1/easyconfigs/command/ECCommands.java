package tech.kdgaming1.easyconfigs.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
        return "Usage: /EasyConfigs <saveConfigs|loadConfigs|loadDefaultConfigs> [1-9]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("EC", "ec", "EasyConfigs");
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
                        ECConfigs.copySlot = 0;
                        ECConfigs.getConfiguration().save();
                        ECChatUtils.printChatMessage("§aDefault configs loaded. Restart the game to apply the options.");
                    } else {
                        sender.addChatMessage(new ChatComponentText("§cThe loading of default configs this will overwrite your configs and the game will SHUTDOWN! Click to confirm!")
                                .setChatStyle(new ChatStyle()
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/EasyConfigs loadDefaultConfigs confirm"))));
                    }
                    break;
                case "confirmOverwrite":
                    confirmOverwrite(sender, args);
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
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/EasyConfigs confirmOverwrite " + slot))));
            } else {
                ECConfigFileManager.saveConfigs(slot, false);
                sender.addChatMessage(new ChatComponentText("§aConfigs saved to slot " + slot));
            }

        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            sender.addChatMessage(new ChatComponentText("§cAn error occurred while saving the configs: " + e.getMessage()));
            e.printStackTrace(); // Print stack trace to log for debugging
        }
    }

    public void confirmOverwrite(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot (1-9)"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            } else {
                ECConfigFileManager.saveConfigs(slot, true);
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

        Path saveDir = Paths.get(ECSetup.ECDir, "EasyConfigsSave" + slot);
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

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "loadDefaultConfigs", "saveConfigs", "loadConfigs");
        } else if (args.length == 2 && (args[0].equals("saveConfigs") || args[0].equals("loadAll"))) {
            return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "7", "8", "9");
        }
        return Arrays.asList();
    }
}
