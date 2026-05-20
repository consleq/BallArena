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
        gc.save();

        gc.setStroke(Color.SILVER);
        gc.setLineWidth(4);
        gc.strokeLine(
                sword.getSwordStartX(), sword.getSwordStartY(),
                sword.getSwordEndX(),   sword.getSwordEndY()
        );

        gc.restore();
    }

    private void renderSpikes(GraphicsContext gc, WallSpike wallSpike) {
        gc.setFill(Color.ORANGERED);
        for (WallSpike.Spike spike : wallSpike.getSpikes()) {
            gc.save();
            gc.translate(spike.x, spike.y);
            gc.rotate(spike.angle);
            // 基底在原點(貼牆)、尖端往上(-SPIKE_LENGTH)；旋轉後尖端指向場地內側
            gc.fillPolygon(
                    new double[]{0, -6, 6},
                    new double[]{-WallSpike.SPIKE_LENGTH, 0, 0},
                    3
            );
            gc.restore();
        }
    }
}