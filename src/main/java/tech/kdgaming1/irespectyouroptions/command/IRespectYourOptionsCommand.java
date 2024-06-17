package tech.kdgaming1.irespectyouroptions.command;

import tech.kdgaming1.irespectyouroptions.IRespectYourOptions;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see IRespectYourOptions
 */
@Command(value = IRespectYourOptions.MODID, description = "Access the " + IRespectYourOptions.NAME + " GUI.")
public class IRespectYourOptionsCommand {
    @Main
    private void handle() {
        IRespectYourOptions.INSTANCE.config.openGui();
    }
}