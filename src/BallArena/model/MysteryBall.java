package BallArena.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 神秘球：
 * - 受到傷害時有 50% 機率免疫 (顯示白色數字)。
 * - 每 2 秒觸發一次神秘效果 (驚嘆號表示傷害，問號表示幫敵方回血)。
 * - 攻擊時 (2秒一次) 有 80% 機率造成 1, 3, 5, 10 傷害之一。
 * - 有 20% 機率幫敵人回復 5 血。
 */
public class MysteryBall extends Ball {

    private static final Random RNG = new Random();
    private final List<Double> pendingDamageHits = new ArrayList<>();
    private final List<Double> pendingHealHits   = new ArrayList<>();
    private final List<Double> pendingMissHits   = new ArrayList<>();
    
    private double attackTimer = 0;
    private static final double ATTACK_INTERVAL = 2.0;
    private Ball target;
    private boolean triggerSymbolDmg = false;
    private boolean triggerSymbolHeal = false;

    public MysteryBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
    }

    public void setTarget(Ball target) {
        this.target = target;
    }

    @Override
    public void updateAbility(double deltaTime) {
        if (target == null || target.isDead()) return;

        attackTimer += deltaTime;
        if (attackTimer >= ATTACK_INTERVAL) {
            attackTimer = 0;
            triggerAttack(target);
        }
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        // 碰牆無效果
    }

    @Override
    public String getTypeName() { return "神秘球"; }

    @Override
    public double takeDamage(double dmg) {
        // 50% 機率免疫傷害
        if (RNG.nextDouble() < 0.5) {
            pendingMissHits.add(dmg);
            return 0;
        }
        return super.takeDamage(dmg);
    }

    /** 觸發神秘攻擊效果 */
    private void triggerAttack(Ball target) {
        if (RNG.nextDouble() < 0.8) {
            // 80% 造成 1, 3, 5, 10 傷害
            double[] pool = {1, 3, 5, 10};
            double dmg = pool[RNG.nextInt(pool.length)];
            double actualDmg = target.takeDamage(dmg);
            // 即使被對方免疫，也要顯示驚嘆號，但這裡我們只記錄有造成傷害的 popup
            if (actualDmg >= 0) { // 包括 0 (被免疫)
                pendingDamageHits.add(actualDmg);
            }
            triggerSymbolDmg = true;
        } else {
            // 20% 幫敵人補 5 血
            target.heal(5);
            pendingHealHits.add(5.0);
            triggerSymbolHeal = true;
        }
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

    public List<Double> drainMissHits() {
        if (pendingMissHits.isEmpty()) return Collections.emptyList();
        List<Double> copy = new ArrayList<>(pendingMissHits);
        pendingMissHits.clear();
        return copy;
    }

    public boolean checkAndResetSymbolDmg() {
        boolean val = triggerSymbolDmg;
        triggerSymbolDmg = false;
        return val;
    }

    public boolean checkAndResetSymbolHeal() {
        boolean val = triggerSymbolHeal;
        triggerSymbolHeal = false;
        return val;
    }
}
