package BallArena.renderer;

import BallArena.model.BallStyle;
import javafx.scene.paint.Color;

/** 將 model 層的 BallStyle 對應到 JavaFX 的實際顏色 */
public class BallColorMap {

    /** 取得球的主色 */
    public static Color colorOf(BallStyle style) {
        return switch (style) {
            case BLUE -> Color.CORNFLOWERBLUE;
            case RED  -> Color.TOMATO;
        };
    }
}