package tech.kdgaming1.easyconfigs.gui;


import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import tech.kdgaming1.easyconfigs.EasyConfigs;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

public class ECConfigGui extends GuiConfig {
    public ECConfigGui (GuiScreen guiScreen) {
        super(
                guiScreen,
                new ConfigElement(ECConfigs.getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                EasyConfigs.MOD_ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ECConfigs.getConfiguration().toString())
        );
    }
}
