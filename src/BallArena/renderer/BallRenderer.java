package BallArena.renderer;

import BallArena.model.Ball;
import BallArena.model.MysteryBall;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class BallRenderer {

    /** 神秘球本體問號使用的 PressStart2P 字體（載入一次重複使用） */
    private static final Font SYMBOL_FONT = loadSymbolFont();

    private static Font loadSymbolFont() {
        var is = BallRenderer.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf");
        if (is != null) {
            Font f = Font.loadFont(is, 18);
            if (f != null) return f;
        }
        // 載入失敗時退回等寬字體
        return Font.font("Monospaced", 18);
    }

    public void render(GraphicsContext gc, Ball ball, Color color) {
        double x = ball.getX();
        double y = ball.getY();
        double r = ball.getRadius();

        // 畫球（以球心為圓心）
        gc.setFill(color);
        gc.fillOval(x - r, y - r, r * 2, r * 2);

        // 邊框
        gc.setStroke(color.darker());
        gc.setLineWidth(2);
        gc.strokeOval(x - r, y - r, r * 2, r * 2);

        // 神秘球：在球心畫一個問號（PressStart2P）
        if (ball instanceof MysteryBall) {
            gc.setFill(Color.WHITE);
            gc.setFont(SYMBOL_FONT);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("?", x, y);
        }
    }
}
