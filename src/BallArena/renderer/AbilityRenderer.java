package BallArena.renderer;

import BallArena.ability.FireSpell;
import BallArena.ability.WallSpike;
import BallArena.model.Ball;
import BallArena.model.FireBall;
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
            renderSpikes(gc, spb.getWallSpike(), BallColorMap.colorOf(spb.getStyle()));
        } else if (ball instanceof FireBall fb) {
            renderFireSpell(gc, fb.getFireSpell());
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

    private void renderFireSpell(GraphicsContext gc, FireSpell fireSpell) {
        // 先畫範圍傷害區（在下層）
        for (FireSpell.FireZone zone : fireSpell.getZones()) {
            double alpha = zone.getProgress();
            double r = FireSpell.FireZone.RADIUS;

            // 半透明火紅填充
            gc.setFill(Color.color(1.0, 0.35, 0.05, 0.28 * alpha));
            gc.fillOval(zone.x - r, zone.y - r, r * 2, r * 2);

            // 較深的邊框
            gc.setStroke(Color.color(1.0, 0.45, 0.0, 0.85 * alpha));
            gc.setLineWidth(2);
            gc.strokeOval(zone.x - r, zone.y - r, r * 2, r * 2);
        }

        // 再畫飛行中的投射物（在上層）
        for (FireSpell.FireProjectile p : fireSpell.getProjectiles()) {
            double r = FireSpell.FireProjectile.RADIUS;
            gc.setFill(Color.ORANGERED);
            gc.fillOval(p.x - r, p.y - r, r * 2, r * 2);
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(1);
            gc.strokeOval(p.x - r, p.y - r, r * 2, r * 2);
        }
    }

    private void renderSpikes(GraphicsContext gc, WallSpike wallSpike, Color color) {
        gc.setFill(color);
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