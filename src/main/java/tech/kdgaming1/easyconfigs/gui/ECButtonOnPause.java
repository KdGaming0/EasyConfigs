package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

public class ECButtonOnPause {
    private static final int buttonId = (int) System.nanoTime();
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    @SubscribeEvent
    public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if ((ECConfigs.configButtonOnPause && event.gui instanceof GuiIngameMenu) ||
                (ECConfigs.configButtonOnOptions && event.gui instanceof GuiOptions)) {
            if (event.button.id == buttonId) {
                Minecraft.getMinecraft().displayGuiScreen(new ECGuiScreen());
            }
        }
    }

    @SubscribeEvent
    public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiIngameMenu && ECConfigs.configButtonOnPause) {
            addButtonToCorner(event, true);
        } else if (event.gui instanceof GuiOptions && ECConfigs.configButtonOnOptions) {
            addButtonToCorner(event, false);
        }
    }

    private void addButtonToCorner(GuiScreenEvent.InitGuiEvent.Post event, boolean isPauseMenu) {
        int screenWidth = event.gui.width;
        int screenHeight = event.gui.height;

        // Position the button in the top-right corner
        int x = screenWidth - BUTTON_WIDTH - 5;
        int y = 5;

        // Check for overlaps and adjust position if necessary
        for (GuiButton button : event.buttonList) {
            if (button.xPosition + button.width > x && button.xPosition < x + BUTTON_WIDTH &&
                    button.yPosition + button.height > y && button.yPosition < y + BUTTON_HEIGHT) {
                // If there's an overlap, move our button down
                y = button.yPosition + button.height + 2;
            }
        }

        // Add the button
        GuiButton easyConfigsButton = new GuiButton(buttonId, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, "Easy Configs");
        event.buttonList.add(easyConfigsButton);

        // If it's the pause menu, we need to move the "Open to LAN" button if it exists
        if (isPauseMenu) {
            for (GuiButton button : event.buttonList) {
                if (button.id == 7) { // 7 is the ID for the "Open to LAN" button
                    button.yPosition = y + BUTTON_HEIGHT + 2;
                    break;
                }
            }
        }
    }
}