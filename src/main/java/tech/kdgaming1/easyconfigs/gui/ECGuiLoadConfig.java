package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECCheckbox;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECDropdownMenu;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECWarningScreen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ECGuiLoadConfig {
    private final FontRenderer fontRenderer;
    private final ECDropdownMenu slotSelector;
    private final GuiButton loadButton;
    private final ECCheckbox checkbox;
    private boolean saveSlotExists = true;
    private final GuiScreen parentScreen = null;

    public ECGuiLoadConfig(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.slotSelector = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, Arrays.asList(
                "Default Configs", "EasyConfigSave1", "EasyConfigSave2", "EasyConfigSave3", "EasyConfigSave4", "EasyConfigSave5", "EasyConfigSave6", "EasyConfigSave7", "EasyConfigSave8", "EasyConfigSave9"));
        this.loadButton = new GuiButton(1, 0, 0, "Load Configs");
        this.checkbox = new ECCheckbox(2, 0, 0, "Include MCOptions", true);
        slotSelector.setSelectionListener(this::onSlotSelected);
    }

    private void onSlotSelected(String selectedSlot) {
        // Check if the selected slot exists
        if (selectedSlot.equals("Default Configs")) {
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
        // Draw the "Load Config" GUI inside the box
        String text = "Load Configs";
        int textX = boxX + (boxWidth - fontRenderer.getStringWidth(text)) / 2;
        int textY = boxY + 10;
        fontRenderer.drawString(text, textX, textY, 0xFFFFFF);
        fontRenderer.drawString("-----------", textX, textY + 5, 0xFFFFFF);

        // Calculate the Load button's width and position
        int buttonWidth = Math.min(loadButton.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight - loadButton.height - 10;

        // Draw the Load button
        loadButton.width = buttonWidth;
        loadButton.xPosition = buttonX;
        loadButton.yPosition = buttonY;
        loadButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        {
            // Draw information text for the slot selector
            String infoText = "Select a slot to load configs from:";
            int infoTextX = boxX + 10;
            int infoTextY = textY + 20;
            fontRenderer.drawString(infoText, infoTextX, infoTextY, 0xFFFFFF);

            // Draw information text if the save slot don't exist
            if (!saveSlotExists) {
                int textWidth = boxWidth - 20;
                int x = boxX + 10;
                int y = boxY + 70;
                int color = 0xFFFF0000;
                String infoTextError = "The selected slot does not exist and can not be copied from. Please select a different slot that have configs saved to it. Or go to the Save Configs screen to save configs to the selected slot.";
                fontRenderer.drawSplitString(infoTextError, x, y, textWidth, color);
            }
            if (saveSlotExists) {
                // Draw the include McOptions checkbox
                {
                    int checkboxX = boxX + 10;
                    int checkboxY = boxY + 70;
                    checkbox.xPosition = checkboxX;
                    checkbox.yPosition = checkboxY;
                    checkbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
                }
                int textWidth = boxWidth - 20;
                int x = boxX + 10;
                int y = boxY + 95;
                int color = 0xFF00FF00;
                String infoTextInfo = "When you click the Load Configs button the game will copy the configs from the selected slot and apply them to the game. The game will need to be restarted to apply the changes.";
                fontRenderer.drawSplitString(infoTextInfo, x, y, textWidth, color);
                color = 0xFFFF3333;
                String infoTextWarning = "YOU MUST RESTART THE GAME TO APPLY THE CONFIGS!";
                List<String> lines = fontRenderer.listFormattedStringToWidth(infoTextWarning, textWidth);
                int textHeight = lines.size() * 9; // Calculate the height of the text
                int warningTextY = boxY + boxHeight - loadButton.height - textHeight - 15;
                for (String line : lines) {
                    int warningTextX = boxX + (boxWidth - fontRenderer.getStringWidth(line)) / 2;
                    fontRenderer.drawString(line, warningTextX, warningTextY, color);
                    warningTextY += fontRenderer.FONT_HEIGHT;
                }
            }

            // Draw the slot selector
            int dropdownX = boxX + 10;
            int dropdownY = infoTextY + 15;
            slotSelector.setX(dropdownX);
            slotSelector.setY(dropdownY);
            slotSelector.drawDropdown(mouseX, mouseY);
        }

        // Draw the tooltip for the Select a slot to save the config to text
        if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 25 && mouseY <= boxY + 40) {
            if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                String tooltipText = "Select a slot to load the config from, Defult Configs is the configs shipped with the mod pack if you are using a mod pack.";
                drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
            }
        }
        // Draw tooltip for the include McOptions checkbox
        if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 65 && mouseY <= boxY + 90) {
            if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                String tooltipText = "MCOptions are the settings for the vanilla game, this includes keybindings, video settings, server list, etc.";
                drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth , fontRenderer);
            }
        }
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        boolean isOptionSelected = slotSelector.getSelectedOption() != null;
        if (!isOptionSelected) {
            loadButton.enabled = false;
            loadButton.displayString = "Select an option";
        } else if (!saveSlotExists) {
            loadButton.enabled = false;
            loadButton.displayString = "Slot does not exist";
        } else {
            loadButton.enabled = true;
            loadButton.displayString = "Load Configs";
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        slotSelector.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= loadButton.xPosition && mouseX <= loadButton.xPosition + loadButton.width &&
                mouseY >= loadButton.yPosition && mouseY <= loadButton.yPosition + loadButton.height) {
            if (loadButton.enabled) {
                actionPerformed(loadButton);
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

    public void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            loadButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            String selectedSlot = slotSelector.getSelectedOption();
            if (selectedSlot.equals("Default Configs")) {
                selectedSlot = "EasyConfigSave0";
            }
            final String finalSelectedSlot = selectedSlot;
            boolean mcOptions = checkbox.isChecked();

            // Create and display the warning screen
            ECWarningScreen warningScreen = new ECWarningScreen(
                    parentScreen,
                    "Are you sure you want to load configs from " + finalSelectedSlot + "? " +
                            "The game will need to be restarted to apply the changes.",
                    () -> {
                        try {
                            ECConfigFileManager.loadConfigs(Integer.parseInt(finalSelectedSlot.substring(finalSelectedSlot.length() - 1)), mcOptions);
                            Minecraft.getMinecraft().displayGuiScreen(parentScreen);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            Minecraft.getMinecraft().displayGuiScreen(warningScreen);
        }
    }

    public GuiButton getSaveButton() {
        return loadButton;
    }

    public ECDropdownMenu getSlotSelector() {
        return slotSelector;
    }

    public ECCheckbox getCheckbox() {
        return checkbox;
    }



}
