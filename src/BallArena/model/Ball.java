package BallArena.model;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/** 所有球種的抽象基底類別（不依賴 JavaFX） */
public abstract class Ball {

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected double hp;
    protected double maxHp;
    protected final double radius = ArenaConfig.BALL_RADIUS;

    /** 球的視覺風格（由子類別決定） */
    protected final BallStyle style;

    public Ball(double x, double y, double hp, BallStyle style) {
        this.x     = x;
        this.y     = y;
        this.hp    = hp;
        this.maxHp = hp;
        this.style = style;
    }

    public abstract void updateAbility(double deltaTime);
    public abstract void onBounce(boolean hitVertical, boolean hitHorizontal);

    /** 子類別必須提供球種的中文名稱（顯示在結算畫面） */
    public abstract String getTypeName();

    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getVx()     { return vx; }
    public double getVy()     { return vy; }
    public double getHp()     { return hp; }
    public double getMaxHp()  { return maxHp; }
    public double getRadius() { return radius; }
    public BallStyle getStyle() { return style; }

    public void setX(double x)   { this.x = x; }
    public void setY(double y)   { this.y = y; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }

    /**
     * 對球造成傷害
     * @return 實際扣除的血量
     */
    public double takeDamage(double dmg) {
        double oldHp = hp;
        hp = Math.max(0, hp - dmg);
        return oldHp - hp;
    }

    public void heal(double amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public boolean isDead() {
        return hp <= 0;
    }


    public void slowDown(){
//        if (Math.sqrt((vx*vx) + (vy *vy)) < 600){
            vx = vx*0.5;
            vy = vy*0.5;
//        }
//        else{
//            vx = vx*0.3;
//            vy = vy*0.3;
//        }

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));

        delay.setOnFinished(e -> {
                vx = vx*2;
                vy = vy*2;
        });

        delay.play();
    }
}