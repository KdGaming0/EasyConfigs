package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import tech.kdgaming1.easyconfigs.chatutils.ECChatUtils;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECCheckbox;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECDropdownMenu;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECTextField;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ECGuiExportConfigs {
    private final FontRenderer fontRenderer;
    private final ECDropdownMenu slotSelector;
    private final GuiButton exportButton;
    private final GuiButton openExportFolderButton;
    private final ECTextField textField;
    private final ECCheckbox checkbox;
    private boolean saveSlotExists = true;
    private boolean nameExists = false;

    public ECGuiExportConfigs(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.slotSelector = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, Arrays.asList(
                "Config Folder","EasyConfigSave1", "EasyConfigSave2", "EasyConfigSave3", "EasyConfigSave4", "EasyConfigSave5", "EasyConfigSave6", "EasyConfigSave7", "EasyConfigSave8", "EasyConfigSave9"));
        this.exportButton = new GuiButton(1, 0, 0, "Export");
        this.openExportFolderButton = new GuiButton(5, 0, 0, "Open Export Folder");
        this.checkbox = new ECCheckbox(2, 0, 0, "Include MCOptions", false);
        this.textField = new ECTextField(3, fontRenderer, 0, 0, 150, 20);

        // Add a key listener to the text field
        textField.setKeyTypedCallback((typedChar, keyCode) -> {
            // Check if file already exists
            File file = new File(ECSetup.ECExport, textField.getText() + ".zip");
            if (file.exists()) {
                // Change the color of the text field to red
                textField.setTextColor(0xFF0000);
                // Display a message to the user
                nameExists = true;
            } else {
                // Change the color of the text field back to white
                textField.setTextColor(0xFFFFFF);
                nameExists = false;
            }
        });
        // Add a listener or callback for when the selected item in the dropdown menu changes
        slotSelector.setSelectionListener(this::onSlotSelected);
    }

    private void onSlotSelected(String selectedSlot) {
        // Check if the selected slot exists
        if (selectedSlot.equals("Config Folder")) {
            selectedSlot = "EasyConfigSave0";
        }
        File saveDir = new File(ECSetup.ECDir, selectedSlot);
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            // If the selected slot does not exist, render text to inform the user
            saveSlotExists = false;
        } else {
            saveSlotExists = true;
        }
    }

    public void draw(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY) {
        // Draw the "Save Config" GUI inside the box
        String text = "Export Configs";
        int textX = boxX + 10;
        int textY = boxY + 10;
        fontRenderer.drawString(text, textX, textY, 0xFFFFFF);
        fontRenderer.drawString("-------------", textX, textY + 5, 0xFFFFFF);

        // Calculate the save button's width and position
        int buttonWidth = Math.min(exportButton.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight - exportButton.height - 10;

        // Draw the save button
        exportButton.width = buttonWidth;
        exportButton.xPosition = buttonX;
        exportButton.yPosition = buttonY;
        exportButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

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

        openExportFolderButton.width = buttonWidth / 2 + 20; // Set the width of the button
        openExportFolderButton.xPosition = boxX + boxWidth - openExportFolderButton.width - 5; // Position the button
        openExportFolderButton.yPosition = boxY + 5; // Position the button
        openExportFolderButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        // Draw information text if the save slot don't exist
        if (!saveSlotExists) {
            int textWidth = boxWidth - 20;
            int x = boxX + 10;
            int y = boxY + 70;
            int color2 = 0xFFFF0000;
            String infoTextError = "The selected slot does not exist and can not be exported from. Please select a different slot that have configs saved to it. Or go to the Save Configs screen to save configs to the selected slot.";
            fontRenderer.drawSplitString(infoTextError, x, y, textWidth, color2);
        }

        if (saveSlotExists) {
            // Draw the include McOptions checkbox
            int checkboxX = boxX + 10;
            int checkboxY = boxY + 70;
            checkbox.xPosition = checkboxX;
            checkbox.yPosition = checkboxY;
            checkbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
            // Draw text field information
            String textFieldInfo = "Enter a name for the export file:";
            int textFieldInfoX = boxX + 10;
            int textFieldInfoY = checkboxY + 25;
            fontRenderer.drawString(textFieldInfo, textFieldInfoX, textFieldInfoY, 0xFFFFFF);
            // Draw the text field
            int textFieldX = boxX + 10;
            int textFieldY = checkboxY + 45;
            textField.xPosition = textFieldX;
            textField.yPosition = textFieldY;
            textField.drawTextBox();
        }
        if (nameExists) {
            int textWidth = boxWidth - 20;
            int x = boxX + 10;
            int y = boxY + 145;
            int color3 = 0xFFFF0000;
            String infoTextError = "The file name already exists. Please choose a different name.";
            fontRenderer.drawSplitString(infoTextError, x, y, textWidth, color3);
        }


        // Draw information text for the slot selector
        String infoTex2 = "Select save slot to export:";
        int infoTextX2 = boxX + 10;
        int infoTextY2 = textY + 20;
        fontRenderer.drawString(infoTex2, infoTextX2, infoTextY2, 0xFFFFFF);

        // Draw the slot selector
        int dropdownX = boxX + 10;
        int dropdownY = infoTextY2 + 15;
        slotSelector.setX(dropdownX);
        slotSelector.setY(dropdownY);
        slotSelector.drawDropdown(mouseX, mouseY);

        {
            // Draw tooltip for the choose import file dropdown
            if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 25 && mouseY <= boxY + 40) {
                if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                    String tooltipText = "Select a save slot to export from.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                }
            }
            // Draw tooltip for the include McOptions checkbox
            checkBoxLocation:
            {
                if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 70 && mouseY <= boxY + 90) {
                    String tooltipText = "If this is check the settings you export will include the vanilla game settings, this includes keybindings, video settings, server list, etc.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                }

            }
        }

        // Update save button state based on current conditions
        updateImportButtonState();
    }

    private void updateImportButtonState() {
        boolean isSlotSelected = slotSelector.getSelectedOption() != null;

        if (!isSlotSelected) {
            exportButton.enabled = false;
            exportButton.displayString = "Select a slot";
        } else if (!saveSlotExists) {
            exportButton.enabled = false;
            exportButton.displayString = "Slot does not exist";
        } else if (textField.getText().isEmpty()) {
            exportButton.enabled = false;
            exportButton.displayString = "Enter a name";
        } else if (nameExists) {
            exportButton.enabled = false;
            exportButton.displayString = "Name exists";
        } else {
            exportButton.enabled = true;
            exportButton.displayString = "Export";
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        slotSelector.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= exportButton.xPosition && mouseX <= exportButton.xPosition + exportButton.width &&
                mouseY >= exportButton.yPosition && mouseY <= exportButton.yPosition + exportButton.height) {
            if (exportButton.enabled) {
                actionPerformed(exportButton);
            }
        }
        if (mouseX >= openExportFolderButton.xPosition && mouseX <= openExportFolderButton.xPosition + openExportFolderButton.width &&
                mouseY >= openExportFolderButton.yPosition && mouseY <= openExportFolderButton.yPosition + openExportFolderButton.height) {
            try {
                // Open the import directory
                Desktop.getDesktop().open(new File(ECSetup.ECExport));
            } catch (IOException e) {
                // Step 5: Handle the exception
                e.printStackTrace();
            }
        }
        // Handle the checkboxes
        if (checkbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            checkbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    public void handleMouseInput() throws IOException {
        slotSelector.handleMouseInput();
    }
    public void keyTyped(char typedChar, int keyCode) {
        textField.textboxKeyTyped(typedChar, keyCode);
    }

    public void actionPerformed(GuiButton button) {
        if (button == exportButton) {
            exportButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            // Get the file path to the selected slot
            String selectedSlot = slotSelector.getSelectedOption();
            if (selectedSlot.equals("Config Folder")) {
                selectedSlot = "config";
            }
            ECChatUtils.printChatMessage("Exporting configs from slot " + selectedSlot + "...");
            String fileName = textField.getText();
            boolean mcOptions = checkbox.isChecked();
            ECChatUtils.printChatMessage("McOptions: " + mcOptions);

            try {
                ECConfigFileManager.exportConfigs(selectedSlot, fileName, mcOptions);
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
