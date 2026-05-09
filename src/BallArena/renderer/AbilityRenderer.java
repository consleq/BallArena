package BallArena.renderer;

import BallArena.ability.WallSpike;
import BallArena.model.Ball;
import BallArena.model.SpikeBall;
import BallArena.model.SwordBall;
import BallArena.ability.RotatingSword;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** 根據球的型別繪製對應技能 */
public class AbilityRenderer {

    public void render(GraphicsContext gc, Ball ball) {
        if (ball instanceof SwordBall sb) {
            renderSword(gc, sb.getSword());
        } else if (ball instanceof SpikeBall spb) {
            renderSpikes(gc, spb.getWallSpike());
        }
    }

    private void renderSword(GraphicsContext gc, RotatingSword sword) {
        gc.setStroke(Color.SILVER);
        gc.setLineWidth(4);
        gc.strokeLine(
                sword.getSwordStartX(), sword.getSwordStartY(),
                sword.getSwordEndX(),   sword.getSwordEndY()
        );
    }

    private void renderSpikes(GraphicsContext gc, WallSpike wallSpike) {
        gc.setFill(Color.ORANGERED);
        for (WallSpike.Spike spike : wallSpike.getSpikes()) {
            // 簡單畫一個小三角形代表尖刺
            double x = spike.x;
            double y = spike.y;
            gc.fillPolygon(
                    new double[]{x, x - 6, x + 6},
                    new double[]{y - 10, y + 4, y + 4},
                    3
            );
        }
    }
}