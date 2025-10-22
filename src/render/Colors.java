package render;

import java.awt.Color;


public enum Colors {
    BUTTON_COLOR(new Color(0xfef498)),
    TITLE_COLOR(new Color(0x000000)),
    SUBTITLE_COLOR(new Color(0x000000)),
    BACKGROUND_COLOR(new Color(0x1167a6));

    public final Color val;

    Colors(Color color) {
        this.val = color;
    }

}
