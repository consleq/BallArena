package BallArena.renderer;

import BallArena.model.ArenaConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 管理並繪製所有扣血浮動文字。
 *
 * 字體設定：將 PressStart2P-Regular.ttf 放入
 * resources/BallArena/fonts/ 即可自動載入 Arcade 風格。
 * 字體下載：https://fonts.google.com/specimen/Press+Start+2P
 * 若未放置字體檔，自動退回等寬字體。
 */
public class PopupRenderer {

    /** 每秒向上飄移的像素數 */
    private static final double FLOAT_SPEED = 40.0;

    private final List<DamagePopup> popups = new ArrayList<>();
    private final Font arcadeFont;

    public PopupRenderer() {
        InputStream stream = PopupRenderer.class.getResourceAsStream(
                "/fonts/PressStart2P-Regular.ttf");
        Font loaded = (stream != null) ? Font.loadFont(stream, 20) : null;
        arcadeFont  = (loaded != null) ? loaded : Font.font("Monospace", 40);
    }

    /** 在場地座標 (x, y) 處新增一個扣血 popup */
    public void addPopup(double x, double y, double damage) {
        popups.add(new DamagePopup(x, y, damage));
    }

    /** 在場地座標 (x, y) 處新增一個回血 popup */
    public void addHealPopup(double x, double y, double amount) {
        popups.add(new DamagePopup(x, y, "+" + (int) amount, Color.color(0.25, 1.0, 0.25)));
    }

    /** 每幀呼叫：倒數計時並讓文字向上飄移 */
    public void update(double deltaTime) {
        Iterator<DamagePopup> it = popups.iterator();
        while (it.hasNext()) {
            DamagePopup p = it.next();
            p.timeLeft -= deltaTime;
            p.y -= FLOAT_SPEED * deltaTime;
            if (p.isExpired()) it.remove();
        }
    }

    /** 繪製所有存活的 popup（自動 clip 在場地範圍內） */
    public void render(GraphicsContext gc) {
        if (popups.isEmpty()) return;

        gc.save();

        // 限制繪製範圍在場地內，避免溢出到邊框外
        gc.beginPath();
        gc.rect(0, 0, ArenaConfig.WIDTH, ArenaConfig.HEIGHT);
        gc.clip();

        gc.setFont(arcadeFont);
        gc.setTextAlign(TextAlignment.CENTER);

        for (DamagePopup p : popups) {
            Color c = p.color;
            gc.setFill(Color.color(c.getRed(), c.getGreen(), c.getBlue(), p.getAlpha()));
            gc.fillText(p.text, p.x, p.y);
        }

        gc.restore();
    }
}
