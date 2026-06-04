package BallArena.model;

/** 場地相關常數，所有層共用 */
public class ArenaConfig {
    public static final double WIDTH  = 300;
    public static final double HEIGHT = 300;
    public static final double BALL_RADIUS = 20;
    /** 球的基準移動速度（出生、撞球後皆正規化為此速度） */
    public static final double BALL_SPEED = 250;
}