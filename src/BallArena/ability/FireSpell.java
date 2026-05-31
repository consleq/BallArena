package BallArena.ability;

import BallArena.model.ArenaConfig;
import BallArena.model.Ball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * FireBall 的技能：
 * pushtest
 *  - 每隔 FIRE_INTERVAL 秒朝當前目標位置發射火球
 *  - 火球碰到目標 → 直接扣 EXPLOSION_DAMAGE 點傷害
 *  - 火球碰到牆壁 → 在落點產生一次性爆炸，目標若在範圍內則扣 EXPLOSION_DAMAGE 點傷害
 *  - 爆炸不持續傷害，僅留下短暫視覺特效
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

    /** 爆炸特效（純視覺，不再持續造成傷害） */
    public static class FireZone {
        public static final double RADIUS         = 60;
        public static final double VISUAL_DURATION = 0.5;
        public static final double DAMAGE_EXPLOSION = 5.0;
        public static final double DAMAGE_DIRECT    = 8.0;

        public final double x, y;
        public double timeLeft;

        public FireZone(double x, double y) {
            this.x = x;
            this.y = y;
            this.timeLeft = VISUAL_DURATION;
        }

        public boolean isExpired() { return timeLeft <= 0; }
        /** 剩餘比例：1.0 ~ 0.0，用於透明度動畫 */
        public double getProgress() { return Math.max(0, timeLeft / VISUAL_DURATION); }
    }

    private static final double FIRE_INTERVAL    = 1.8;
    private static final double PROJECTILE_SPEED = 350;
    private static final double INITIAL_DELAY    = 0.8;

    /** 用 Ball 而非 FireBall 以避免循環相依 */
    private final Ball owner;
    private Ball target;
    private double fireCooldown = INITIAL_DELAY;

    private final List<FireProjectile> projectiles = new ArrayList<>();
    private final List<FireZone>       zones       = new ArrayList<>();

    /** 本幀爆炸後實際扣到血的事件，由 GameController 取走以顯示 popup */
    private final List<Double> pendingDamageHits = new ArrayList<>();

    public FireSpell(Ball owner) {
        this.owner = owner;
    }

    public void setTarget(Ball target) { this.target = target; }
    public List<FireProjectile> getProjectiles() { return projectiles; }
    public List<FireZone>       getZones()       { return zones; }

    /** 取走所有待處理的扣血事件（讓 popup 顯示），呼叫後清空 */
    public List<Double> drainDamageHits() {
        if (pendingDamageHits.isEmpty()) return Collections.emptyList();
        List<Double> copy = new ArrayList<>(pendingDamageHits);
        pendingDamageHits.clear();
        return copy;
    }

    @Override
    public void update(double deltaTime) {
        if (target == null) return;

        // 1. 發射倒數
        fireCooldown -= deltaTime;
        if (fireCooldown <= 0) {
            launchProjectile();
            fireCooldown = FIRE_INTERVAL;
        }

        // 2. 移動投射物，檢查碰撞
        Iterator<FireProjectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            FireProjectile p = pit.next();
            p.x += p.vx * deltaTime;
            p.y += p.vy * deltaTime;

            // 直接命中目標 → 必定扣血
            double dx = target.getX() - p.x;
            double dy = target.getY() - p.y;
            double hitR = target.getRadius() + FireProjectile.RADIUS;
            if (dx * dx + dy * dy < hitR * hitR) {
                triggerExplosion(p.x, p.y, true);
                pit.remove();
                continue;
            }

            // 碰到牆壁 → 在落點爆炸，目標若在範圍內才扣血
            if (p.x < 0 || p.x > ArenaConfig.WIDTH
             || p.y < 0 || p.y > ArenaConfig.HEIGHT) {
                double cx = Math.max(0, Math.min(ArenaConfig.WIDTH,  p.x));
                double cy = Math.max(0, Math.min(ArenaConfig.HEIGHT, p.y));
                triggerExplosion(cx, cy, false);
                pit.remove();
            }
        }

        // 3. 衰減爆炸特效壽命
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

    /**
     * 觸發爆炸：產生視覺特效，並決定是否扣血
     * @param directHit true=投射物直接命中目標，必定扣血；false=碰牆，需檢查目標是否在範圍內
     */
    private void triggerExplosion(double x, double y, boolean directHit) {
        zones.add(new FireZone(x, y));

        double damageApplied = 0;
        if (directHit) {
            // 直接命中：扣 8 血
            damageApplied = FireZone.DAMAGE_DIRECT;
        } else {
            // 牆壁爆炸：檢查目標是否在爆炸半徑 + 球半徑範圍內，命中扣 5 血
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double r  = FireZone.RADIUS + target.getRadius();
            if (dx * dx + dy * dy < r * r) {
                damageApplied = FireZone.DAMAGE_EXPLOSION;
            }
        }

        if (damageApplied > 0) {
            target.takeDamage(damageApplied);
            pendingDamageHits.add(damageApplied);
        }
    }
}
