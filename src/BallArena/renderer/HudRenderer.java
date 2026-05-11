package BallArena.renderer;

import BallArena.model.Ball;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class HudRenderer {

    public void render(GraphicsContext gc, Ball ball1, Ball ball2) {
        drawHpAboveBall(gc, ball1, Color.CORNFLOWERBLUE);
        drawHpAboveBall(gc, ball2, Color.TOMATO);
    }

    private void drawHpAboveBall(GraphicsContext gc, Ball ball, Color color) {
        String text = String.valueOf((int) ball.getHp());
        double x = ball.getX();
        double y = ball.getY() - ball.getRadius() - 8;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.CENTER);

        // 陰影
        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillText(text, x + 1, y + 1);

        // 文字本體
        gc.setFill(Color.WHITE);
        gc.fillText(text, x, y);
    }
}