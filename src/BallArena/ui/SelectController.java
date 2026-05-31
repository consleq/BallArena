package BallArena.ui;

import BallArena.model.BallType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * 選球畫面 Controller
 * 使用垂直 ScrollBar 讓雙方各自選擇角色。
 */
public class SelectController {

    @FXML private StackPane playerContainer;
    @FXML private StackPane enemyContainer;
    @FXML private Button backButton;
    @FXML private Button startButton;

    private ScrollableList playerList;
    private ScrollableList enemyList;

    @FXML
    public void initialize() {
        playerList = new ScrollableList(playerContainer, BallType.values());
        enemyList = new ScrollableList(enemyContainer, BallType.values());
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
     * 內部類別：使用垂直 ScrollBar 滾動選擇清單
     */
    private class ScrollableList {
        private final VBox content;
        private final BallType[] types;
        private int selectedIndex = 0;
        private final double itemHeight = 80;
        private final double containerHeight = 240;
        private final double cardWidth = 248;
        private final ScrollBar scrollBar;

        public ScrollableList(StackPane container, BallType[] types) {
            this.types = types;
            this.content = new VBox();
            this.content.setAlignment(Pos.CENTER);
            this.content.setPrefWidth(cardWidth);

            for (BallType type : types) {
                VBox card = createCard(type);
                card.setPrefHeight(itemHeight);
                content.getChildren().add(card);
            }

            // 裁切卡片區域
            Pane cardPane = new Pane(content);
            Rectangle clip = new Rectangle(cardWidth, containerHeight);
            cardPane.setClip(clip);
            cardPane.setPrefSize(cardWidth, containerHeight);

            // 選取框（視覺輔助）
            Pane overlay = new Pane();
            overlay.getStyleClass().add("selection-overlay");
            overlay.setPrefSize(cardWidth - 10, itemHeight);
            overlay.setLayoutX(5);
            overlay.setLayoutY((containerHeight - itemHeight) / 2);
            overlay.setMouseTransparent(true);
            cardPane.getChildren().add(overlay);

            // 滑鼠滾輪滾動卡片區域
            cardPane.setOnScroll(e -> {
                int delta = e.getDeltaY() < 0 ? 1 : -1;
                snapTo(selectedIndex + delta);
            });

            // 垂直 ScrollBar
            this.scrollBar = new ScrollBar();
            scrollBar.setOrientation(Orientation.VERTICAL);
            scrollBar.setMin(0);
            scrollBar.setMax(Math.max(0, types.length - 1));
            scrollBar.setValue(0);
            scrollBar.setBlockIncrement(1);
            scrollBar.setUnitIncrement(1);
            scrollBar.setPrefHeight(containerHeight);

            scrollBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                int idx = (int) Math.round(newVal.doubleValue());
                if (idx != selectedIndex) {
                    snapTo(idx);
                }
            });

            HBox layout = new HBox(cardPane, scrollBar);
            layout.setPrefSize(280, containerHeight);

            container.getChildren().add(layout);
            snapTo(0);
        }

        private VBox createCard(BallType type) {
            VBox card = new VBox();
            card.getStyleClass().add("ball-card");

            Label title = new Label(type.getDisplayName());
            title.getStyleClass().add("ball-card-title");

            Label desc = new Label(type.getDescription());
            desc.getStyleClass().add("ball-card-desc");
            desc.setWrapText(true);
            desc.setPrefWidth(220);

            card.getChildren().addAll(title, desc);
            return card;
        }

        private void snapTo(int index) {
            selectedIndex = Math.max(0, Math.min(types.length - 1, index));
            double targetY = (containerHeight - itemHeight) / 2 - (selectedIndex * itemHeight);
            content.setLayoutY(targetY);
            // 同步 ScrollBar 位置（避免循環觸發）
            if (Math.abs(scrollBar.getValue() - selectedIndex) > 0.01) {
                scrollBar.setValue(selectedIndex);
            }
        }

        public BallType getSelectedType() {
            return types[selectedIndex];
        }
    }
}
