package tech.kdgaming1.easyconfigs.gui.guiutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ECCheckbox extends GuiButton {
    private static final ResourceLocation CHECKBOX_TEXTURE = new ResourceLocation("easyconfigs:textures/gui/checkbox.png");
    private boolean checked;

    public ECCheckbox(int buttonId, int x, int y, String buttonText, boolean checked) {
        super(buttonId, x, y, 20, 20, buttonText);
        this.checked = checked;
    }

    public void drawCheckbox(Minecraft mc, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(CHECKBOX_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        int textureX = 0;
        int textureY = 0;

        if (checked) {
            textureY = 20; // Checked state
        }

        if (hovered) {
            textureX = 20; // Highlighted state
        }

        this.drawTexturedModalRect(this.xPosition, this.yPosition, textureX, textureY, 20, 20);
        this.mouseDragged(mc, mouseX, mouseY);

        if (!this.displayString.isEmpty()) {
            int color = 0xFFFFFF;
            if (!this.enabled) {
                color = 0xA0A0A0;
            } else if (this.hovered) {
                color = 0xFFFFA0;
            }
            this.drawString(mc.fontRendererObj, this.displayString, this.xPosition + 24, this.yPosition + 6, color);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.checked = !this.checked; // Toggle the checked state
            return true;
        }
        return false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
