package BallArena.ability;

import BallArena.model.ArenaConfig;
import BallArena.model.Ball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TurretSkill implements Ability {

    /** 砲台發射的小子彈 */
    public static class TurretBullet {
        public double x, y, vx, vy;
        public double damage;
        public static final double RADIUS = 4; // 子彈大小

        public TurretBullet(double x, double y, double vx, double vy, double damage) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.damage = damage;
        }
    }

    public static class Turret {
        public double x, y;
        public int level = 0;
        public final double radius = 14; // 固定的基座半徑
        public double angle = 0;         // 砲管朝向（弧度）

        public Turret(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void upgrade() {
            if (level < 3) level++;
        }

        // 依據等級決定發射間隔 (秒)
        public double getFireInterval() {
            return switch (level) {
                case 1 -> 1.0;  // 1級：每秒1發
                case 2 -> 0.5;  // 2級：每秒2發
                case 3 -> 0.2;  // 3級：每秒5發 (機槍)
                default -> 999;
            };
        }

        // 依據等級決定單發子彈傷害
        public double getBulletDamage() {
            return switch (level) {
                case 1 -> 1.0;
                case 2 -> 2.0;
                case 3 -> 3.0;
                default -> 0.0;
            };
        }
    }

    private final Ball owner;
    private Ball target; // 鎖定的敵人
    private Turret turret;

    private double timeElapsed = 0;
    private boolean isDeployed = false;
    private double upgradeCooldown = 0;
    private double fireCooldown = 0;

    private final List<TurretBullet> bullets = new ArrayList<>();
    private static final double BULLET_SPEED = 350; // 子彈飛行速度

    public TurretSkill(Ball owner) {
        this.owner = owner;
    }

    /** 告訴砲台要瞄準誰 */
    public void setTarget(Ball target) {
        this.target = target;
    }

    @Override
    public void update(double deltaTime) {
        // 1. 處理開場佈署
        if (!isDeployed) {
            timeElapsed += deltaTime;
            if (timeElapsed >= 2.0) {
                turret = new Turret(owner.getX(), owner.getY());
                isDeployed = true;
            }
        }

        if (upgradeCooldown > 0) upgradeCooldown -= deltaTime;

        // 2. 瞄準與發射
        if (turret != null && turret.level > 0 && target != null) {
            // 計算砲台到敵人的角度
            double dx = target.getX() - turret.x;
            double dy = target.getY() - turret.y;
            turret.angle = Math.atan2(dy, dx); // 更新砲管朝向

            fireCooldown -= deltaTime;
            if (fireCooldown <= 0) {
                // 發射子彈
                double vx = Math.cos(turret.angle) * BULLET_SPEED;
                double vy = Math.sin(turret.angle) * BULLET_SPEED;
                // 子彈從砲管末端生成 (稍微往外推)
                double spawnX = turret.x + Math.cos(turret.angle) * 20;
                double spawnY = turret.y + Math.sin(turret.angle) * 20;

                bullets.add(new TurretBullet(spawnX, spawnY, vx, vy, turret.getBulletDamage()));
                fireCooldown = turret.getFireInterval();
            }
        }

        // 3. 移動子彈，並清除飛出場外的子彈
        Iterator<TurretBullet> it = bullets.iterator();
        while (it.hasNext()) {
            TurretBullet b = it.next();
            b.x += b.vx * deltaTime;
            b.y += b.vy * deltaTime;
            if (b.x < 0 || b.x > ArenaConfig.WIDTH || b.y < 0 || b.y > ArenaConfig.HEIGHT) {
                it.remove();
            }
        }
    }

    @Override
    public void onBounce() {}

    public Turret getTurret() { return turret; }
    public List<TurretBullet> getBullets() { return bullets; }
    public boolean canUpgrade() { return upgradeCooldown <= 0; }
    public void resetUpgradeCooldown() { this.upgradeCooldown = 1.0; }
}