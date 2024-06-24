package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ECGuiStartScreen extends GuiScreen {

    // Use the vanilla book GUI texture
    private static final ResourceLocation BOOK_GUI_TEXTURE = new ResourceLocation("minecraft:textures/gui/book.png");
    private final int guiWidth = 192;
    private final int guiHeight = 192;

    @Override
    public void initGui() {
        int centerX = (width - guiWidth) / 2;
        int centerY = (height - guiHeight) / 2;

        int buttonWidth = 115;
        int buttonHeight = 20;
        int buttonX = centerX + 36; // Adjust based on the book texture margins
        int totalButtonHeight = 3 * buttonHeight + 2 * 25; // Total height of all buttons and margins
        int buttonY = centerY + guiHeight - totalButtonHeight + 10; // Subtract totalButtonHeight from the bottom of the book texture

        this.buttonList.add(new GuiButton(1, buttonX, buttonY, buttonWidth, buttonHeight, I18n.format("Load|Save Configs")));
        this.buttonList.add(new GuiButton(2, buttonX, buttonY + 25, buttonWidth, buttonHeight, I18n.format("Import|Export Configs")));
        this.buttonList.add(new GuiButton(3, buttonX, buttonY + 50, buttonWidth, buttonHeight, I18n.format("Done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            // Handle Load and Save Configs
        } else if (button.id == 2) {
            Minecraft.getMinecraft().displayGuiScreen(new ECGuiImportAndExportManager(null));
        } else if (button.id == 3) {
            // Handle Go Back
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURE);
        int centerX = (width / 2) - guiWidth / 2;
        int centerY = (height /2) - guiHeight / 2;
        drawTexturedModalRect(centerX, centerY, 0, 0, guiWidth, guiHeight);


        // Define margins based on the book's inner area
        int marginX = 36;
        int marginY = 16;
        String text = "Easy Configs";
        int textWidth = fontRendererObj.getStringWidth(text);
        int centerX2 = (width / 2) - textWidth / 2;
        int textY = centerY + marginY; // keep the original Y position

        fontRendererObj.drawSplitString(text, centerX2, textY, textWidth, 0x000000);
        int textX = centerX + marginX;
        int textY2 = centerY + marginY;
        int textWidth2 = guiWidth - 2 * marginX; // Total width minus margins
        fontRendererObj.drawSplitString("Easily manage your configurations.", textX, textY2 + 20, textWidth2, 0x000000);
        fontRendererObj.drawSplitString("You can load, save, import, and export configs effortlessly.", textX, textY2 + 40, textWidth2, 0x000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}