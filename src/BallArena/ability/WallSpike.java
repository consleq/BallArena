package BallArena.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** SpikeBall 的技能：碰牆後在碰撞點產生尖刺，存在一段時間後消失 */
public class WallSpike implements Ability {

    /** 尖刺資料（純資料，不含 JavaFX） */
    public static class Spike {
        public double x, y;
        public double timeLeft; // 剩餘存活秒數

        public Spike(double x, double y, double duration) {
            this.x        = x;
            this.y        = y;
            this.timeLeft = duration;
        }
    }

    private final List<Spike> spikes = new ArrayList<>();

    /** 每根尖刺的存活時間（秒） */
    private static final double SPIKE_DURATION = 5.0;

    @Override
    public void update(double deltaTime) {
        // 每幀倒數所有尖刺的存活時間，移除過期的
        Iterator<Spike> it = spikes.iterator();
        while (it.hasNext()) {
            Spike s = it.next();
            s.timeLeft -= deltaTime;
            if (s.timeLeft <= 0) it.remove();
        }
    }

    @Override
    public void onBounce() {
        // 實際位置由 SpikeBall.addSpike() 傳入，這裡留空
    }

    /** 在指定座標新增一根尖刺 */
    public void addSpike(double x, double y) {
        spikes.add(new Spike(x, y, SPIKE_DURATION));
    }

    public List<Spike> getSpikes() { return spikes; }
}