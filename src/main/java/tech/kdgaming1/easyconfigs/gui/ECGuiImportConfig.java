package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tech.kdgaming1.easyconfigs.chatutils.ECChatUtils;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECCheckbox;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECDropdownMenu;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECWarningScreen;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ECGuiImportConfig {
    private final FontRenderer fontRenderer;
    private final ECDropdownMenu slotSelector;
    private final ECDropdownMenu slotSelector2;
    private final GuiButton importButton;
    private final GuiButton openImportFolderButton;
    private final ECCheckbox checkbox;
    private final ECCheckbox overwriteCheckbox;
    private final ECCheckbox setAsCurretcheckbox;
    private boolean showOverwriteCheckbox = false;

    private final Path importDir;
    private long lastCheckTime;
    private static final long CHECK_INTERVAL = 5000;

    public ECGuiImportConfig(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.importDir = Paths.get(ECSetup.ECImport);
        this.lastCheckTime = System.currentTimeMillis();

        List<String> importFiles = getImportFiles();
        if (importFiles.isEmpty()) {
            importFiles.add("No files in the folder");
        }

        this.slotSelector = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, importFiles);
        this.slotSelector2 = new ECDropdownMenu(Minecraft.getMinecraft(), 0, 0, 150, 20, Arrays.asList(
                "EasyConfigSave1", "EasyConfigSave2", "EasyConfigSave3", "EasyConfigSave4", "EasyConfigSave5", "EasyConfigSave6", "EasyConfigSave7", "EasyConfigSave8", "EasyConfigSave9"));
        this.importButton = new GuiButton(1, 0, 0, "Import");
        this.openImportFolderButton = new GuiButton(5, 0, 0, "Open Import Folder");
        this.setAsCurretcheckbox = new ECCheckbox(4, 0, 0, "Set Import as current settings", false);
        this.checkbox = new ECCheckbox(2, 0, 0, "Include MCOptions", false);
        this.overwriteCheckbox = new ECCheckbox(3, 0, 0, "Overwrite Existing", false);

        // Add a listener or callback for when the selected item in the dropdown menu changes
        slotSelector2.setSelectionListener(this::onSlotSelected);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCheckTime > CHECK_INTERVAL) {
                updateImportFiles();
                lastCheckTime = currentTime;
            }
        }
    }

    private void updateImportFiles() {
        List<String> newImportFiles = getImportFiles();
        if (newImportFiles.isEmpty()) {
            newImportFiles.add("No files in the folder");
        }
        if (!newImportFiles.equals(slotSelector.getOptions())) {
            slotSelector.updateOptions(newImportFiles);
        }
    }

    private List<String> getImportFiles() {
        try {
            return Files.list(importDir)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void onSlotSelected(String selectedSlot) {
        // Check if the selected slot matches an existing save folder
        File saveDir = new File(ECSetup.ECDir, selectedSlot);
        showOverwriteCheckbox = saveDir.exists() && saveDir.isDirectory();
        overwriteCheckbox.setChecked(false); // Reset the checkbox when selection changes
    }

    public void draw(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY) {
        // Draw the "Save Config" GUI inside the box
        String text = "Import Configs";
        int textX = boxX + 10;
        int textY = boxY + 10;
        fontRenderer.drawString(text, textX, textY, 0xFFFFFF);
        // Draw a line under the title
        fontRenderer.drawString("-------------", textX, textY + 5, 0xFFFFFF);

        // Calculate the save button's width and position
        int buttonWidth = Math.min(importButton.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight - importButton.height - 10;

        // Draw the save button
        importButton.width = buttonWidth;
        importButton.xPosition = buttonX;
        importButton.yPosition = buttonY;
        importButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        // Draw the open import folder button
        openImportFolderButton.width = buttonWidth / 2 + 20; // Set the width of the button
        openImportFolderButton.xPosition = boxX + boxWidth - openImportFolderButton.width - 5; // Position the button
        openImportFolderButton.yPosition = boxY + 5; // Position the button
        openImportFolderButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        {
            // Draw the set import as current settings checkbox
            int checkboxX = boxX + 10;
            int checkboxY = boxY + 110;
            setAsCurretcheckbox.xPosition = checkboxX;
            setAsCurretcheckbox.yPosition = checkboxY;
            setAsCurretcheckbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        // Conditionally draw the overwrite checkbox
        if (showOverwriteCheckbox) {
            int overwriteCheckboxX = boxX + 10;
            int overwriteCheckboxY = boxY + 135;
            overwriteCheckbox.xPosition = overwriteCheckboxX;
            overwriteCheckbox.yPosition = overwriteCheckboxY;
            overwriteCheckbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        // Draw the include McOptions checkbox
        {
            boolean showCheckbox = setAsCurretcheckbox.isChecked();
            if (showCheckbox) {
                int checkboxX = boxX + 10;
                int checkboxY = boxY + 135;
                if (showOverwriteCheckbox) {
                    checkboxY += 25;
                }
                checkbox.xPosition = checkboxX;
                checkbox.yPosition = checkboxY;
                checkbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
            }
        }


        // Draw information text for the slot selector
        String infoText = "Select a slot to save the config to:";
        int infoTextX = boxX + 10;
        int infoTextY = boxY + 70;
        fontRenderer.drawString(infoText, infoTextX, infoTextY, 0xFFFFFF);
        // Draw the second slot selector
        int dropdownX2 = boxX + 10;
        int dropdownY2 = boxY + 85;
        slotSelector2.setX(dropdownX2);
        slotSelector2.setY(dropdownY2);
        slotSelector2.drawDropdown(mouseX, mouseY);

        // Draw information text for the slot selector
        String infoTex2 = "Select import file:";
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
                    String tooltipText = "Select a file to import the config from.";
                    drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                }
            }
                // Draw tooltip for the include McOptions checkbox
                checkBoxLocation:
                {
                    int checkboxY = boxY + 135;
                    if (!slotSelector.isExpanded() && !slotSelector2.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= checkboxY - 25 && mouseY <= checkboxY - 5) {
                        String tooltipText = "If this is check the settings you import now will be copied to you config folder when you restart your game.";
                        drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                    }

                    if (showOverwriteCheckbox) {
                        checkboxY += 25;
                    }
                    if (!slotSelector.isExpanded() && setAsCurretcheckbox.isChecked() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= checkboxY && mouseY <= checkboxY + 20) {
                        if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                            String tooltipText = "MCOptions are the settings for the vanilla game, this includes keybindings, video settings, server list, etc.";
                            drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                        }
                    }
                }
                // Draw tooltip for the overwrite checkbox
                if (showOverwriteCheckbox && !slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 135 && mouseY <= boxY + 155) {
                    if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                        String tooltipText = "You have select an existing save slot, check this box will overwrite the existing saves in that slot.";
                        drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                    }
                }
                // Draw the tooltip for the Select a slot to save the config to text
                if (!slotSelector.isExpanded() && mouseX >= boxX + 10 && mouseX <= boxX + 10 + 150 && mouseY >= boxY + 60 && mouseY <= boxY + 80) {
                    if (mouseX > Minecraft.getMinecraft().currentScreen.width / 2) {
                        String tooltipText = "Select a slot to save the config to, if the slot is already taken, you can choose to overwrite it.";
                        drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth, fontRenderer);
                    }
                }
            }

        // Update save button state based on current conditions
        updateImportButtonState();
    }

    private void updateImportButtonState() {
        boolean isSlotSelected = slotSelector.getSelectedOption() != null;
        boolean isSlotSelected2 = slotSelector2.getSelectedOption() != null;

        if (!isSlotSelected || !isSlotSelected2) {
            importButton.enabled = false;
            importButton.displayString = "Select both options";
        } else if (showOverwriteCheckbox && !overwriteCheckbox.isChecked()) {
            importButton.enabled = false;
            importButton.displayString = "Confirm Overwrite";
        } else {
            importButton.enabled = true;
            importButton.displayString = "Import";
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        slotSelector.mouseClicked(mouseX, mouseY, mouseButton);
        slotSelector2.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= importButton.xPosition && mouseX <= importButton.xPosition + importButton.width &&
                mouseY >= importButton.yPosition && mouseY <= importButton.yPosition + importButton.height) {
            if (importButton.enabled) {
                actionPerformed(importButton);
            }
        }
        if (mouseX >= openImportFolderButton.xPosition && mouseX <= openImportFolderButton.xPosition + openImportFolderButton.width &&
                mouseY >= openImportFolderButton.yPosition && mouseY <= openImportFolderButton.yPosition + openImportFolderButton.height) {
            try {
                // Open the import directory
                Desktop.getDesktop().open(new File(ECSetup.ECImport));
            } catch (IOException e) {
                // Step 5: Handle the exception
                e.printStackTrace();
            }
        }
        // Handle the checkboxes
        if (checkbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            checkbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        if (showOverwriteCheckbox && overwriteCheckbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            overwriteCheckbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        if (setAsCurretcheckbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            setAsCurretcheckbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    public void handleMouseInput() throws IOException {
        slotSelector.handleMouseInput();
        slotSelector2.handleMouseInput();
    }

    public void actionPerformed(GuiButton button) {
        if (button == importButton) {
            importButton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            String selectedSlot = slotSelector2.getSelectedOption();
            String selectedFile = slotSelector.getSelectedOption();
            boolean includeMCOptions = checkbox.isChecked();
            boolean setAsCurrent = setAsCurretcheckbox.isChecked();

            // Convert the selected slot to an integer
            int slotNumber = Integer.parseInt(selectedSlot.substring(selectedSlot.length() - 1));

            // Convert the selected file to a Path
            String selectedFileWithoutExtension;
            if (selectedFile.endsWith(".zip") && !selectedFile.equals(".zip")) {
                selectedFileWithoutExtension = selectedFile.substring(0, selectedFile.length() - 4);
            } else if (selectedFile.equals(".zip")) {
                // Handle the case where the filename is ".zip"
                // For example, show an error message and return
                ECChatUtils.printChatMessage("Invalid filename: .zip Must have something before the .zip extension.");
                return;
            } else {
                selectedFileWithoutExtension = selectedFile;
            }

            Path importPath = Paths.get(ECSetup.ECImport, selectedFileWithoutExtension);

            if (setAsCurrent) {
                // Create and display the warning screen
                ECWarningScreen warningScreen = new ECWarningScreen(
                        Minecraft.getMinecraft().currentScreen,
                        "Are you sure you want to set this as the current configuration? The game will need to be restarted to apply the changes.",
                        () -> {
                            try {
                                ECConfigFileManager.importConfigs(slotNumber, importPath, setAsCurrent, includeMCOptions);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
                Minecraft.getMinecraft().displayGuiScreen(warningScreen);
            } else {
                try {
                    ECConfigFileManager.importConfigs(slotNumber, importPath, setAsCurrent, includeMCOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public GuiButton getSaveButton() {
        return importButton;
    }

    public ECDropdownMenu getSlotSelector() {
        return slotSelector;
    }
    public ECDropdownMenu getSlotSelector2() {
        return slotSelector2;
    }

    public ECCheckbox getCheckbox() {
        return checkbox;
    }
}
