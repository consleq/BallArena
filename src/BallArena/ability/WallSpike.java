package BallArena.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** SpikeBall 的技能：碰牆後在碰撞點產生尖刺，存在一段時間後消失 */
public class WallSpike implements Ability {

    /** 尖刺長度（從基底到尖端，像素） */
    public static final double SPIKE_LENGTH = 14;

    /** 尖刺資料（純資料，不含 JavaFX）。(x, y) 是貼在牆上的基底中心點 */
    public static class Spike {
        public double x, y;
        /** 旋轉角度（度，順時針）：0=朝上(下牆), 90=朝右(左牆), 180=朝下(上牆), 270=朝左(右牆) */
        public double angle;

        public Spike(double x, double y, double angle) {
            this.x     = x;
            this.y     = y;
            this.angle = angle;
        }

        /** 尖刺尖端的 X 座標 */
        public double getTipX() {
            return x + SPIKE_LENGTH * Math.sin(Math.toRadians(angle));
        }

        /** 尖刺尖端的 Y 座標 */
        public double getTipY() {
            return y - SPIKE_LENGTH * Math.cos(Math.toRadians(angle));
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

    /** 在指定座標新增一根尖刺，angle 為旋轉角度（度，順時針） */
    public void addSpike(double x, double y, double angle) {
        spikes.add(new Spike(x, y, angle));
    }

    public void clearSpikes() {
        spikes.clear();
    }

    public List<Spike> getSpikes() { return spikes; }
}