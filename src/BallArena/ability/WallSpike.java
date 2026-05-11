package BallArena.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** SpikeBall 的技能：碰牆後在碰撞點產生尖刺，存在一段時間後消失 */
public class WallSpike implements Ability {

    /** 尖刺資料（純資料，不含 JavaFX） */
    public static class Spike {
        public double x, y;

        public Spike(double x, double y) {
            this.x        = x;
            this.y        = y;
        }
    }

    private final List<Spike> spikes = new ArrayList<>();


    @Override
    public void update(double deltaTime) {
        // spike lasts forever until game ends
    }

    @Override
    public void onBounce() {
        // 實際位置由 SpikeBall.addSpike() 傳入，這裡留空
    }

    /** 在指定座標新增一根尖刺 */
    public void addSpike(double x, double y) {
        spikes.add(new Spike(x, y));
    }

    public void clearSpikes() {
        spikes.clear();
    }

    public List<Spike> getSpikes() { return spikes; }
}