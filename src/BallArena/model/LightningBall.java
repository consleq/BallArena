package BallArena.model;

import BallArena.ability.LightningStrike;

/**
 * 閃電球：每隔一段時間鎖定敵方當下位置瞬間放電（必中），
 * 以鋸齒狀電光命中目標造成固定傷害。
 */
public class LightningBall extends Ball {

    private final LightningStrike lightningStrike;

    public LightningBall(double x, double y, BallStyle style) {
        super(x, y, 100, style);
        this.lightningStrike = new LightningStrike(this);
    }

    /** 由 GameController 在建好雙方球後設定放電目標 */
    public void setTarget(Ball target) {
        lightningStrike.setTarget(target);
    }

    @Override
    public void updateAbility(double deltaTime) {
        lightningStrike.update(deltaTime);
    }

    @Override
    public void onBounce(boolean hitVertical, boolean hitHorizontal) {
        lightningStrike.onBounce();
    }

    @Override
    public String getTypeName() { return "閃電球"; }

    public LightningStrike getLightningStrike() { return lightningStrike; }
}
