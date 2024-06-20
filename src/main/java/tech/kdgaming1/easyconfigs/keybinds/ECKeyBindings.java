package tech.kdgaming1.easyconfigs.keybinds;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import tech.kdgaming1.easyconfigs.gui.ECConfigGui;

public class ECKeyBindings {
    public static KeyBinding openConfigGuiKey;

    public static void init() {
        openConfigGuiKey = new KeyBinding("key.opengui", Keyboard.KEY_G, "key.categories.mymod");
        ClientRegistry.registerKeyBinding(openConfigGuiKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openConfigGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ECConfigGui(null));
        }
    }
}

