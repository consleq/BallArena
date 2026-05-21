package BallArena.renderer;

/** 單一扣血浮動文字的資料（不含繪製邏輯） */
public class DamagePopup {

    public double x;
    public double y;
    public final String text;
    public double timeLeft;
    public final double totalTime;

    private static final double DURATION = 1.0; // 存活秒數

    public DamagePopup(double x, double y, double damage) {
        this.x         = x;
        this.y         = y;
        this.text      = "-" + (int) damage;
        this.timeLeft  = DURATION;
        this.totalTime = DURATION;
    }

    public boolean isExpired() { return timeLeft <= 0; }

    /** 透明度：剛出現時 1.0，消失前趨近 0.0 */
    public double getAlpha() { return timeLeft / totalTime; }
}
