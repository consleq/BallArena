package BallArena.ability;

import BallArena.model.Ball;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * VampireBall 的技能：吸血光環
 * - 對範圍內的敵人造成持續傷害
 * - 將造成的傷害轉化為自身的生命值
 */
public class VampireAbility implements Ability {

    private final Ball owner;
    private Ball target;

    public static final double TETHER_RANGE = 160;
    public static final double TICK_INTERVAL = 1.0;
    public static final double DAMAGE_PER_TICK = 4.0;
    public static final double HEAL_PER_3_TICKS = 5.0;

    private double tickTimer = 0;
    private int tickCount = 0;
    private boolean isTethered = false;
    private final List<Double> pendingDamageHits = new ArrayList<>();
    private final List<Double> pendingHealHits   = new ArrayList<>();

    public VampireAbility(Ball owner) {
        this.owner = owner;
    }

    public void setTarget(Ball target) {
        this.target = target;
    }

    public List<Double> drainDamageHits() {
        if (pendingDamageHits.isEmpty()) return Collections.emptyList();
        List<Double> copy = new ArrayList<>(pendingDamageHits);
        pendingDamageHits.clear();
        return copy;
    }

    public List<Double> drainHealHits() {
        if (pendingHealHits.isEmpty()) return Collections.emptyList();
        List<Double> copy = new ArrayList<>(pendingHealHits);
        pendingHealHits.clear();
        return copy;
    }

    @Override
    public void update(double deltaTime) {
        if (target == null || target.isDead()) {
            isTethered = false;
            return;
        }

        double dx = target.getX() - owner.getX();
        double dy = target.getY() - owner.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        // 判斷是否在連結範圍內
        if (dist < TETHER_RANGE) {
            isTethered = true;
            tickTimer += deltaTime;

            if (tickTimer >= TICK_INTERVAL) {
                tickTimer -= TICK_INTERVAL;
                tickCount++;

                // 造成傷害
                target.takeDamage(DAMAGE_PER_TICK);
                pendingDamageHits.add(DAMAGE_PER_TICK);

                // 每累積滿三次傷害回復5血 (不需連續)
                if (tickCount >= 3) {
                    owner.heal(HEAL_PER_3_TICKS);
                    pendingHealHits.add(HEAL_PER_3_TICKS);
                    tickCount = 0;
                }
            }
        } else {
            isTethered = false;
            tickTimer = 0;
            // tickCount 不在此重置，實現「累積」而非「連續」
        }
    }

    public boolean isTethered() { return isTethered; }
    public Ball getTarget() { return target; }

    @Override
    public void onBounce() {
        // 碰牆無特殊效果
    }
    
    public Ball getOwner() { return owner; }
}
