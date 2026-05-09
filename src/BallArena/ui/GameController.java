package BallArena.ui;

import BallArena.model.*;
import BallArena.renderer.ArenaRenderer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;

public class GameController {

    @FXML private Canvas gameCanvas;

    private Ball ball1;
    private Ball ball2;
    private PhysicsEngine physics;
    private ArenaRenderer renderer;
    private AnimationTimer gameLoop;
    private long lastTime = -1;

    @FXML
    public void initialize() {
        physics  = new PhysicsEngine();
        renderer = new ArenaRenderer();

        // 暫時寫死球種（之後從 Menu 傳入）
        ball1 = new SwordBall(200, 250);
        ball2 = new SpikeBall(500, 250);

        // 給兩球隨機初速
        Random rng = new Random();
        double speed = 180; // px/秒
        double angle1 = rng.nextDouble() * Math.PI * 2;
        double angle2 = rng.nextDouble() * Math.PI * 2;
        ball1.setVx(Math.cos(angle1) * speed);
        ball1.setVy(Math.sin(angle1) * speed);
        ball2.setVx(Math.cos(angle2) * speed);
        ball2.setVy(Math.sin(angle2) * speed);

        // 啟動遊戲主迴圈
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) { lastTime = now; return; }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                // 上限 deltaTime，避免視窗拖移造成大跳躍
                deltaTime = Math.min(deltaTime, 0.05);

                update(deltaTime);
                render();
            }
        };
        gameLoop.start();
    }

    private void update(double deltaTime) {
        physics.update(ball1, deltaTime);
        physics.update(ball2, deltaTime);
        physics.handleBallCollision(ball1, ball2);
        ball1.updateAbility(deltaTime);
        ball2.updateAbility(deltaTime);
    }

    private void render() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        renderer.render(gc, ball1, ball2);
    }
}