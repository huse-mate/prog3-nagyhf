package render;

import java.awt.Color;


public enum Colors {
    BUTTON_COLOR(new Color(0xfef498)),
    TITLE_COLOR(new Color(0x000000)),
    SUBTITLE_COLOR(new Color(0x000000)),
    BACKGROUND_COLOR(new Color(0xfef498)),
    MAP_BACKGROUND_COLOR(new Color(0x1167a6)),
    PLAYERPANEL_COLOR(new Color(0xfef498)),
    HIGHLIGHTED_PLAYERPANEL_COLOR(new Color(0xfefdd2)),

    POSSIBLE_SETTLEMENT_COLOR(new Color(0xd3d3d3)),
    PRESSED_SETTLEMENT_COLOR(new Color(0xaaaaaa)),
    POSSIBLE_CITY_COLOR(new Color(0xd3d3d3)),
    PRESSED_CITY_COLOR(new Color(0xaaaaaa)),
    POSSIBLE_ROAD_COLOR(new Color(0xd3d3d3)),
    PRESSED_ROAD_COLOR(new Color(0xaaaaaa)),

    PLAYER1_COLOR(new Color(0x00ff00)),
    PLAYER2_COLOR(new Color(0x0000ff)),
    PLAYER3_COLOR(new Color(0xff00ff)),
    PLAYER4_COLOR(new Color(0xff0000));

    public final Color val;

    Colors(Color color) {
        this.val = color;
    }

}
