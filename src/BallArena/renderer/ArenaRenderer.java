package BallArena.renderer;

import BallArena.model.Ball;
import BallArena.model.ArenaConfig;
import BallArena.model.SwordBall;
import BallArena.model.SpikeBall;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** 負責統籌所有繪製，只讀取 model 狀態，不修改 */
public class ArenaRenderer {

    private final BallRenderer    ballRenderer    = new BallRenderer();
    private final AbilityRenderer abilityRenderer = new AbilityRenderer();
    private final HudRenderer     hudRenderer     = new HudRenderer();

    public void render(GraphicsContext gc, Ball ball1, Ball ball2) {
        // 清空畫布
        gc.clearRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);
        gc.save();

        // 限制所有繪製在場地範圍內
        gc.beginPath();
        gc.rect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);
        gc.clip();

        // Background
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);

        // 繪製場地邊框
        gc.setStroke(Color.web("#4a4a8a"));
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);

        // 繪製技能（在球底下）
        abilityRenderer.render(gc, ball1);
        abilityRenderer.render(gc, ball2);

        // 繪製球本體
        ballRenderer.render(gc, ball1, Color.CORNFLOWERBLUE);
        ballRenderer.render(gc, ball2, Color.TOMATO);

        // 繪製 HUD（血量條等）
        hudRenderer.render(gc, ball1, ball2);

        gc.restore();
    }
}