package tech.kdgaming1.irespectyouroptions.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import tech.kdgaming1.irespectyouroptions.config.IRespectYourOptionsConfig;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see IRespectYourOptionsConfig#hud
 */
public class IRespectYourOptionsHud extends SingleTextHud {
    public IRespectYourOptionsHud() {
        super("Have you selected a config options", true);
    }

    @Override
    public String getText(boolean example) {
        return "You have selected: " + IRespectYourOptionsConfig.exampleDropdown + " as your default options.";
    }
}
