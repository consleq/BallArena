package BallArena.ability;

/** 所有技能的共用介面（Strategy 模式） */
public interface Ability {
    /** 每幀呼叫，deltaTime 單位為秒 */
    void update(double deltaTime);

    /** 碰到牆壁時觸發 */
    void onBounce();
}