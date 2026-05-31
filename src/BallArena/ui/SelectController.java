package BallArena.ui;

import BallArena.model.BallType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * 選球畫面 Controller
 * 改良為雙方皆可手動選擇角色，並支援上下拖動選單。
 */
public class SelectController {

    @FXML private StackPane playerContainer;
    @FXML private StackPane enemyContainer;
    @FXML private Button backButton;
    @FXML private Button startButton;

    private DraggableList playerList;
    private DraggableList enemyList;

    @FXML
    public void initialize() {
        playerList = new DraggableList(playerContainer, BallType.values());
        enemyList = new DraggableList(enemyContainer, BallType.values());
    }

    @FXML
    private void onStartBattle() {
        startGame(playerList.getSelectedType(), enemyList.getSelectedType());
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
    private void startGame(BallType playerType, BallType enemyType) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/BallArena/game.fxml")
            );
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.setBallTypes(playerType, enemyType);

            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 內部類別：實現上下拖動選擇列表
     */
    private class DraggableList {
        private final VBox content;
        private final BallType[] types;
        private double startY;
        private double initialLayoutY;
        private int selectedIndex = 0;
        private final double itemHeight = 80;
        private final double containerHeight = 240;

        public DraggableList(StackPane container, BallType[] types) {
            this.types = types;
            this.content = new VBox();
            this.content.setAlignment(Pos.CENTER);
            this.content.setPrefWidth(280);

            for (BallType type : types) {
                VBox card = createCard(type);
                card.setPrefHeight(itemHeight);
                content.getChildren().add(card);
            }

            // 裁切區域
            Pane clipPane = new Pane(content);
            Rectangle clip = new Rectangle(280, containerHeight);
            clipPane.setClip(clip);
            
            // 選取框（視覺輔助）
            Pane overlay = new Pane();
            overlay.getStyleClass().add("selection-overlay");
            overlay.setPrefSize(270, itemHeight);
            overlay.setLayoutX(5);
            overlay.setLayoutY((containerHeight - itemHeight) / 2);
            overlay.setMouseTransparent(true);
            
            container.getChildren().addAll(clipPane, overlay);

            // 初始置中第一個項目
            snapTo(0);

            // 拖動邏輯
            content.setOnMousePressed(e -> {
                startY = e.getSceneY();
                initialLayoutY = content.getLayoutY();
            });

            content.setOnMouseDragged(e -> {
                double deltaY = e.getSceneY() - startY;
                double newY = initialLayoutY + deltaY;
                
                // 限制拖動範圍（稍微超出邊界一點，增加彈性感）
                double minY = (containerHeight - itemHeight) / 2 - (types.length - 1) * itemHeight - 50;
                double maxY = (containerHeight - itemHeight) / 2 + 50;
                content.setLayoutY(Math.max(minY, Math.min(maxY, newY)));
            });

            content.setOnMouseReleased(e -> snap());
        }

        private VBox createCard(BallType type) {
            VBox card = new VBox();
            card.getStyleClass().add("ball-card");
            
            Label title = new Label(type.getDisplayName());
            title.getStyleClass().add("ball-card-title");
            
            Label desc = new Label(type.getDescription());
            desc.getStyleClass().add("ball-card-desc");
            desc.setWrapText(true);
            desc.setPrefWidth(240);
            
            card.getChildren().addAll(title, desc);
            return card;
        }

        private void snap() {
            double currentY = content.getLayoutY();
            double centerOffset = (containerHeight - itemHeight) / 2;
            selectedIndex = (int) Math.round((centerOffset - currentY) / itemHeight);
            selectedIndex = Math.max(0, Math.min(types.length - 1, selectedIndex));
            
            snapTo(selectedIndex);
        }

        private void snapTo(int index) {
            selectedIndex = index;
            double targetY = (containerHeight - itemHeight) / 2 - (selectedIndex * itemHeight);
            content.setLayoutY(targetY);
        }

        public BallType getSelectedType() {
            return types[selectedIndex];
        }
    }
}
