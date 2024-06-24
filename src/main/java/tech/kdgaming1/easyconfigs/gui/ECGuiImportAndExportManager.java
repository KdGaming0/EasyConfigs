package tech.kdgaming1.easyconfigs.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import tech.kdgaming1.easyconfigs.easyconfighandler.ECSetup;
import tech.kdgaming1.easyconfigs.gui.guiutils.ECScrollBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ECGuiImportAndExportManager extends GuiScreen {
    private List<String> firstColumnFiles;
    private List<String> secondColumnFiles;
    private GuiScreen parentScreen;
    private static final int BOX_PADDING = 5;
    private static final int BOX_HEIGHT = 20;
    private static final int COLUMN_WIDTH = 200;
    private ECScrollBar scrollBar1;
    private ECScrollBar scrollBar2;

    private int scrollOffset1 = 0;
    private int scrollOffset2 = 0;
    private int targetScrollOffset1 = 0;
    private int targetScrollOffset2 = 0;
    private long lastTime; // Time of the last frame

    public ECGuiImportAndExportManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.firstColumnFiles = new ArrayList<>();
        this.secondColumnFiles = new ArrayList<>();
        loadFolderContents();
    }

    @Override
    public void initGui() {
        super.initGui();
        int boxHeight = this.height - 40 - 40; // Subtract the y-coordinate of the box and the bottom padding from the screen height
        this.scrollBar1 = new ECScrollBar(this.width / 4 + COLUMN_WIDTH / 2, 40, 20, boxHeight);
        this.scrollBar2 = new ECScrollBar(this.width * 3 / 4 + COLUMN_WIDTH / 2, 40, 20, boxHeight);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f; // Time elapsed since the last frame, in seconds
        lastTime = currentTime;

        // Gradually move scrollOffset1 and scrollOffset2 towards targetScrollOffset1 and targetScrollOffset2 with an ease-out effect and frame rate independence
        float distance1 = targetScrollOffset1 - scrollOffset1;
        float movement1 = distance1 * deltaTime * 10; // Adjust the '10' to control the speed of the animation
        if (Math.abs(movement1) < 0.001f) { // If the movement is very small, snap to the target position to prevent endless oscillation
            scrollOffset1 = targetScrollOffset1;
        } else {
            scrollOffset1 += movement1;
        }

        float distance2 = targetScrollOffset2 - scrollOffset2;
        float movement2 = distance2 * deltaTime * 10; // Adjust the '10' to control the speed of the animation
        if (Math.abs(movement2) < 0.001f) { // If the movement is very small, snap to the target position to prevent endless oscillation
            scrollOffset2 = targetScrollOffset2;
        } else {
            scrollOffset2 += movement2;
        }

        this.drawDefaultBackground();

        // Draw first column
        drawColumnBackground(this.width / 4 - COLUMN_WIDTH / 2, 40, COLUMN_WIDTH + 20, "");
        drawFileBoxes(secondColumnFiles, this.width / 4 - COLUMN_WIDTH / 2, 60 - scrollOffset1);

        // Draw second column
        drawColumnBackground(this.width * 3 / 4 - COLUMN_WIDTH / 2, 40, COLUMN_WIDTH + 20, "");
        drawFileBoxes(firstColumnFiles, this.width * 3 / 4 - COLUMN_WIDTH / 2, 60 - scrollOffset2);


        // Draw top and bottom frames with the same color as the box
        drawRect(this.width / 4 - COLUMN_WIDTH / 2, 40, this.width / 4 + COLUMN_WIDTH / 2, 60, 0x80000000); // Top frame for first column
        drawRect(this.width * 3 / 4 - COLUMN_WIDTH / 2, 40, this.width * 3 / 4 + COLUMN_WIDTH / 2, 60, 0x80000000); // Top frame for second column


        // Draw the text on top of the frames
        this.drawCenteredString(this.fontRendererObj, "Imported Folder", this.width / 4, 50 - 5, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Exported Folder", this.width * 3 / 4, 50 - 5, 0xFFFFFF);

        scrollBar1.draw();
        scrollBar2.draw();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawColumnBackground(int x, int y, int width, String title) {
        int boxHeight = this.height - y - 40; // Subtract 40 (or any other value) from the height
        drawRect(x, y, x + width, y + boxHeight, 0x80000000);
        this.drawCenteredString(this.fontRendererObj, title, x + width / 2, y + 5, 0xFFFFFF);
    }

    private void drawFileBoxes(List<String> files, int startX, int startY) {
        int y = startY;
        int titleHeight = 15; // Height of the title text
        int titlePadding = 5; // Additional padding below the title text
        int boxTop = 40 + titleHeight + titlePadding; // Top y-coordinate of the box
        int boxBottom = this.height - 40; // Bottom y-coordinate of the box
        for (String fileName : files) {
            if (y + BOX_HEIGHT < boxTop || y > boxBottom - BOX_HEIGHT) { // Skip drawing if outside of box bounds
                y += BOX_HEIGHT + BOX_PADDING;
                continue;
            }
            drawRect(startX + BOX_PADDING, y, startX + COLUMN_WIDTH - BOX_PADDING, y + BOX_HEIGHT, 0x80808080);
            this.drawString(this.fontRendererObj, fileName, startX + BOX_PADDING * 2, y + 6, 0xFFFFFF);
            y += BOX_HEIGHT + BOX_PADDING;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= this.width / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width / 4 + COLUMN_WIDTH / 2) {
            scrollBar1.mouseClicked(mouseX, mouseY, mouseButton);
        } else if (mouseX >= this.width * 3 / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width * 3 / 4 + COLUMN_WIDTH / 2) {
            scrollBar2.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseX >= this.width / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width / 4 + COLUMN_WIDTH / 2) {
            scrollBar1.mouseReleased(mouseX, mouseY, mouseButton);
        } else if (mouseX >= this.width * 3 / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width * 3 / 4 + COLUMN_WIDTH / 2) {
            scrollBar2.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        float lastItemHeight = BOX_HEIGHT;
        float totalContentHeight1 = (BOX_HEIGHT + BOX_PADDING) * firstColumnFiles.size();
        float totalContentHeight2 = (BOX_HEIGHT + BOX_PADDING) * secondColumnFiles.size();
        if (mouseX >= this.width / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width / 4 + COLUMN_WIDTH / 2) {
            scrollBar1.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, lastItemHeight, totalContentHeight1);
        } else if (mouseX >= this.width * 3 / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width * 3 / 4 + COLUMN_WIDTH / 2) {
            scrollBar2.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, lastItemHeight, totalContentHeight2);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            scroll /= 10;
            int viewportHeight = this.height - 40 - 40; // Subtract the y-coordinate of the box and the bottom padding from the screen height
            if (mouseX >= this.width / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width / 4 + COLUMN_WIDTH / 2) {
                targetScrollOffset1 -= scroll;
                targetScrollOffset1 = Math.max(0, Math.min(targetScrollOffset1, (BOX_HEIGHT + BOX_PADDING) * firstColumnFiles.size() - viewportHeight));
                scrollBar1.setScrollPos((float)targetScrollOffset1 / ((BOX_HEIGHT + BOX_PADDING) * firstColumnFiles.size() - viewportHeight));
            } else if (mouseX >= this.width * 3 / 4 - COLUMN_WIDTH / 2 && mouseX <= this.width * 3 / 4 + COLUMN_WIDTH / 2) {
                targetScrollOffset2 -= scroll;
                targetScrollOffset2 = Math.max(0, Math.min(targetScrollOffset2, (BOX_HEIGHT + BOX_PADDING) * secondColumnFiles.size() - viewportHeight));
                scrollBar2.setScrollPos((float)targetScrollOffset2 / ((BOX_HEIGHT + BOX_PADDING) * secondColumnFiles.size() - viewportHeight));
            }
        }
    }

    private void loadFolderContents() {
        loadFilesFromFolder(new File(ECSetup.ECExport), firstColumnFiles);
        loadFilesFromFolder(new File(ECSetup.ECImport), secondColumnFiles);
    }

    private void loadFilesFromFolder(File folder, List<String> fileList) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".zip")) {
                    fileList.add(file.getName());
                }
            }
        }
    }
}