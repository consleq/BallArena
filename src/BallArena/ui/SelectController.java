package BallArena.ui;

import BallArena.model.BallType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Random;

/**
 * 選球畫面 Controller
 * 玩家點擊球種按鈕後，敵方從 BallType.values() 中隨機選一個，
 * 接著載入 game.fxml 並把雙方球種傳給 GameController
 */
public class SelectController {

    @FXML private Button swordButton;
    @FXML private Button spikeButton;
    @FXML private Button fireButton;
    @FXML private Button backButton;

    private static final Random RNG = new Random();

    @FXML
    private void onSelectSword() {
        startGame(BallType.SWORD);
    }

    @FXML
    private void onSelectSpike() {
        startGame(BallType.SPIKE);
    }

    @FXML
    private void onSelectFire() {
        startGame(BallType.FIRE);
    }

    @FXML
    private void onBackToMenu() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/BallArena/menu.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }

    /** 載入遊戲場景並指定雙方球種 */
    private void startGame(BallType playerType) {
        BallType enemyType = randomBallType();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/BallArena/game.fxml")
            );
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setBallTypes(playerType, enemyType);

            Stage stage = (Stage) swordButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 敵方隨機選擇球種（涵蓋所有 BallType 值，未來新增球種無需修改） */
    private BallType randomBallType() {
        BallType[] all = BallType.values();
        return all[RNG.nextInt(all.length)];
    }
}
