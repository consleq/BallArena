package BallArena.model;

public enum BallType {
    SWORD("劍球", "一把劍以固定角速度繞球旋轉，命中可造成穩定傷害"),
    SPIKE("尖刺球", "每次碰牆會在牆面生成尖刺，敵方撞到尖端會扣血並反彈"),
    FIRE ("火球", "鎖定敵方位置發射火球，命中或牆壁爆炸造成範圍傷害"),
    ENGINEER("工程師", "開場2秒放置砲台，本體撞擊砲台可使其升級"),
    VAMPIRE ("吸血球", "擁有吸血光環，造成持續傷害並週期性回復生命"),
    MYSTERY ("神秘球", "受傷時50%機率免疫。每2秒80%機率造成隨機傷害(1,3,5,10)，20%機率幫敵人補5滴血");

    private final String displayName;
    private final String description;

    BallType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}