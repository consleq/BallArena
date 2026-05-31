package BallArena.ability;

import BallArena.model.Ball;

/** SwordBall 的技能：一把劍以固定角速度繞球旋轉 */
public class RotatingSword implements Ability {

    private final Ball owner;

    /** 劍的當前角度（弧度） */
    private double angle = 0;

    /** 旋轉角速度（弧度 / 秒） */
    private static final double ANGULAR_SPEED = Math.PI * 1.5;

    /** 劍距球心的距離 */
    public static final double SWORD_DISTANCE = 40;

    /** 劍的長度 */
    public static final double SWORD_LENGTH = 60;

    public RotatingSword(Ball owner) {
        this.owner = owner;
    }

    @Override
    public void update(double deltaTime) {
        angle += ANGULAR_SPEED * deltaTime;
    }

    @Override
    public void onBounce() {
        // 目前碰牆不影響劍，預留給未來功能
    }

    /** 劍的起點 X（球心往外 SWORD_DISTANCE） */
    public double getSwordStartX() {
        return owner.getX() + Math.cos(angle) * SWORD_DISTANCE;
    }

    public double getSwordStartY() {
        return owner.getY() + Math.sin(angle) * SWORD_DISTANCE;
    }

    /** 劍的終點 X */
    public double getSwordEndX() {
        return owner.getX() + Math.cos(angle) * (SWORD_DISTANCE + SWORD_LENGTH);
    }

    public double getSwordEndY() {
        return owner.getY() + Math.sin(angle) * (SWORD_DISTANCE + SWORD_LENGTH);
    }

    public double getAngle() { return angle; }
}