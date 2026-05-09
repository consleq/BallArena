package BallArena.model;

import BallArena.ability.WallSpike;

/** 碰牆後產生尖刺的球 */
public class SpikeBall extends Ball {

    private final WallSpike wallSpike;

    public SpikeBall(double x, double y) {
        super(x, y, 100);
        this.wallSpike = new WallSpike();
    }

    @Override
    public void updateAbility(double deltaTime) {
        wallSpike.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        // 在碰撞點新增尖刺（位置由 PhysicsEngine 傳入後呼叫 addSpike）
        wallSpike.onBounce();
    }

    /** 在指定位置產生一根尖刺 */
    public void addSpike(double x, double y) {
        wallSpike.addSpike(x, y);
    }

    public WallSpike getWallSpike() { return wallSpike; }
}