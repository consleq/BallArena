package BallArena.renderer;

import BallArena.model.Ball;
import BallArena.model.ArenaConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HudRenderer {

    private static final double BAR_WIDTH  = 200;
    private static final double BAR_HEIGHT = 16;
    private static final double BAR_Y      = ArenaConfig.HEIGHT + 10; // 畫在場地下方
    // 注意：Canvas 高度要設定比 ArenaConfig.HEIGHT 多一些才看得到

    public void render(GraphicsContext gc, Ball ball1, Ball ball2) {
        // 玩家一血量條（左側）
        drawHpBar(gc, 10, BAR_Y, ball1, Color.CORNFLOWERBLUE, "P1");

        // 玩家二血量條（右側）
        drawHpBar(gc, ArenaConfig.WIDTH - BAR_WIDTH - 10, BAR_Y, ball2, Color.TOMATO, "P2");
    }

    private void drawHpBar(GraphicsContext gc, double x, double y,
                           Ball ball, Color color, String label) {
        double ratio = ball.getHp() / ball.getMaxHp();

        // 背景
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(x, y, BAR_WIDTH, BAR_HEIGHT, 8, 8);

        // 血量
        gc.setFill(color);
        gc.fillRoundRect(x, y, BAR_WIDTH * ratio, BAR_HEIGHT, 8, 8);

        // 文字
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.fillText(label + " " + (int) ball.getHp() + "/" + (int) ball.getMaxHp(),
                x + 4, y + 12);
    }
}