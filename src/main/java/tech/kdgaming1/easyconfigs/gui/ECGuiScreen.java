package tech.kdgaming1.easyconfigs.gui;

import com.google.common.base.Objects;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.List;

public class ECGuiScreen extends GuiScreen {
    private ECGuiSaveConfig saveConfigGui;
    private boolean showSaveConfig;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw the screen
        drawDefaultBackground();
        {
            // Draw the headline and information text
            String ECHeadline = "Easy Configs";
            drawString(fontRendererObj, ECHeadline, 10, 10, 0xFFFFFF);
            int lineWidth = fontRendererObj.getStringWidth(ECHeadline);
            drawHorizontalLine(8, 12 + lineWidth, 20, 0xFFFFFFFF);
            int thanksY;
            {
                int textWidth = this.width / 2 - 20; // Set the width to half of the screen width
                int x = 10; // The x-coordinate where the text starts
                int y = 30; // The y-coordinate where the text starts
                int color = 0xFFFFFF; // The color of the text
                String infoText = "With Easy Configs, you can effortlessly save and load different configurations. You can also export and import them to easily share your setups with friends.";
                fontRendererObj.drawSplitString(infoText, x, y, textWidth, color);
                int numLines = fontRendererObj.listFormattedStringToWidth(infoText, textWidth).size();
                int textHeight = numLines * 9; // Calculate the height of the text
                thanksY = y + textHeight + 10;
            }
            drawString(fontRendererObj, "Thanks for using Easy Configs!", 10, thanksY, 0xFFFFFF);
        }
        {
            // Draw the info box
            int boxWidth = width / 2 - 20; // Set the width of the box to the screen width / 2 minus 20 pixels
            int boxHeight = height - 50; // Set the height of the box to the screen height minus 50 pixels
            int boxX = width / 2 + 10; // Set the x-coordinate of the box to the screen width / 2 plus 10 pixels
            int boxY = 30; // Set the y-coordinate of the box to 30 pixels from the top
            drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x80000000); // Draw a semi-transparent black box
            // Draw a frame around the box
            drawHorizontalLine(boxX, boxX + boxWidth, boxY, 0xFFFFFFFF);
            drawHorizontalLine(boxX, boxX + boxWidth, boxY + boxHeight, 0xFFFFFFFF);
            drawVerticalLine(boxX, boxY, boxY + boxHeight, 0xFFFFFFFF);
            drawVerticalLine(boxX + boxWidth, boxY, boxY + boxHeight, 0xFFFFFFFF);
            // Draw placeholder text until one of the buttons is clicked
            if (!showSaveConfig) {
                String infoBoxText = "Click one of the buttons to get started!";
                int color = 0xFFFFFF;
                List<String> lines = fontRendererObj.listFormattedStringToWidth(infoBoxText, boxWidth);
                int totalTextHeight = lines.size() * 9;
                int textY = boxY + (boxHeight - totalTextHeight) / 2;
                for (String line : lines) {
                    int textX = boxX + (boxWidth - fontRendererObj.getStringWidth(line)) / 2;
                    fontRendererObj.drawString(line, textX, textY, color);
                    textY += 9;
                }
            }
        }
        {
            // Draw the save config GUI
            if (showSaveConfig) {
                int boxX = this.width / 4;
                int boxY = this.height / 4;
                int boxWidth = this.width / 2;
                int boxHeight = this.height / 2;
                saveConfigGui.draw(boxX, boxY, boxWidth, boxHeight, mouseX, mouseY);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        // Initialize the GUI
        super.initGui();
        saveConfigGui = new ECGuiSaveConfig(fontRendererObj);
        {
            // Draw the buttons
            int buttonWidth = 150; // The width of the buttons
            int buttonHeight = 20; // The height of the buttons
            int buttonX = 10; // The x-coordinate of the buttons, 10 pixels from the left edge
            int buttonY = height - buttonHeight - 10; // The y-coordinate of the buttons, 10 pixels from the bottom edge
            this.buttonList.add(new GuiButton(0, buttonX, buttonY, buttonWidth, buttonHeight, "Done"));
            this.buttonList.add(new GuiButton(1, buttonX, buttonY - buttonHeight - 5, buttonWidth, buttonHeight, "Save Configs"));
            this.buttonList.add(new GuiButton(2, buttonX, buttonY - 2 * buttonHeight - 10, buttonWidth, buttonHeight, "Load Configs"));
            this.buttonList.add(new GuiButton(3, buttonX, buttonY - 3 * buttonHeight - 15, buttonWidth, buttonHeight, "Export Configs"));
            this.buttonList.add(new GuiButton(4, buttonX, buttonY - 4 * buttonHeight - 20, buttonWidth, buttonHeight, "Import Configs"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // Handle button clicks
        super.actionPerformed(button);
        if (button.id == 1) {
            showSaveConfig = !showSaveConfig;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (showSaveConfig) {
            saveConfigGui.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (showSaveConfig) {
            saveConfigGui.handleMouseInput();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
