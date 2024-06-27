package tech.kdgaming1.easyconfigs.gui.guiutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ECDropdownMenu extends Gui {
    private final Minecraft mc;
    private int x, y, width, height;
    private final List<String> options;
    private boolean isExpanded;
    private int selectedIndex;
    private int scrollOffset;
    private static final int OPTION_HEIGHT = 20;
    private static final int MAX_VISIBLE_OPTIONS = 5;
    private Consumer<String> selectionListener;
    private int scrollbarY;

    public ECDropdownMenu(Minecraft mc, int x, int y, int width, int height, List<String> options) {
        this.mc = mc;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.options = new ArrayList<>(options);
        this.isExpanded = false;
        this.selectedIndex = -1;
        this.scrollOffset = 0;
    }

    public void drawDropdown(int mouseX, int mouseY) {
        drawRect(x, y, x + width, y + height, 0xFF888888);

        if (isValidIndex(selectedIndex)) {
            String selectedOption = options.get(selectedIndex);
            mc.fontRendererObj.drawString(selectedOption, x + 5, y + 5, 0xFFFFFF);
        } else {
            mc.fontRendererObj.drawString("Select an option", x + 5, y + 5, 0xFFFFFF);
        }

        mc.fontRendererObj.drawString("▼", x + width - 10, y + 5, 0xFFFFFF);

        if (isExpanded) {
            drawExpandedDropdown(mouseX, mouseY);
        }
    }

    private void drawExpandedDropdown(int mouseX, int mouseY) {
        drawRect(x, y + height, x + width, y + height + MAX_VISIBLE_OPTIONS * OPTION_HEIGHT, 0xFF000000);
        int end = Math.min(scrollOffset + MAX_VISIBLE_OPTIONS, options.size());
        for (int i = scrollOffset; i < end; i++) {
            int optionY = y + height + (i - scrollOffset) * OPTION_HEIGHT;
            if (mouseX >= x && mouseX <= x + width && mouseY >= optionY && mouseY <= optionY + OPTION_HEIGHT) {
                drawRect(x, optionY, x + width, optionY + OPTION_HEIGHT, 0xFF555555);
            }
            if (isValidIndex(i)) {
                mc.fontRendererObj.drawString(options.get(i), x + 5, optionY + 5, 0xFFFFFF);
            }
        }

        drawScrollbar();
    }

    private void drawScrollbar() {
        int scrollbarX = x + width - 10;
        int scrollbarHeight = (int) ((float) MAX_VISIBLE_OPTIONS / options.size() * OPTION_HEIGHT * MAX_VISIBLE_OPTIONS);
        int scrollbarColor = 0xFFAAAAAA;
        scrollbarY = y + height + (int) ((float) scrollOffset / Math.max(1, options.size() - MAX_VISIBLE_OPTIONS) * (OPTION_HEIGHT * MAX_VISIBLE_OPTIONS - scrollbarHeight));
        drawRect(scrollbarX, scrollbarY, scrollbarX + 5, scrollbarY + scrollbarHeight, scrollbarColor);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            isExpanded = !isExpanded;
        } else if (isExpanded) {
            handleExpandedClick(mouseX, mouseY);
        } else {
            isExpanded = false;
        }
    }

    private void handleExpandedClick(int mouseX, int mouseY) {
        int end = Math.min(scrollOffset + MAX_VISIBLE_OPTIONS, options.size());
        for (int i = scrollOffset; i < end; i++) {
            int optionY = y + height + (i - scrollOffset) * OPTION_HEIGHT;
            if (mouseX >= x && mouseX <= x + width && mouseY >= optionY && mouseY <= optionY + OPTION_HEIGHT) {
                selectedIndex = i;
                isExpanded = false;
                if (selectionListener != null && isValidIndex(selectedIndex)) {
                    selectionListener.accept(options.get(selectedIndex));
                }
                break;
            }
        }
    }

    public void handleMouseInput() {
        if (isExpanded) {
            int dWheel = Mouse.getEventDWheel();
            if (dWheel != 0) {
                if (dWheel > 0) {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                } else if (dWheel < 0) {
                    scrollOffset = Math.min(Math.max(0, options.size() - MAX_VISIBLE_OPTIONS), scrollOffset + 1);
                }
            }
        }
    }

    public void handleKeyboardInput(int keyCode) {
        if (isExpanded) {
            if (keyCode == Keyboard.KEY_UP) {
                selectedIndex = Math.max(0, selectedIndex - 1);
                scrollOffset = Math.max(0, selectedIndex - MAX_VISIBLE_OPTIONS + 1);
            } else if (keyCode == Keyboard.KEY_DOWN) {
                selectedIndex = Math.min(options.size() - 1, selectedIndex + 1);
                scrollOffset = Math.min(Math.max(0, options.size() - MAX_VISIBLE_OPTIONS), selectedIndex);
            }
        }
    }


    public void updateOptions(List<String> newOptions) {
        this.options.clear();
        this.options.addAll(newOptions);
        this.selectedIndex = -1;
        this.scrollOffset = 0;
    }


    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < options.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectedOption() {
        return isValidIndex(selectedIndex) ? options.get(selectedIndex) : null;
    }

    public void setSelectionListener(Consumer<String> listener) {
        this.selectionListener = listener;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isExpanded() {
        return isExpanded;
    }
}