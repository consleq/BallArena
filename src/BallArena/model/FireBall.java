package BallArena.model;

import BallArena.ability.FireSpell;

/**
 * 火球：自動鎖定敵方位置、定時發射投射物，
 * 投射物碰到目標或牆壁時產生圓形範圍傷害區（持續 3 秒、每秒 4 點傷害）
 */
public class FireBall extends Ball {

    private final FireSpell fireSpell;

    public FireBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
        this.fireSpell = new FireSpell(this);
    }

    /** 由 GameController 在建好雙方球後設定，告訴 FireSpell 要對誰開火 */
    public void setTarget(Ball target) {
        fireSpell.setTarget(target);
    }

    @Override
    public void updateAbility(double deltaTime) {
        fireSpell.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        fireSpell.onBounce();
    }

    @Override
    public String getTypeName() { return "火球"; }

    public FireSpell getFireSpell() { return fireSpell; }

}
