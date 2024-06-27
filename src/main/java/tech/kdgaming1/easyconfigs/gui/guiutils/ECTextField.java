package tech.kdgaming1.easyconfigs.gui.guiutils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.util.function.BiConsumer;

public class ECTextField extends GuiTextField {
    private BiConsumer<Character, Integer> keyTypedCallback;

    public ECTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);
    }

    public void setKeyTypedCallback(BiConsumer<Character, Integer> keyTypedCallback) {
        this.keyTypedCallback = keyTypedCallback;
    }

    @Override
    public void drawTextBox() {
        if (this.getVisible()) {
            super.drawTextBox();
        }
    }


    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (this.isFocused()) {
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                // Handle enter key
                return true;
            }
            boolean result = super.textboxKeyTyped(typedChar, keyCode);
            // Call the key typed callback
            if (keyTypedCallback != null) {
                keyTypedCallback.accept(typedChar, keyCode);
            }
            return result;
        }
        return false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
