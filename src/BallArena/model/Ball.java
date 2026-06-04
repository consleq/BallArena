package BallArena.model;

/** 所有球種的抽象基底類別（不依賴 JavaFX） */
public abstract class Ball {

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected double hp;
    protected double maxHp;
    protected final double radius = ArenaConfig.BALL_RADIUS;

    /** 球的視覺風格（由子類別決定） */
    protected final BallStyle style;

    public Ball(double x, double y, double hp, BallStyle style) {
        this.x     = x;
        this.y     = y;
        this.hp    = hp;
        this.maxHp = hp;
        this.style = style;
    }

    public abstract void updateAbility(double deltaTime);
    public abstract void onBounce(boolean hitVertical, boolean hitHorizontal);

    /** 子類別必須提供球種的中文名稱（顯示在結算畫面） */
    public abstract String getTypeName();

    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getVx()     { return vx; }
    public double getVy()     { return vy; }
    public double getHp()     { return hp; }
    public double getRadius() { return radius; }
    public BallStyle getStyle() { return style; }

    public void setX(double x)   { this.x = x; }
    public void setY(double y)   { this.y = y; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }

    /**
     * 對球造成傷害.
     * @return 實際扣除的血量
     */
    public double takeDamage(double dmg) {
        double oldHp = hp;
        hp = Math.max(0, hp - dmg);
        return oldHp - hp;
    }

    public void heal(double amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public boolean isDead() {
        return hp <= 0;
    }


    public static final double SLOW_FACTOR = 0.5;
    private static final double SLOW_DURATION = 0.5;
    private double slowTimer = 0;

    public boolean isSlowed() { return slowTimer > 0; }

    /** 目前應有的速度大小（緩速中為基準速度的一半），供撞球後正規化使用 */
    public double targetSpeed() {
        return ArenaConfig.BALL_SPEED * (isSlowed() ? SLOW_FACTOR : 1.0);
    }

    /**
     * 套用緩速：直接把速度大小設為目標速度並重置計時器。
     * 採絕對設定而非相對折半，避免與撞球時的速度正規化互相干擾、導致還原後速度爆掉。
     */
    public void slowDown() {
        slowTimer = SLOW_DURATION;
        setSpeed(targetSpeed());
    }

    /** 每幀由 GameController 呼叫，計時結束後把速度還原為基準速度 */
    public void tickSlowTimer(double deltaTime) {
        if (slowTimer > 0) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0) {
                slowTimer = 0;
                setSpeed(targetSpeed());
            }
        }
    }

    /** 保持方向不變，將速度向量縮放到指定大小 */
    private void setSpeed(double speed) {
        double mag = Math.sqrt(vx * vx + vy * vy);
        if (mag < 1e-9) return;
        vx = vx / mag * speed;
        vy = vy / mag * speed;
    }
}