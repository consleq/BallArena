package BallArena.renderer;

import BallArena.ability.TurretSkill;
import BallArena.model.EngineerBall;
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
        } else if (ball instanceof EngineerBall eb) {
            renderTurret(gc, eb.getTurretSkill());
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
    /** 繪製工程師的砲台與子彈 */
    private void renderTurret(GraphicsContext gc, TurretSkill skill) {
        var turret = skill.getTurret();

        // 1. 畫飛行的子彈
        for (var b : skill.getBullets()) {
            gc.setFill(Color.GOLD);
            gc.fillOval(b.x - TurretSkill.TurretBullet.RADIUS, b.y - TurretSkill.TurretBullet.RADIUS,
                    TurretSkill.TurretBullet.RADIUS * 2, TurretSkill.TurretBullet.RADIUS * 2);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeOval(b.x - TurretSkill.TurretBullet.RADIUS, b.y - TurretSkill.TurretBullet.RADIUS,
                    TurretSkill.TurretBullet.RADIUS * 2, TurretSkill.TurretBullet.RADIUS * 2);
        }

        if (turret == null) return; // 砲台尚未佈署

        double r = turret.radius;

        // 根據等級改變砲台主色
        Color turretColor = switch (turret.level) {
            case 1 -> Color.LIGHTGRAY;
            case 2 -> Color.YELLOW;
            case 3 -> Color.ORANGE;
            default -> Color.DARKGRAY; // 0級 (未啟動)
        };

        // 2. 畫砲管 (0級不畫砲管)
        if (turret.level > 0) {
            gc.save();
            // 將畫布原點移動到砲台中心，並旋轉到瞄準敵人的角度
            gc.translate(turret.x, turret.y);
            gc.rotate(Math.toDegrees(turret.angle));

            gc.setFill(Color.DARKSLATEGRAY);
            // 畫一個長方形砲管，從中心往右延伸 22px，高度為 8px
            gc.fillRect(0, -4, 22, 8);
            gc.restore();
        }

        // 3. 畫砲台基座 (圓形固定大小)
        gc.setFill(turretColor);
        gc.fillOval(turret.x - r, turret.y - r, r * 2, r * 2);
        gc.setStroke(Color.DARKSLATEGRAY);
        gc.setLineWidth(2);
        gc.strokeOval(turret.x - r, turret.y - r, r * 2, r * 2);

        // 4. 高等級時，在砲台上方畫剩餘時間進度條
        if (turret.level >= 2) {
            double barWidth = 28;
            double barHeight = 4;
            double barX = turret.x - barWidth / 2;
            double barY = turret.y - r - 8;
            double progress = turret.getLevelProgress();

            // 背景（深灰）
            gc.setFill(Color.color(0, 0, 0, 0.55));
            gc.fillRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);

            // 進度（依等級顏色）
            gc.setFill(turret.level == 3 ? Color.ORANGE : Color.YELLOW);
            gc.fillRect(barX, barY, barWidth * progress, barHeight);

            // 外框
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeRect(barX, barY, barWidth, barHeight);
        }
    }
}