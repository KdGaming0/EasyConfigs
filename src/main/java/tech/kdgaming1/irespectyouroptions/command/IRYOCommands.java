package tech.kdgaming1.irespectyouroptions.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.kdgaming1.irespectyouroptions.IRespectYourOptions;
import tech.kdgaming1.irespectyouroptions.IRYODefaultOptionsApplier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class IRYOCommands extends CommandBase {

    public static int slot = 0;

    private static final Logger LOGGER = LogManager.getLogger(IRYOCommands.class);

    @Override
    public String getCommandName() {
        return "IRYO";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Usage: /IRYO <saveConfigs|loadConfigs> [1-9]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("IRYO", "iryo", "irrespectyouroptions");
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
                case "loadAll":
                    handleLoadConfigs(sender, args);
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

            Path saveDir = Paths.get(IRespectYourOptions.IRYODir, "IYROSave" + slot);
            if (Files.exists(saveDir)) {
                // Prompt the user with a clickable confirmation message
                ChatComponentText confirmationMessage = new ChatComponentText("§cSave slot already exists. Click to overwrite: ");
                ChatComponentText clickableMessage = new ChatComponentText("§a[Overwrite]");
                clickableMessage.setChatStyle(new ChatStyle().setChatClickEvent(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/IRYO confirmOverwrite " + slot)));
                confirmationMessage.appendSibling(clickableMessage);
                sender.addChatMessage(confirmationMessage);
            } else {
                IRYODefaultOptionsApplier.saveConfigs(slot);
                sender.addChatMessage(new ChatComponentText("§aConfigs saved to slot " + slot));
            }
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            LOGGER.error("Error saving configs: ", e);
            sender.addChatMessage(new ChatComponentText("§cError saving configs. Check logs for details."));
        }
    }

    private void handleLoadConfigs(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a load slot (1-9)"));
            return;
        }

        try {
            slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            }

            IRYODefaultOptionsApplier.loadConfigs(slot);
            sender.addChatMessage(new ChatComponentText("§aConfigs loaded from slot " + slot));
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
            return getListOfStringsMatchingLastWord(args, "weather", "coinflip", "saveConfigs", "loadAll");
        } else if (args.length == 2 && (args[0].equals("saveConfigs") || args[0].equals("loadAll"))) {
            return getListOfStringsMatchingLastWord(args, "1", "2", "3", "4", "5", "6", "7", "8", "9");
        }
        return Arrays.asList();
    }

    public void confirmOverwrite(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cPlease specify a save slot (1-9) to overwrite"));
            return;
        }

        try {
            int slot = Integer.parseInt(args[1]);
            if (slot < 1 || slot > 9) {
                sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
                return;
            }

            Path saveDir = Paths.get(IRespectYourOptions.IRYODir, "save" + slot);
            Files.walk(saveDir).forEach(sourcePath -> {
                try {
                    Files.delete(sourcePath);
                } catch (IOException e) {
                    LOGGER.error("Error overwriting save slot: ", e);
                }
            });

            IRYODefaultOptionsApplier.saveConfigs(slot);
            sender.addChatMessage(new ChatComponentText("§aSave slot " + slot + " overwritten."));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("§cInvalid slot number. Please specify a number between 1 and 9."));
        } catch (IOException e) {
            LOGGER.error("Error overwriting save slot: ", e);
            sender.addChatMessage(new ChatComponentText("§cError overwriting save slot. Check logs for details."));
        }
    }
}
