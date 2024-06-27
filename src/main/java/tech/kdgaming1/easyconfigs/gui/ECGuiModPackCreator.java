package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import tech.kdgaming1.easyconfigs.easyconfighandler.ECConfigFileManager;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECWarningScreen;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText;

public class ECGuiModPackCreator {
    private final FontRenderer fontRenderer;
    private final GuiButton modPackButton2;
    private final ECGuiScreen guiScreen = new ECGuiScreen();
    private int urlX, urlY, urlWidth, urlHeight;
    private int buttonTextX, buttonTextY, buttonTextWidth, buttonTextHeight;

    public ECGuiModPackCreator(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.modPackButton2 = new GuiButton(5, 0, 0, "Create Default Configs");
    }

    public void drawHorizontalLine(int startX, int endX, int y, int color) {
        this.guiScreen.drawLine(startX, endX, y, color);
    }

    public void draw(int boxX, int boxY, int boxWidth, int boxHeight, int mouseX, int mouseY) {
        // Draw the "Mod Pack Creator" title
        String titleText = "Mod Pack Creator";
        int titleTextX = boxX + (boxWidth - fontRenderer.getStringWidth(titleText)) / 2;
        int titleTextY = boxY + 10;
        fontRenderer.drawString(titleText, titleTextX, titleTextY, 0xFFFFFF);
        fontRenderer.drawString("---------------", titleTextX, titleTextY + 5, 0xFFFFFF);

        int textWidth = boxWidth - 20;
        int x = boxX + 10;
        int y = boxY + 25;
        int color = 0xFF00FF00;
        String infoText = "Are you a mod pack creator? Click the URL to get instructions on how to make your mod pack ready ship! Go to the Mod Pack Creator drop down on the site.";
        fontRenderer.drawSplitString(infoText, x, y, textWidth, color);
        List<String> lines = fontRenderer.listFormattedStringToWidth(infoText, textWidth);
        int infoHeight = lines.size() * 9; // Calculate the height of the text
        int urlYPosition = boxY + infoHeight + 35;
        String urlText = "https://modrinth.com/mod/easyconfigs";
        urlX = x;
        urlY = urlYPosition - 5;
        urlWidth = fontRenderer.getStringWidth(urlText);
        urlHeight = fontRenderer.FONT_HEIGHT;
        fontRenderer.drawString(urlText, urlX, urlY, 0xFF0000FF); // Change URL color to blue

        // Move the mod pack button2 to the position of the old modPackButton
        int buttonWidth = Math.min(modPackButton2.getButtonWidth(), boxWidth - 20);
        int buttonX = boxX + (boxWidth - buttonWidth) / 2;
        int buttonY = boxY + boxHeight - modPackButton2.height - 10;

        // Draw the mod pack button2
        modPackButton2.width = buttonWidth;
        modPackButton2.xPosition = buttonX;
        modPackButton2.yPosition = buttonY;
        modPackButton2.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);

        // Draw text over the "Create The Default Configs" button
        String buttonText = "Do you want to create default configs for your mod pack? Click here!";
        List<String> buttonTextLines = fontRenderer.listFormattedStringToWidth(buttonText, buttonWidth);
        int buttonTextHeight = buttonTextLines.size() * 9;
        buttonTextY = modPackButton2.yPosition + (modPackButton2.height - buttonTextHeight) / 2 - 25;

        for (String line : buttonTextLines) {
            buttonTextX = modPackButton2.xPosition + (modPackButton2.width - fontRenderer.getStringWidth(line)) / 2;
            fontRenderer.drawString(line, buttonTextX, buttonTextY, 0xFFFFFF);
            buttonTextY += fontRenderer.FONT_HEIGHT;
        }

        // Save dimensions for tooltip detection
        buttonTextY = modPackButton2.yPosition + (modPackButton2.height - buttonTextHeight) / 2;
        buttonTextWidth = buttonWidth; // Set buttonTextWidth to buttonWidth
        buttonTextHeight = fontRenderer.FONT_HEIGHT * buttonTextLines.size();

        // Render tooltips if mouse is over text
        if (mouseX >= urlX && mouseX <= urlX + urlWidth && mouseY >= urlY && mouseY <= urlY + urlHeight) {
            String tooltipText = "Click to open the Modrinth page.";
            drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth , fontRenderer);
        } else if (mouseX >= buttonTextX - buttonTextWidth / 2 && mouseX <= buttonTextX + buttonTextWidth / 2 && mouseY >= buttonTextY - buttonTextHeight && mouseY <= buttonTextY) {
            String tooltipText = "This will copy all the files from the config folder to the default config slot. Only do this when you want to update your default configs.";
            drawHoveringText(Arrays.asList(tooltipText), mouseX, mouseY, boxWidth - 20, boxWidth - 40, boxWidth , fontRenderer);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Check if the user clicked on the URL
        if (mouseX >= urlX && mouseX <= urlX + urlWidth && mouseY >= urlY && mouseY <= urlY + urlHeight) {
            try {
                // Open the URL in the user's default web browser
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("https://modrinth.com/mod/easyconfigs"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mouseX >= modPackButton2.xPosition && mouseX <= modPackButton2.xPosition + modPackButton2.width &&
                mouseY >= modPackButton2.yPosition && mouseY <= modPackButton2.yPosition + modPackButton2.height) {
            actionPerformed(modPackButton2);
        }
    }

    public void actionPerformed(GuiButton button) {
        if (button == modPackButton2) { // Check if the "Create Default Configs" button was clicked
            modPackButton2.playPressSound(Minecraft.getMinecraft().getSoundHandler());
            // Create and display the warning screen
            ECWarningScreen warningScreen = new ECWarningScreen(
                    Minecraft.getMinecraft().currentScreen,
                    "Are you sure you want to create default configs? This will overwrite any existing configs in slot 0. " +
                            "When you have click confirm are you ready to ship your mod pack, all you have to do is to ship the EasyConfigs folder with your mod pack and not ship any of the configs files located in the config folder." +
                            " Also the Options.txt server.dat should not be ship with your mod pack.",
                    () -> {
                        try {
                            ECConfigFileManager.saveConfigs(0, true, true); // Call the saveConfigs method
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            Minecraft.getMinecraft().displayGuiScreen(warningScreen);
        }
    }
}
