package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECCheckbox;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECDropdownMenu;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ECGuiSaveConfig {
    private final FontRenderer fontRenderer;
    private final ECDropdownMenu slotSelector;
    private final GuiButton saveButton;
    private final ECCheckbox checkbox;
    private final ECCheckbox overwriteCheckbox; // Second checkbox
    private boolean showOverwriteCheckbox = false;

    public ECGuiSaveConfig(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.slotSelector = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, Arrays.asList(
                "EasyConfigSave1", "EasyConfigSave2", "EasyConfigSave3", "EasyConfigSave4", "EasyConfigSave5", "EasyConfigSave6", "EasyConfigSave7", "EasyConfigSave8", "EasyConfigSave9"));
        this.saveButton = new GuiButton(1, 0, 0, "Save");
        this.checkbox = new ECCheckbox(2, 0, 0, "Include MCOptions", false);
        this.overwriteCheckbox = new ECCheckbox(3, 0, 0, "Overwrite Existing", false);

        // Add a listener or callback for when the selected item in the dropdown menu changes
        slotSelector.setSelectionListener(this::onSlotSelected);
    }

    private void onSlotSelected(String selectedSlot) {
        // Check if the selected slot matches an existing save folder
        File saveDir = new File(ECSetup.ECDir, selectedSlot);
        showOverwriteCheckbox = saveDir.exists() && saveDir.isDirectory();
        overwriteCheckbox.setChecked(false); // Reset the checkbox when selection changes
    }

    public void draw(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY) {
        // Draw the "Save Config" GUI inside the box
        String text = "Save Configs";
        int textX = boxX + (boxWidth - fontRenderer.getStringWidth(text)) / 2;
        int textY = boxY + 10;
        fontRenderer.drawString(text, textX, textY, 0xFFFFFF);
        fontRenderer.drawString("-----------", textX, textY + 5, 0xFFFFFF);

        // Calculate the save button's width and position
        int buttonWidth = Math.min(saveButton.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight - saveButton.height - 10;

        // Draw the save button
        saveButton.width = buttonWidth;
        saveButton.xPosition = buttonX;
        saveButton.yPosition = buttonY;
        saveButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        int color = 0xFF0000; // Red color
        String warningText = "Warning: On lower-end machines, this may take some time and can make the game unresponsive for a few seconds.";
        List<String> lines = fontRenderer.listFormattedStringToWidth(warningText, boxWidth - 20);
        int textHeight = lines.size() * fontRenderer.FONT_HEIGHT;
        int warningTextY = buttonY - 10 - textHeight;
        for (String line : lines) {
            int warningTextX = boxX + (boxWidth - fontRenderer.getStringWidth(line)) / 2;
            fontRenderer.drawString(line, warningTextX, warningTextY, color);
            warningTextY += fontRenderer.FONT_HEIGHT;
        }


        // Draw the include McOptions checkbox
        {
            int checkboxX = boxX + 10;
            int checkboxY = boxY + 70;
            checkbox.xPosition = checkboxX;
            checkbox.yPosition = checkboxY;
            checkbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        // Conditionally draw the overwrite checkbox
        if (showOverwriteCheckbox) {
            int overwriteCheckboxX = boxX + 10;
            int overwriteCheckboxY = boxY + 100;
            overwriteCheckbox.xPosition = overwriteCheckboxX;
            overwriteCheckbox.yPosition = overwriteCheckboxY;
            overwriteCheckbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        // Draw information text for the slot selector
        String infoText = "Select a slot to save the config to:";
        int infoTextX = boxX + 10;
        int infoTextY = textY + 20;
        fontRenderer.drawString(infoText, infoTextX, infoTextY, 0xFFFFFF);

        // Draw the slot selector
        int dropdownX = boxX + 10;
        int dropdownY = infoTextY + 15;
        slotSelector.setX(dropdownX);
        slotSelector.setY(dropdownY);
        slotSelector.drawDropdown(mouseX, mouseY);

        {
            // Draw tooltip for the include McOptions checkbox
            if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 65 && mouseY <= boxY + 90) {
                if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                    String tooltipText = "MCOptions are the settings for the vanilla game, this includes keybindings, video settings, server list, etc.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth , fontRenderer);
                }
            }
            // Draw tooltip for the overwrite checkbox
            if (showOverwriteCheckbox && !slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 95 && mouseY <= boxY + 120) {
                if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                    String tooltipText = "You have select an existing save slot, check this box will overwrite the existing saves in that slot.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                }
            }
            // Draw the tooltip for the Select a slot to save the config to text
            if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 25 && mouseY <= boxY + 40) {
                if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                    String tooltipText = "Select a slot to save the config to, if the slot is already taken, you can choose to overwrite it.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                }
            }
        }

        // Update save button state based on current conditions
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        boolean isOptionSelected = slotSelector.getSelectedOption() != null;

        if (!isOptionSelected) {
            saveButton.enabled = false;
            saveButton.displayString = "Select an option";
        } else if (showOverwriteCheckbox && !overwriteCheckbox.isChecked()) {
            saveButton.enabled = false;
            saveButton.displayString = "Confirm Overwrite";
        } else {
            saveButton.enabled = true;
            saveButton.displayString = "Save";
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        slotSelector.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= saveButton.xPosition && mouseX <= saveButton.xPosition + saveButton.width &&
                mouseY >= saveButton.yPosition && mouseY <= saveButton.yPosition + saveButton.height) {
            if (saveButton.enabled) {
                actionPerformed(saveButton);
            }
        }
        // Handle the checkboxes
        if (checkbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            checkbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        if (showOverwriteCheckbox && overwriteCheckbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            overwriteCheckbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    public void handleMouseInput() throws IOException {
        slotSelector.handleMouseInput();
    }

    public void actionPerformed(GuiButton button) {
        if (button == saveButton) {
            saveButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            // Extract the slot number from the selected option
            String selectedOption = slotSelector.getSelectedOption();
            int slotNumber = Integer.parseInt(selectedOption.substring(selectedOption.length() - 1));

            boolean overwrite = overwriteCheckbox.isChecked();
            boolean mcOptions = checkbox.isChecked();

            try {
                ECConfigFileManager.saveConfigs(slotNumber, overwrite, mcOptions);
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }
    }
    public ECDropdownMenu getSlotSelector() {
        return slotSelector;
    }
}
