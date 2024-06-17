package tech.kdgaming1.irespectyouroptions.page;

import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.utils.InputHandler;

public class IRespectYourOptionsPage extends Page {
    public IRespectYourOptionsPage() {
        super("Mod Pack Developer Settings");
    }
    public void draw(long vg, int x, int y) {
        // draw script for the page
    }

    // SCROLLING
    // if you want it to be scrollable, you can use the following methods:
    public int drawStatic(long vg, int x, int y) {
        // draw elements that are not going to be scrollable
        return 12; // return the height of the elements that are drawn in this method
    }

    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {

    }

    public int getMaxScrollHeight() {
        return 1240; // return the total length of the page (how far can be scrolled)
    }
}
