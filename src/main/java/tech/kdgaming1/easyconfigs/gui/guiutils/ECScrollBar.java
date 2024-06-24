package tech.kdgaming1.easyconfigs.gui.guiutils;

import static net.minecraft.client.gui.Gui.drawRect;

public class ECScrollBar {
    private int x, y, width, height;
    private float scrollPos;
    private float targetScrollPos;
    private boolean isDragging;
    private long lastTime; // Time of the last frame

    public ECScrollBar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lastTime = System.nanoTime();
    }

    public void draw() {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f; // Time elapsed since the last frame, in seconds
        lastTime = currentTime;

        // Gradually move scrollPos towards targetScrollPos with an ease-out effect and frame rate independence
        float distance = targetScrollPos - scrollPos;
        float movement = distance * deltaTime * 10; // Adjust the '10' to control the speed of the animation
        if (Math.abs(movement) < 0.001f) { // If the movement is very small, snap to the target position to prevent endless oscillation
            scrollPos = targetScrollPos;
        } else {
            scrollPos += movement;
        }

        drawRect(x, y, x + width, y + height, 0x80808080); // Draw background
        drawRect(x, y + (int)(scrollPos * height), x + width, y + (int)(scrollPos * height) + 20, 0xFFFFFFFF); // Draw handle
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            isDragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        isDragging = false;
    }

    public void mouseDragged(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick, float lastItemHeight, float totalContentHeight) {
        if (isDragging) {
            float maxScrollPos = 1 - (float)lastItemHeight / totalContentHeight;
            targetScrollPos = (float)(mouseY - y) / height;
            targetScrollPos = Math.max(0, Math.min(targetScrollPos, maxScrollPos));
        }
    }

    public void setScrollPos(float scrollPos) {
        this.targetScrollPos = scrollPos;
    }

    public float getScrollPos() {
        return scrollPos;
    }
}