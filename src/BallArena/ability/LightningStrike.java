package BallArena.ability;

import BallArena.model.Ball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * LightningBall 的技能：
 *  - 每隔 STRIKE_INTERVAL 秒鎖定敵方「當下位置」瞬間放電（hitscan，必中）
 *  - 命中固定扣 STRIKE_DAMAGE 點傷害
 *  - 閃電本身為瞬間命中，畫面上以鋸齒折線殘留短暫視覺特效後淡出
 */
public class LightningStrike implements Ability {

    /** 一道閃電的視覺特效：鋸齒折線 + 殘留時間 */
    public static class Bolt {
        public static final double DURATION = 0.28;

        public final double[] xs;
        public final double[] ys;
        public double timeLeft;

        public Bolt(double[] xs, double[] ys) {
            this.xs = xs;
            this.ys = ys;
            this.timeLeft = DURATION;
        }

        public boolean isExpired() { return timeLeft <= 0; }
        /** 剩餘比例 1.0 ~ 0.0，用於透明度淡出 */
        public double getProgress() { return Math.max(0, timeLeft / DURATION); }
    }

    private static final double STRIKE_INTERVAL = 2.0;
    private static final double INITIAL_DELAY   = 1.0;
    private static final double STRIKE_DAMAGE   = 4.0;
    private static final int    SEGMENTS        = 10;   // 鋸齒段數
    private static final double JAGGED_AMP       = 14;  // 鋸齒最大橫向偏移

    private static final Random RNG = new Random();

    /** 用 Ball 而非 LightningBall 以避免循環相依 */
    private final Ball owner;
    private Ball target;
    private double strikeCooldown = INITIAL_DELAY;

    private final List<Bolt> bolts = new ArrayList<>();

    /** 本幀放電造成的扣血事件，由 GameController 取走顯示 popup */
    private final List<Double> pendingDamageHits = new ArrayList<>();

    public LightningStrike(Ball owner) {
        this.owner = owner;
    }

    public void setTarget(Ball target) { this.target = target; }
    public List<Bolt> getBolts() { return bolts; }

    /** 取走所有待處理的扣血事件（顯示 popup 後清空） */
    public List<Double> drainDamageHits() {
        if (pendingDamageHits.isEmpty()) return Collections.emptyList();
        List<Double> copy = new ArrayList<>(pendingDamageHits);
        pendingDamageHits.clear();
        return copy;
    }

    @Override
    public void update(double deltaTime) {
        // 衰減既有閃電視覺壽命
        Iterator<Bolt> it = bolts.iterator();
        while (it.hasNext()) {
            Bolt b = it.next();
            b.timeLeft -= deltaTime;
            if (b.isExpired()) it.remove();
        }

        if (target == null || target.isDead()) return;

        // 放電倒數
        strikeCooldown -= deltaTime;
        if (strikeCooldown <= 0) {
            strike();
            strikeCooldown = STRIKE_INTERVAL;
        }
    }

    @Override
    public void onBounce() {
        // 自身碰牆不影響放電節奏
    }

    /** 朝目標當下位置瞬間放電：必中扣血 + 產生鋸齒視覺 */
    private void strike() {
        double ox = owner.getX(), oy = owner.getY();
        double tx = target.getX(), ty = target.getY();

        // 必中扣血
        target.takeDamage(STRIKE_DAMAGE);
        pendingDamageHits.add(STRIKE_DAMAGE);

        // 產生鋸齒折線：兩端錨定球心與目標，中段隨機橫向偏移
        double dx = tx - ox, dy = ty - oy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        // 單位法線方向（垂直於連線），用來做橫向抖動
        double nx = 0, ny = 0;
        if (dist > 0.001) { nx = -dy / dist; ny = dx / dist; }

        double[] xs = new double[SEGMENTS + 1];
        double[] ys = new double[SEGMENTS + 1];
        for (int i = 0; i <= SEGMENTS; i++) {
            double t = (double) i / SEGMENTS;
            double baseX = ox + dx * t;
            double baseY = oy + dy * t;
            // 端點偏移為 0，中段最大：用 sin 包絡
            double envelope = Math.sin(Math.PI * t);
            double offset = (RNG.nextDouble() * 2 - 1) * JAGGED_AMP * envelope;
            xs[i] = baseX + nx * offset;
            ys[i] = baseY + ny * offset;
        }
        bolts.add(new Bolt(xs, ys));
    }
}
