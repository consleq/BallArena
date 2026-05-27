package BallArena.ui;

import BallArena.model.*;
import BallArena.renderer.ArenaRenderer;
import BallArena.renderer.BallColorMap;
import BallArena.renderer.PopupRenderer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class GameController {

    @FXML private Canvas gameCanvas;

    private Ball ball1;
    private Ball ball2;
    private PhysicsEngine physics;
    private ArenaRenderer renderer;
    private PopupRenderer popupRenderer;
    private AnimationTimer gameLoop;
    private long lastTime = -1;
    private boolean gameEnded = false;

    @FXML
    public void initialize() {
        physics       = new PhysicsEngine();
        renderer      = new ArenaRenderer();
        popupRenderer = new PopupRenderer();
        // 球與遊戲迴圈會在 setBallTypes() 被呼叫後才建立
    }

    /**
     * 由 SelectController 在切換場景時呼叫，指定雙方球種並啟動遊戲
     * 玩家球(藍)在左、敵方球(紅)在右
     */
    public void setBallTypes(BallType playerType, BallType enemyType) {
        ball1 = createBall(playerType, 80,  150, BallStyle.BLUE);
        ball2 = createBall(enemyType,  220, 150, BallStyle.RED);

        // 需要鎖定對手的球種（如火球）在雙方建立完成後設定 target
        if (ball1 instanceof FireBall fb1) fb1.setTarget(ball2);
        if (ball2 instanceof FireBall fb2) fb2.setTarget(ball1);

        if (ball1 instanceof EngineerBall eb1) eb1.setTarget(ball2);
        if (ball2 instanceof EngineerBall eb2) eb2.setTarget(ball1);

        Random rng = new Random();
        double speed = 180;
        double angle1 = rng.nextDouble() * Math.PI * 2;
        double angle2 = rng.nextDouble() * Math.PI * 2;
        ball1.setVx(Math.cos(angle1) * speed);
        ball1.setVy(Math.sin(angle1) * speed);
        ball2.setVx(Math.cos(angle2) * speed);
        ball2.setVy(Math.sin(angle2) * speed);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) { lastTime = now; return; }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                deltaTime = Math.min(deltaTime, 0.05);

                update(deltaTime);
                render();
            }
        };
        gameLoop.start();
    }

    /** 依照 BallType 建立對應的 Ball 子類別；新增球種時須在此加 case */
    private Ball createBall(BallType type, double x, double y, BallStyle style) {
        return switch (type) {
            case SWORD -> new SwordBall(x, y, style);
            case SPIKE -> new SpikeBall(x, y, style);
            case FIRE  -> new FireBall(x, y, style);
            case ENGINEER -> new EngineerBall(x, y, style);
        };
    }

    private void update(double deltaTime) {
        if (gameEnded) return;

        physics.update(ball1, deltaTime);
        physics.update(ball2, deltaTime);
        physics.handleBallCollision(ball1, ball2);
        // 判定工程師是否撞擊自己的砲台升級
        if (ball1 instanceof EngineerBall eb1) physics.checkTurretUpgrade(eb1);
        if (ball2 instanceof EngineerBall eb2) physics.checkTurretUpgrade(eb2);
        ball1.updateAbility(deltaTime);
        ball2.updateAbility(deltaTime);

        physics.updateCooldowns(deltaTime);
        popupRenderer.update(deltaTime);

        // 判定命中；若造成傷害則在被攻擊球上方顯示扣血 popup
        if (ball1 instanceof SwordBall sb1) {
            double dmg = physics.checkSwordHit(sb1, ball2);
            if (dmg > 0) popupRenderer.addPopup(ball2.getX(), ball2.getY() - ball2.getRadius() - 4, dmg);
        }
        if (ball2 instanceof SwordBall sb2) {
            double dmg = physics.checkSwordHit(sb2, ball1);
            if (dmg > 0) popupRenderer.addPopup(ball1.getX(), ball1.getY() - ball1.getRadius() - 4, dmg);
        }
        if (ball1 instanceof SpikeBall spb1) {
            double dmg = physics.checkSpikeHit(spb1, ball2);
            if (dmg > 0) popupRenderer.addPopup(ball2.getX(), ball2.getY() - ball2.getRadius() - 4, dmg);
        }
        if (ball2 instanceof SpikeBall spb2) {
            double dmg = physics.checkSpikeHit(spb2, ball1);
            if (dmg > 0) popupRenderer.addPopup(ball1.getX(), ball1.getY() - ball1.getRadius() - 4, dmg);
        }
        // 火球範圍傷害每秒結算一次 popup，平常仍每幀少量扣血
        if (ball1 instanceof FireBall fb1) {
            double dmg = physics.checkFireZoneDamage(fb1, ball2, deltaTime);
            if (dmg >= 1.0) popupRenderer.addPopup(ball2.getX(), ball2.getY() - ball2.getRadius() - 4, dmg);
        }
        if (ball2 instanceof FireBall fb2) {
            double dmg = physics.checkFireZoneDamage(fb2, ball1, deltaTime);
            if (dmg >= 1.0) popupRenderer.addPopup(ball1.getX(), ball1.getY() - ball1.getRadius() - 4, dmg);
        }
        if (ball1 instanceof EngineerBall eb1) {
            double dmg = physics.checkTurretBulletHit(eb1, ball2);
            if (dmg > 0) popupRenderer.addPopup(ball2.getX(), ball2.getY() - ball2.getRadius() - 4, dmg);
        }
        if (ball2 instanceof EngineerBall eb2) {
            double dmg = physics.checkTurretBulletHit(eb2, ball1);
            if (dmg > 0) popupRenderer.addPopup(ball1.getX(), ball1.getY() - ball1.getRadius() - 4, dmg);
        }

        checkGameOver();
    }

    private void checkGameOver() {
        if (ball1.isDead() && ball2.isDead()) {
            endGame("平手", Color.GRAY);
        } else if (ball1.isDead()) {
            endGame("敵方（" + ball2.getTypeName() + "）", BallColorMap.colorOf(ball2.getStyle()));
        } else if (ball2.isDead()) {
            endGame("玩家（" + ball1.getTypeName() + "）", BallColorMap.colorOf(ball1.getStyle()));
        }
    }

    private void endGame(String winnerName, Color color) {
        gameEnded = true;
        gameLoop.stop();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/BallArena/result.fxml")
            );
            Parent root = loader.load();

            ResultController resultController = loader.getController();
            resultController.setWinner(winnerName, color);

            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void render() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        renderer.render(gc, ball1, ball2);
        popupRenderer.render(gc);
    }
}