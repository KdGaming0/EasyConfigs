package tech.kdgaming1.easyconfigs;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tech.kdgaming1.easyconfigs.config.ECConfigs;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECOptionsApplier;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.keybinds.ECKeyBindings;
import tech.kdgaming1.easyconfigs.command.ECCommands;

import java.nio.file.Paths;

@Mod(modid = EasyConfigs.MOD_ID, version = EasyConfigs.VERSION, guiFactory = "tech.kdgaming1.easyconfigs.config.gui.ECGuiFactory")
public class EasyConfigs {
    public static final String MOD_ID = "easyconfigs";
    public static final String VERSION = "1.0.0-1.8.9";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String configDir = event.getModConfigurationDirectory().toString();
        ECConfigs.init(configDir);

        String runDir = ECSetup.runDir;
        String ECSave = Paths.get(ECSetup.ECDir, "EasyConfigSave" + ECConfigs.copySlot).toString();

        ECSetup.setup();

        LOGGER.info("Copy Slot: " + ECConfigs.copySlot);
        LOGGER.info("EC Save path: " + ECSave);

        LOGGER.info("Applying default options... (EasyConfigs!)");
        if (!ECConfigs.wantToCopy) {
            LOGGER.info("Copying of config files have been set to false by you. Easy Configs will not copy the config files from your chosen ECConfig slot or the default config slot to the config folder. \n If you want to copy the config files, Do /EasyConfigs LoadConfigs [1-9] or LoadDefaultConfigs.");
        } else {
            LOGGER.info("Copying of config files have been set to true by you. Easy Configs is now copying the config files from your chosen ECConfig slot or the default config slot to the config folder.");
            ECOptionsApplier.apply(ECSave, runDir);
        }

        FMLCommonHandler.instance().bus().register(new ECConfigs());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ECKeyBindings.init();
        MinecraftForge.EVENT_BUS.register(new ECKeyBindings());
        ClientCommandHandler.instance.registerCommand(new ECCommands());
    }
}
