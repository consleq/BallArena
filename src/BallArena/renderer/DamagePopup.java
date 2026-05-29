package BallArena.renderer;

import javafx.scene.paint.Color;

/** 單一扣血浮動文字的資料（不含繪製邏輯） */
public class DamagePopup {

    public double x;
    public double y;
    public final String text;
    public double timeLeft;
    public final double totalTime;
    public final Color color;

    private static final double DURATION = 1.0; // 存活秒數

    public DamagePopup(double x, double y, double damage) {
        this(x, y, "-" + (int) damage, Color.color(1.0, 0.25, 0.25));
    }

    public DamagePopup(double x, double y, String text, Color color) {
        this.x         = x;
        this.y         = y;
        this.text      = text;
        this.color     = color;
        this.timeLeft  = DURATION;
        this.totalTime = DURATION;
    }

    public boolean isExpired() { return timeLeft <= 0; }

    /** 透明度：剛出現時 1.0，消失前趨近 0.0 */
    public double getAlpha() { return timeLeft / totalTime; }
}
