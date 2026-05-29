package BallArena.model;

import BallArena.ability.VampireAbility;

/** 吸血球：擁有吸血光環 */
public class VampireBall extends Ball {

    private final VampireAbility vampireAbility;

    public VampireBall(double x, double y, BallStyle style) {
        // 吸血球血量稍微低一點作為平衡？或者標準 100
        super(x, y, 100, style);
        this.vampireAbility = new VampireAbility(this);
    }

    @Override
    public void updateAbility(double deltaTime) {
        vampireAbility.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        vampireAbility.onBounce();
    }

    @Override
    public String getTypeName() { return "吸血球"; }

    public VampireAbility getVampireAbility() { return vampireAbility; }
    
    public void setTarget(Ball target) {
        vampireAbility.setTarget(target);
    }
}
