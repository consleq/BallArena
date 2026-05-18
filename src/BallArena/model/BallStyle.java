package BallArena.model;

/**
 * 球的視覺風格與顯示名稱（model 層只存抽象概念，不依賴 JavaFX）
 * 實際的顏色由 renderer 層的 BallColorMap 對應
 */
public enum BallStyle {
    BLUE("藍球"),
    RED ("紅球");

    private final String displayName;

    BallStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}