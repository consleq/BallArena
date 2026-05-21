package BallArena.model;

/**
 * 球種列舉（純資料，不依賴 JavaFX）
 * 用於「在還沒建立 Ball 實例前」描述要建立哪種球，例如選球畫面、敵方隨機選擇等。
 * 新增球種時須同步加入此 enum，並在 GameController.createBall() 加上對應的 case。
 */
public enum BallType {
    SWORD("劍球", "一把劍以固定角速度繞球旋轉，命中可造成穩定傷害"),
    SPIKE("尖刺球", "每次碰牆會在牆面生成尖刺，敵方撞到尖端會扣血並反彈"),
    FIRE ("火球",   "鎖定敵方位置發射火球，命中後產生範圍傷害區，每秒持續扣血");

    private final String displayName;
    private final String description;

    BallType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
