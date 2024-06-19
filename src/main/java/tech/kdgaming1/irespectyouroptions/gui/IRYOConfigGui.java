package tech.kdgaming1.irespectyouroptions.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class IRYOConfigGui extends GuiScreen {
    private GuiScreen parentScreen;

    public IRYOConfigGui(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 50, I18n.format("button.mymod.option1")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2, I18n.format("button.mymod.option2")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 50, I18n.format("button.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                // Handle option 1
                break;
            case 1:
                // Handle option 2
                break;
            case 2:
                this.mc.displayGuiScreen(this.parentScreen);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "My Mod Configuration", this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
