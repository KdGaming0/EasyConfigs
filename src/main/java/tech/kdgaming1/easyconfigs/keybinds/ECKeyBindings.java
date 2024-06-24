package tech.kdgaming1.easyconfigs.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import tech.kdgaming1.easyconfigs.gui.ECGuiImportAndExportManager;
import tech.kdgaming1.easyconfigs.gui.ECGuiScreen;
import tech.kdgaming1.easyconfigs.gui.ECGuiStartScreen;

public class ECKeyBindings {
    public static KeyBinding openGuiKey;
    public static KeyBinding openStartScreenKey;
    public static KeyBinding openMainECGui;

    public static void init() {
        openGuiKey = new KeyBinding("key.easyconfigs.opengui", Keyboard.KEY_G, "key.categories.easyconfigs");
        openStartScreenKey = new KeyBinding("key.easyconfigs.openstartscreen", Keyboard.KEY_H, "key.categories.easyconfigs"); // Initialize the new key binding
        openMainECGui = new KeyBinding("key.easyconfigs.openmaingui", Keyboard.KEY_K, "key.categories.easyconfigs");
        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(openStartScreenKey); // Register the new key binding
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ECGuiImportAndExportManager(null));
        }
        if (openStartScreenKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ECGuiStartScreen());
        }
        if (openMainECGui.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ECGuiScreen());
        }

    }
}