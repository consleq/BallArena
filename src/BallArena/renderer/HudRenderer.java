package BallArena.renderer;

import BallArena.model.Ball;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;

public class HudRenderer {

    private final Font pressStartFont;

    public HudRenderer() {
        InputStream stream = HudRenderer.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf");
        Font loaded = (stream != null) ? Font.loadFont(stream, 10) : null;
        pressStartFont = (loaded != null) ? loaded : Font.font("Arial", 13);
    }

    public void render(GraphicsContext gc, Ball ball1, Ball ball2) {
        // 顏色從球的 style 取得，不再寫死
        drawHpAboveBall(gc, ball1, BallColorMap.colorOf(ball1.getStyle()));
        drawHpAboveBall(gc, ball2, BallColorMap.colorOf(ball2.getStyle()));
    }

    private void drawHpAboveBall(GraphicsContext gc, Ball ball, Color color) {
        String text = String.valueOf((int) ball.getHp());
        double x = ball.getX();
        double y = ball.getY() - ball.getRadius() - 8;

        gc.setFont(pressStartFont);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillText(text, x + 1, y + 1);

        gc.setFill(Color.WHITE);
        gc.fillText(text, x, y);
    }
}