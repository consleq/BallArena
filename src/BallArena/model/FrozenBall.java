package BallArena.model;

import BallArena.ability.FrozenSpell;


/**
 * 火球：自動鎖定敵方位置、定時發射投射物，
 * 投射物碰到目標或牆壁時產生圓形範圍傷害區（持續 3 秒、每秒 4 點傷害）
 */
public class FrozenBall extends Ball {

    private final FrozenSpell frozenSpell;

    public FrozenBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
        this.frozenSpell = new FrozenSpell(this);
    }

    /** 由 GameController 在建好雙方球後設定，告訴 FireSpell 要對誰開火 */
    public void setTarget(Ball target) {
        frozenSpell.setTarget(target);
    }

    @Override
    public void updateAbility(double deltaTime) {
        frozenSpell.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        frozenSpell.onBounce();
    }

    @Override
    public String getTypeName() { return "冰霜球"; }

    public FrozenSpell getFrozenSpell() { return frozenSpell; }
}
