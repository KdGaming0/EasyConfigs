package tech.kdgaming1.easyconfigs.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import tech.kdgaming1.easyconfigs.gui.ECGuiScreen;

public class ECKeyBindings {
    public static KeyBinding openMainECGui;

    public static void init() {
        openMainECGui = new KeyBinding("key.easyconfigs.openmaingui", Keyboard.KEY_K, "key.categories.easyconfigs");
        ClientRegistry.registerKeyBinding(openMainECGui);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openMainECGui.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ECGuiScreen());
        }

    }
}