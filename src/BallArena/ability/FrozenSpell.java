package BallArena.ability;

import BallArena.model.ArenaConfig;
import BallArena.model.Ball;
import BallArena.ui.GameController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * Fi0reBall 的技能：
 * pushtest
 *  - 每隔 FI0RE_INTERVAL 秒朝當前目標位置發射火球
 *  - 火球碰到目標 → 直接扣 EXPLOSION_DAMAGE 點傷害
 *  - 火球碰到牆壁 → 在落點產生一次性爆炸，目標若在範圍內則扣 EXPLOSION_DAMAGE 點傷害
 *  - 爆炸不持續傷害，僅留下短暫視覺特效
 */
public class FrozenSpell implements Ability {

    /** 飛行中的火焰投射物 */
    public static class FrozenProjectile {
        public static final double RADIUS = 5;
        public double x, y, vx, vy;

        public FrozenProjectile(double x, double y, double vx, double vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
    }

    /** 爆炸特效（純視覺，不再持續造成傷害） */
    public static class FrozenZone {
        public static final double RADIUS         = 90;
        public static final double VISUAL_DURATION = 1.5;
        public static final double DAMAGE_EXPLOSION = 2.0;
        public static final double DAMAGE_DIRECT    = 4.0;
        public static final double FROZEN_DAMAGE = 1.0;


        public final double x, y;
        public double timeLeft;

        public FrozenZone(double x, double y) {
            this.x = x;
            this.y = y;
            this.timeLeft = VISUAL_DURATION;
        }

        public boolean isExpired() { return timeLeft <= 0; }
        /** 剩餘比例：1.0 ~ 0.0，用於透明度動畫 */
        public double getProgress() { return Math.max(0, timeLeft / VISUAL_DURATION); }
    }

    private static final double FROZEN_INTERVAL    = 1.8;
    private static final double PROJECTILE_SPEED = 350;
    private static final double INITIAL_DELAY    = 1.1;

    /** 用 Ball 而非 Ball 以避免循環相依 */
    private final Ball owner;
    private Ball target;
    private double frozenCooldown = INITIAL_DELAY;

    private final List<FrozenProjectile> projectiles = new ArrayList<>();
    private final List<FrozenZone>       zones       = new ArrayList<>();

    /** 本幀爆炸後實際扣到血的事件，由 GameController 取走以顯示 popup */
    private final List<Double> pendingDamageHits = new ArrayList<>();

    public FrozenSpell(Ball owner) {
        this.owner = owner;
    }

    public void setTarget(Ball target) { this.target = target; }
    public List<FrozenProjectile> getProjectiles() { return projectiles; }
    public List<FrozenZone>       getZones()       { return zones; }

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
        frozenCooldown -= deltaTime;
        if (frozenCooldown <= 0) {
            launchProjectile();
            frozenCooldown = FROZEN_INTERVAL;
        }

        // 2. 移動投射物，檢查碰撞
        Iterator<FrozenProjectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            FrozenProjectile p = pit.next();
            p.x += p.vx * deltaTime;
            p.y += p.vy * deltaTime;

            // 直接命中目標 → 必定扣血
            double dx = target.getX() - p.x;
            double dy = target.getY() - p.y;
            double hitR = target.getRadius() + FrozenProjectile.RADIUS;
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
        Iterator<FrozenZone> zit = zones.iterator();
        while (zit.hasNext()) {
            FrozenZone z = zit.next();
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
        projectiles.add(new FrozenProjectile(owner.getX(), owner.getY(), vx, vy));
    }

    /**
     * 觸發爆炸：產生視覺特效，並決定是否扣血
     * @param directHit true=投射物直接命中目標，必定扣血；false=碰牆，需檢查目標是否在範圍內
     */
    private void triggerExplosion(double x, double y, boolean directHit) {
        zones.add(new FrozenZone(x, y));

        double damageApplied = 0;
        if (directHit) {
            // 直接命中：扣 4 血
            damageApplied = FrozenZone.DAMAGE_DIRECT;
            target.slowDown();
        } else {
            // 牆壁爆炸：檢查目標是否在爆炸半徑 + 球半徑範圍內，命中扣 5 血
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double r  = FrozenZone.RADIUS + target.getRadius();
            if (dx * dx + dy * dy < r * r) {
                damageApplied = FrozenZone.DAMAGE_EXPLOSION;
                target.slowDown();
            }
        }

        if (damageApplied > 0) {
            target.takeDamage(damageApplied);
            pendingDamageHits.add(damageApplied);
        }

        Timeline FrozenDamageTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0.375), e -> {

                    double dx = target.getX() - x;
                    double dy = target.getY() - y;
                    double r  = FrozenZone.RADIUS + target.getRadius();

                    if (dx * dx + dy * dy < r * r) {
                        target.takeDamage(FrozenZone.FROZEN_DAMAGE);
                        pendingDamageHits.add(FrozenZone.FROZEN_DAMAGE);

                    }

                })
        );

        FrozenDamageTimeLine.setCycleCount(4);
        FrozenDamageTimeLine.play();

        Timeline SlowTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {

                    double dx = target.getX() - x;
                    double dy = target.getY() - y;
                    double r  = FrozenZone.RADIUS + target.getRadius();

                    if (dx * dx + dy * dy < r * r) {
                        target.slowDown();
                    }

                })
        );

        SlowTimeLine.setCycleCount(3);
        SlowTimeLine.play();
    }

        }

