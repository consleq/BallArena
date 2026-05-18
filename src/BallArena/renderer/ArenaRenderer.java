package BallArena.renderer;

import BallArena.model.Ball;
import BallArena.model.ArenaConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** 負責統籌所有繪製，只讀取 model 狀態，不修改 */
public class ArenaRenderer {

    private final BallRenderer    ballRenderer    = new BallRenderer();
    private final AbilityRenderer abilityRenderer = new AbilityRenderer();
    private final HudRenderer     hudRenderer     = new HudRenderer();

    public void render(GraphicsContext gc, Ball ball1, Ball ball2) {
        gc.clearRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);
        gc.save();

        gc.beginPath();
        gc.rect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);
        gc.clip();

        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);

        gc.setStroke(Color.web("#4a4a8a"));
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);

        abilityRenderer.render(gc, ball1);
        abilityRenderer.render(gc, ball2);

        // 顏色從球的 style 取得
        ballRenderer.render(gc, ball1, BallColorMap.colorOf(ball1.getStyle()));
        ballRenderer.render(gc, ball2, BallColorMap.colorOf(ball2.getStyle()));

        hudRenderer.render(gc, ball1, ball2);

        gc.restore();
    }
}