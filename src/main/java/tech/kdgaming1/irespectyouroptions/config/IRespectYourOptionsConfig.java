package tech.kdgaming1.irespectyouroptions.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import tech.kdgaming1.irespectyouroptions.IRespectYourOptions;
import tech.kdgaming1.irespectyouroptions.hud.IRespectYourOptionsHud;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

public class IRespectYourOptionsConfig extends Config {
    @HUD(
            name = "I Respect Your Options"
    )
    public IRespectYourOptionsHud hud = new IRespectYourOptionsHud();

    @Info(
            text = "I Respect Your Options can copy different sets of default configs to the config folder." +
                    "The mod pack creator can choose which set of default configs they want ship with their pack.",
            type = InfoType.INFO // Types are: INFO, WARNING, ERROR, SUCCESS
    )

    @Dropdown(
            name = "Choose the set of default options you want!", // Name of the Dropdown
            options = {"Option 1", "Option 2", "Option 3", "Option 4"} // Options available.
    )
    public static int exampleDropdown = 1; // Default option (in this case "Option 2")

    @Info(
            text = "I Respect Your Options can copy configs from the iRespectYourOptions folder to the config folder. This will overwrite the current config file. So be careful and only turn on when you want to copy the default configs!",
            type = InfoType.INFO // Types are: INFO, WARNING, ERROR, SUCCESS
    )

    @Switch(
            name = "Want to run the copy of the config? Turn this on!",
            size = OptionSize.SINGLE // Optional
    )
    public static boolean wantToRun = true; // The default value for the boolean Switch.


    public IRespectYourOptionsConfig() {
        super(new Mod(IRespectYourOptions.NAME, ModType.UTIL_QOL), IRespectYourOptions.MODID + ".json");
        initialize();
    }
}

