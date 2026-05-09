package BallArena.model;

/** 所有球種的抽象基底類別（不依賴 JavaFX） */
public abstract class Ball {

    // 位置
    protected double x;
    protected double y;

    // 速度（每幀移動量）
    protected double vx;
    protected double vy;

    // 血量
    protected double hp;
    protected double maxHp;

    // 球的半徑（從 ArenaConfig 讀取）
    protected final double radius = ArenaConfig.BALL_RADIUS;

    public Ball(double x, double y, double hp) {
        this.x     = x;
        this.y     = y;
        this.hp    = hp;
        this.maxHp = hp;
    }

    /** 子類別必須實作：每幀更新技能邏輯 */
    public abstract void updateAbility(double deltaTime);

    /** 子類別必須實作：碰到牆壁時觸發 */
    public abstract void onBounce(boolean hitVertical, boolean hitHorizontal);

    // ── Getters / Setters ──────────────────────────────────────────────

    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getVx()     { return vx; }
    public double getVy()     { return vy; }
    public double getHp()     { return hp; }
    public double getMaxHp()  { return maxHp; }
    public double getRadius() { return radius; }

    public void setX(double x)   { this.x = x; }
    public void setY(double y)   { this.y = y; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }

    public void takeDamage(double dmg) {
        hp = Math.max(0, hp - dmg);
    }

    public boolean isDead() {
        return hp <= 0;
    }
}