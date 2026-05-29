package BallArena.model;

public enum BallType {
    SWORD("劍球", "一把劍以固定角速度繞球旋轉，命中可造成穩定傷害"),
    SPIKE("尖刺球", "每次碰牆會在牆面生成尖刺，敵方撞到尖端會扣血並反彈"),
    FIRE ("火球", "鎖定敵方位置發射火球，命中後產生範圍傷害區，每秒持續扣血"),
    ENGINEER("工程師", "開場2秒放置砲台，本體撞擊砲台可使其升級"),
    VAMPIRE ("吸血球", "擁有吸血光環，對範圍內敵人造成持續傷害並治癒自身");

    private final String displayName;
    private final String description;

    BallType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}