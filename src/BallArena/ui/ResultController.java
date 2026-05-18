package BallArena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ResultController {

    @FXML private Label winnerLabel;
    @FXML private Button backButton;

    /**
     * 由 GameController 在切換場景時呼叫，
     * 設定獲勝方資訊（名稱 + 顏色）
     */
    public void setWinner(String winnerName, Color color) {
        winnerLabel.setText(winnerName + " 獲勝！");
        winnerLabel.setStyle(
                "-fx-font-size: 32px; -fx-font-weight: bold; " +
                        "-fx-text-fill: " + toWebColor(color) + ";"
        );
    }

    /** 將 Color 轉為 CSS 可用的 #RRGGBB */
    private String toWebColor(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed()   * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue()  * 255));
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
}