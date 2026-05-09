package BallArena.model;

import BallArena.ability.RotatingSword;

/** 帶著旋轉劍的球 */
public class SwordBall extends Ball {

    private final RotatingSword sword;

    public SwordBall(double x, double y) {
        super(x, y, 100);
        this.sword = new RotatingSword(this);
    }

    @Override
    public void updateAbility(double deltaTime) {
        sword.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        sword.onBounce();
    }

    public RotatingSword getSword() { return sword; }
}