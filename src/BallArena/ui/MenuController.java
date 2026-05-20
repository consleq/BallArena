package BallArena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class MenuController {

    @FXML private Button startButton;

    @FXML
    private void onStartGame() throws Exception {
        // 切換到選球畫面
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/BallArena/select.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }
}