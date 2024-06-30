package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.kdgaming1.easyconfigs.config.ECConfigs;

import java.util.List;
import java.util.stream.Collectors;

public class ECButtonOnPause {
    private static final int BUTTON_ID = (int) System.nanoTime();
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    @SubscribeEvent
    public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if ((ECConfigs.configButtonOnPause && event.gui instanceof GuiIngameMenu) ||
                (ECConfigs.configButtonOnOptions && event.gui instanceof GuiOptions)) {
            if (event.button.id == BUTTON_ID) {
                Minecraft.getMinecraft().displayGuiScreen(new ECGuiScreen());
            }
        }
    }

    @SubscribeEvent
    public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiIngameMenu && ECConfigs.configButtonOnPause) {
            addButtonToCorner(event);
        } else if (event.gui instanceof GuiOptions && ECConfigs.configButtonOnOptions) {
            addButtonToCorner(event);
        }
    }

    private void addButtonToCorner(GuiScreenEvent.InitGuiEvent.Post event) {
        int screenWidth = event.gui.width;
        int screenHeight = event.gui.height;

        // Position the button in the bottom-right corner
        int x = screenWidth - BUTTON_WIDTH - 5;
        int x2 = x + BUTTON_WIDTH;
        int y = screenHeight - BUTTON_HEIGHT - 2;
        int y2 = y + BUTTON_HEIGHT;

        List<GuiButton> sortedButtonList = event.buttonList.stream()
                .sorted((a, b) -> (b.yPosition + b.height) - (a.yPosition + a.height))
                .collect(Collectors.toList());

        for (GuiButton button : sortedButtonList) {
            int otherX = button.xPosition;
            int otherX2 = button.xPosition + button.width;
            int otherY = button.yPosition;
            int otherY2 = button.yPosition + button.height;

            if (otherX2 > x && otherX < x2 && otherY2 > y && otherY < y2) {
                y = otherY - BUTTON_HEIGHT - 2;
                y2 = y + BUTTON_HEIGHT;
            }
        }

        GuiButton easyConfigsButton = new GuiButton(BUTTON_ID, x, Math.max(0, y), BUTTON_WIDTH, BUTTON_HEIGHT, "Easy Configs");
        event.buttonList.add(easyConfigsButton);
    }
}
