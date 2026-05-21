package BallArena.ability;

import BallArena.model.ArenaConfig;
import BallArena.model.Ball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * FireBall 的技能：
 *  - 每隔 FIRE_INTERVAL 秒朝當前目標位置發射火球
 *  - 火球碰到目標或牆壁時，在該位置產生持續 DURATION 秒的圓形範圍傷害區
 *  - 範圍傷害區每秒對範圍內的目標造成 DAMAGE_PER_SEC 點傷害
 */
public class FireSpell implements Ability {

    /** 飛行中的火焰投射物 */
    public static class FireProjectile {
        public static final double RADIUS = 5;
        public double x, y, vx, vy;

        public FireProjectile(double x, double y, double vx, double vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
    }

    /** 爆炸後留下的圓形範圍傷害區 */
    public static class FireZone {
        public static final double RADIUS         = 32;
        public static final double DURATION       = 3.0;
        public static final double DAMAGE_PER_SEC = 4.0;

        public final double x, y;
        public double timeLeft;

        public FireZone(double x, double y) {
            this.x = x;
            this.y = y;
            this.timeLeft = DURATION;
        }

        public boolean isExpired() { return timeLeft <= 0; }
        /** 剩餘比例：1.0 ~ 0.0，用於透明度動畫 */
        public double getProgress() { return Math.max(0, timeLeft / DURATION); }
    }

    private static final double FIRE_INTERVAL    = 1.8; // 發射間隔（秒）
    private static final double PROJECTILE_SPEED = 300; // 投射物速度 px/s
    private static final double INITIAL_DELAY    = 0.8; // 第一發前的延遲

    /** 火球本體（用於取得發射位置）；用 Ball 而非 FireBall 以避免循環相依 */
    private final Ball owner;
    private Ball target;
    private double fireCooldown = INITIAL_DELAY;

    private final List<FireProjectile> projectiles = new ArrayList<>();
    private final List<FireZone>       zones       = new ArrayList<>();

    public FireSpell(Ball owner) {
        this.owner = owner;
    }

    /** 由 FireBall 在遊戲開始時設定，告訴技能要對誰開火 */
    public void setTarget(Ball target) { this.target = target; }

    public List<FireProjectile> getProjectiles() { return projectiles; }
    public List<FireZone>       getZones()       { return zones; }

    @Override
    public void update(double deltaTime) {
        if (target == null) return;

        // 1. 倒數，到時發射
        fireCooldown -= deltaTime;
        if (fireCooldown <= 0) {
            launchProjectile();
            fireCooldown = FIRE_INTERVAL;
        }

        // 2. 移動投射物，檢查碰到目標或牆壁 → 轉成 zone
        Iterator<FireProjectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            FireProjectile p = pit.next();
            p.x += p.vx * deltaTime;
            p.y += p.vy * deltaTime;

            // 碰到目標
            double dx = target.getX() - p.x;
            double dy = target.getY() - p.y;
            double hitR = target.getRadius() + FireProjectile.RADIUS;
            if (dx * dx + dy * dy < hitR * hitR) {
                zones.add(new FireZone(p.x, p.y));
                pit.remove();
                continue;
            }

            // 碰到牆壁（含超出邊界）
            if (p.x < 0 || p.x > ArenaConfig.WIDTH
             || p.y < 0 || p.y > ArenaConfig.HEIGHT) {
                double cx = Math.max(0, Math.min(ArenaConfig.WIDTH,  p.x));
                double cy = Math.max(0, Math.min(ArenaConfig.HEIGHT, p.y));
                zones.add(new FireZone(cx, cy));
                pit.remove();
            }
        }

        // 3. 衰減 zone 壽命，到期移除
        Iterator<FireZone> zit = zones.iterator();
        while (zit.hasNext()) {
            FireZone z = zit.next();
            z.timeLeft -= deltaTime;
            if (z.isExpired()) zit.remove();
        }
    }

    @Override
    public void onBounce() {
        // 火球自身碰牆不影響發射節奏
    }

    /** 朝當前目標方向發射一發投射物 */
    private void launchProjectile() {
        double dx = target.getX() - owner.getX();
        double dy = target.getY() - owner.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.001) return;
        double vx = (dx / dist) * PROJECTILE_SPEED;
        double vy = (dy / dist) * PROJECTILE_SPEED;
        projectiles.add(new FireProjectile(owner.getX(), owner.getY(), vx, vy));
    }
}
