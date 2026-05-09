package BallArena.renderer;

import BallArena.model.Ball;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BallRenderer {

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
    }
}