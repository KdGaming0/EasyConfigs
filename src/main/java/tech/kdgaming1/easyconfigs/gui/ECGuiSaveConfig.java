package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECDropdownMenu;

import java.io.IOException;
import java.util.Arrays;

public class ECGuiSaveConfig {
    private FontRenderer fontRenderer;
    private ECDropdownMenu slotSelector;
    private GuiButton saveButton;

    public ECGuiSaveConfig(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.slotSelector = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, Arrays.asList("Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5", "Slot 6"));
        this.saveButton = new GuiButton(1, 0, 0, "Save");
    }

    public void draw(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY) {
        // Draw the "Save Config" GUI inside the box
        String text = "Save Configs";
        int textX = boxX + (boxWidth - fontRenderer.getStringWidth(text)) / 2;
        int textY = boxY + 10;
        fontRenderer.drawString(text, textX, textY, 0xFFFFFF);

        // Draw the slot selector and save button
        int dropdownX = boxX + 10;
        int dropdownY = textY + 20;
        slotSelector.setX(dropdownX);
        slotSelector.setY(dropdownY);
        slotSelector.drawDropdown(mouseX, mouseY);

        // Calculate the button's width and position
        int buttonWidth = Math.min(saveButton.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = dropdownY + 30;

        saveButton.width = buttonWidth;
        saveButton.xPosition = buttonX;
        saveButton.yPosition = buttonY;
        saveButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        slotSelector.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= saveButton.xPosition && mouseX <= saveButton.xPosition + saveButton.width &&
                mouseY >= saveButton.yPosition && mouseY <= saveButton.yPosition + saveButton.height) {
            actionPerformed(saveButton);
        }
    }

    public void handleMouseInput() throws IOException {
        slotSelector.handleMouseInput();
    }

    public void actionPerformed(GuiButton button) {
        if (button == saveButton) {
            // Handle save action
        }
    }

    public GuiButton getSaveButton() {
        return saveButton;
    }

    public ECDropdownMenu getSlotSelector() {
        return slotSelector;
    }
}
