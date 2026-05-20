package BallArena.model;

import BallArena.ability.WallSpike;

/** 碰牆後產生尖刺的球 */
public class SpikeBall extends Ball {

    private final WallSpike wallSpike;

    public SpikeBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
        this.wallSpike = new WallSpike();
    }

    @Override
    public void updateAbility(double deltaTime) {
        wallSpike.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        wallSpike.onBounce();
    }

    @Override
    public String getTypeName() { return "尖刺球"; }

    public void addSpike(double x, double y, double angle) {
        wallSpike.addSpike(x, y, angle);
    }

    public WallSpike getWallSpike() { return wallSpike; }
}