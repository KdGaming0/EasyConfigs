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

@Mod(modid = EasyConfigs.MOD_ID, version = EasyConfigs.VERSION, guiFactory = "tech.kdgaming1.easyconfigs.gui.ECGuiFactory")
public class EasyConfigs {
    public static final String MOD_ID = "easyconfigs";
    public static final String VERSION = "1.0.0-1.8.9";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    String runDir = ECSetup.runDir;
    String ECDir = ECSetup.ECDir;
    String ECSave = Paths.get(ECDir, "EasyConfigSave" + ECConfigs.copySlot).toString();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String configDir = event.getModConfigurationDirectory().toString();
        ECConfigs.init(configDir);
        FMLCommonHandler.instance().bus().register(new ECConfigs());

        ECSetup.setup();

        LOGGER.info("Applying default options... (EasyConfigs!)");
        if (!ECConfigs.wantToCopy) {
            LOGGER.info("Configs is set to NOT apply, if you want to apply the default options again do /IRYO loadConfigs 0 or [1-9] (1-9 is your own saves or if the mod pack developer have multiple different saves) and restart the game.\");  .");
        } else {
            LOGGER.info("Copying of config files have been set to true by you. Easy Configs is now copying the config files from your chosen ECConfig slot or the default config slot to the config folder.");
            ECOptionsApplier.apply(ECSave, runDir);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ECKeyBindings.init();
        ClientCommandHandler.instance.registerCommand(new ECCommands());
        MinecraftForge.EVENT_BUS.register(new ECKeyBindings());
    }
}
