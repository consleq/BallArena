package BallArena.model;

import BallArena.ability.TurretSkill;

public class EngineerBall extends Ball {

    private final TurretSkill turretSkill;

    public EngineerBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
        this.turretSkill = new TurretSkill(this);
    }

    @Override
    public void updateAbility(double deltaTime) {
        turretSkill.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        turretSkill.onBounce();
    }

    @Override
    public String getTypeName() { return "工程師"; }

    public TurretSkill getTurretSkill() { return turretSkill; }

    public void setTarget(Ball target) {
        turretSkill.setTarget(target);
    }
}