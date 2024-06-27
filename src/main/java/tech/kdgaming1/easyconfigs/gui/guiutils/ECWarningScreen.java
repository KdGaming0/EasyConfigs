package tech.kdgaming1.easyconfigs.gui.guiutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class ECWarningScreen extends GuiScreen {
    private final String warningMessage;
    private final GuiScreen parentScreen;
    private final Runnable onConfirm;

    public ECWarningScreen(GuiScreen parentScreen, String warningMessage, Runnable onConfirm) {
        this.parentScreen = parentScreen;
        this.warningMessage = warningMessage;
        this.onConfirm = onConfirm;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;

        // Confirm button
        this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth - spacing, this.height / 2 + 30, buttonWidth, buttonHeight, "Confirm"));

        // Go Back button
        this.buttonList.add(new GuiButton(1, this.width / 2 + spacing, this.height / 2 + 30, buttonWidth, buttonHeight, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                // Confirm button pressed
                onConfirm.run();
                Minecraft.getMinecraft().displayGuiScreen(null);
            } else if (button.id == 1) {
                // Go Back button pressed
                mc.displayGuiScreen(parentScreen);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int textWidth = this.width - 40; // Subtract 40 to add a 20 pixel margin on each side
        int x = (this.width - textWidth) / 2; // Calculate the x position of the text

        // Calculate the number of lines and the total height of the text
        List<String> lines = this.fontRendererObj.listFormattedStringToWidth(warningMessage, textWidth);
        int totalTextHeight = lines.size() * this.fontRendererObj.FONT_HEIGHT;

        // Calculate the y position of the text to vertically center it
        int y = (this.height - totalTextHeight) / 2;

        this.fontRendererObj.drawSplitString(warningMessage, x, y, textWidth, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}