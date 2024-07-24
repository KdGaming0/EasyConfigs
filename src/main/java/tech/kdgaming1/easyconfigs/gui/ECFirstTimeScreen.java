package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.kdgaming1.easyconfigs.EasyConfigs;
import tech.kdgaming1.easyconfigs.config.ECConfigs;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECCheckbox;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

public class ECFirstTimeScreen extends GuiScreen {

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private final EasyConfigs mod;
    private ECCheckbox showAgainCheckbox;
    private String modpackInfo;
    private List<String> modpackInfoLines;

    public ECFirstTimeScreen(EasyConfigs mod) {
        this.mod = mod;
        this.modpackInfo = ECSetup.readModpackInfo();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int buttonWidth = 200;
        int buttonHeight = 20;
        int yOffset = 30;

        this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth / 2, this.height - yOffset, buttonWidth, buttonHeight, "Got it!"));

        this.showAgainCheckbox = new ECCheckbox(1, 0, 0, "Don't show this screen on next launch", false);

        // Format the modpack info text
        this.modpackInfoLines = new ArrayList<>();
        if (this.fontRendererObj != null) {
            this.modpackInfoLines = this.fontRendererObj.listFormattedStringToWidth(this.modpackInfo, this.width - 40);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Welcome to Easy Configs!", this.width / 2, 20, 0xFFFFFF);

        int y = 50;
        int lineHeight = this.fontRendererObj.FONT_HEIGHT + 2;

        String[] generalInfo = {
                "Easy Configs simplifies configuration management for Minecraft mods.",
                "",
                "How to use Easy Configs:",
                "1. Press ESC to open the game menu",
                "2. Click the 'Easy Configs' button",
                "3. Save, load, import, or export configurations",
                "",
                "Modpack-specific information:"
        };

        for (String line : generalInfo) {
            this.drawCenteredString(this.fontRendererObj, line, this.width / 2, y, 0xFFFFFF);
            y += lineHeight;
        }

        for (String line : modpackInfoLines) {
            this.drawString(this.fontRendererObj, line, 20, y, 0xFFFFFF);
            y += lineHeight;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        int checkboxX = this.width / 2 - 100;
        int checkboxY = this.height - 55;
        this.showAgainCheckbox.xPosition = checkboxX;
        this.showAgainCheckbox.yPosition = checkboxY;
        this.showAgainCheckbox.drawCheckbox(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            // Set showFirstTimeScreen to false if the checkbox is checked
            if (ECConfigs.advancedLogging) {LOGGER.info("Value of showAgainCheckbox is: " + !this.showAgainCheckbox.isChecked()); }
            ECConfigs.showFirstTimeScreen = !this.showAgainCheckbox.isChecked();
            if (ECConfigs.advancedLogging) {LOGGER.info("Value of showFirstTimeScreen is: " + ECConfigs.showFirstTimeScreen); }
            ECConfigs.getConfiguration().get(Configuration.CATEGORY_GENERAL, "Show First Time Screen", true).set(ECConfigs.showFirstTimeScreen);
            ECConfigs.getConfiguration().save();

            this.mc.displayGuiScreen(null);
            mod.onFirstTimeScreenClosed();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showAgainCheckbox.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            this.showAgainCheckbox.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // Prevent closing the screen with Esc key
        if (keyCode == 1) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        // Only allow closing through the button
        if (this.buttonList.isEmpty() || !((GuiButton)this.buttonList.get(0)).enabled) {
            mod.onFirstTimeScreenClosed();
        } else {
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}