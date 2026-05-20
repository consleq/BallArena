package BallArena.model;

/** 負責移動、牆壁反彈、球與球碰撞（純 Java，不依賴 JavaFX） */
public class PhysicsEngine {

    /**
     * 更新一顆球的位置，並處理牆壁反彈
     * @param ball      要更新的球
     * @param deltaTime 經過的秒數
     */

    // 傷害常數
    private static final double SWORD_DAMAGE = 5.0;
    private static final double SPIKE_DAMAGE = 3.0;

    // 避免連續扣血的冷卻時間（秒）
    private double swordCooldown = 0;
    private double spikeCooldown = 0;
    private static final double HIT_COOLDOWN = 0.5;

    public void update(Ball ball, double deltaTime) {
        // 移動
        ball.setX(ball.getX() + ball.getVx() * deltaTime);
        ball.setY(ball.getY() + ball.getVy() * deltaTime);

        boolean hitV = false; // 碰到左右牆
        boolean hitH = false; // 碰到上下牆

        double r = ball.getRadius();

        // 左右牆反彈
        if (ball.getX() - r < 0) {
            ball.setX(r);
            ball.setVx(Math.abs(ball.getVx()));
            hitV = true;
        } else if (ball.getX() + r > ArenaConfig.WIDTH) {
            ball.setX(ArenaConfig.WIDTH - r);
            ball.setVx(-Math.abs(ball.getVx()));
            hitV = true;
        }

        // 上下牆反彈
        if (ball.getY() - r < 0) {
            ball.setY(r);
            ball.setVy(Math.abs(ball.getVy()));
            hitH = true;
        } else if (ball.getY() + r > ArenaConfig.HEIGHT) {
            ball.setY(ArenaConfig.HEIGHT - r);
            ball.setVy(-Math.abs(ball.getVy()));
            hitH = true;
        }

        // 碰牆時通知球
        if (hitV || hitH) {
            ball.onBounce(hitV, hitH);

            // 若是 SpikeBall，把尖刺基底貼在牆面上，並依牆面決定方向
            // 角度定義：0=朝上(下牆), 90=朝右(左牆), 180=朝下(上牆), 270=朝左(右牆)
            if (ball instanceof SpikeBall spikeBall) {
                double angle, sx, sy;
                if (hitV) {
                    if (ball.getX() < ArenaConfig.WIDTH / 2.0) {
                        angle = 90;  sx = 0;                   sy = ball.getY();
                    } else {
                        angle = 270; sx = ArenaConfig.WIDTH;   sy = ball.getY();
                    }
                } else {
                    if (ball.getY() < ArenaConfig.HEIGHT / 2.0) {
                        angle = 180; sx = ball.getX();         sy = 0;
                    } else {
                        angle = 0;   sx = ball.getX();         sy = ArenaConfig.HEIGHT;
                    }
                }
                spikeBall.addSpike(sx, sy, angle);
            }
        }
    }

    /** 更新冷卻計時器，每幀呼叫 */
    public void updateCooldowns(double deltaTime) {
        swordCooldown = Math.max(0, swordCooldown - deltaTime);
        spikeCooldown = Math.max(0, spikeCooldown - deltaTime);
    }

    /**
     * 檢查 SwordBall 的劍是否擊中目標球
     * 使用「點到線段的最短距離」判斷
     */
    public void checkSwordHit(SwordBall attacker, Ball target) {
        if (swordCooldown > 0) return;

        var sword = attacker.getSword();

        // 劍的起點與終點
        double ax = sword.getSwordStartX(), ay = sword.getSwordStartY();
        double bx = sword.getSwordEndX(),   by = sword.getSwordEndY();

        // 目標球心
        double cx = target.getX(), cy = target.getY();

        double dist = pointToSegmentDistance(cx, cy, ax, ay, bx, by);

        if (dist < target.getRadius()) {
            target.takeDamage(SWORD_DAMAGE);
            swordCooldown = HIT_COOLDOWN; // 重置冷卻，避免同一幀連續扣血
        }
    }

    /**
     * 檢查 SpikeBall 的尖刺尖端是否碰到目標球
     * 碰到時：彈開目標球，並（在冷卻外時）扣血
     */
    public void checkSpikeHit(SpikeBall attacker, Ball target) {
        for (var spike : attacker.getWallSpike().getSpikes()) {
            double tipX = spike.getTipX();
            double tipY = spike.getTipY();

            // 尖端到目標球心的向量
            double dx = target.getX() - tipX;
            double dy = target.getY() - tipY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < target.getRadius() && dist > 0) {
                // 法線：從尖端指向目標球心
                double nx = dx / dist;
                double ny = dy / dist;

                // 推出重疊，避免球卡在尖端上
                double overlap = target.getRadius() - dist;
                target.setX(target.getX() + nx * overlap);
                target.setY(target.getY() + ny * overlap);

                // 只在球朝尖端方向移動時才反射（避免剛被彈開又被吸回）
                double vDotN = target.getVx() * nx + target.getVy() * ny;
                if (vDotN < 0) {
                    target.setVx(target.getVx() - 2 * vDotN * nx);
                    target.setVy(target.getVy() - 2 * vDotN * ny);
                }

                // 冷卻內仍會反彈，但不重複扣血
                if (spikeCooldown <= 0) {
                    target.takeDamage(SPIKE_DAMAGE);
                    spikeCooldown = HIT_COOLDOWN;
                }
                break; // 一幀只處理一次碰撞
            }
        }
    }

    /**
     * 計算點 (px, py) 到線段 (ax,ay)-(bx,by) 的最短距離
     */
    private double pointToSegmentDistance(
            double px, double py,
            double ax, double ay,
            double bx, double by) {

        double dx = bx - ax, dy = by - ay;
        double lenSq = dx * dx + dy * dy;

        if (lenSq == 0) {
            // 線段退化成點
            return Math.sqrt((px - ax) * (px - ax) + (py - ay) * (py - ay));
        }

        // 投影比例 t，夾在 [0, 1] 表示在線段上
        double t = Math.max(0, Math.min(1,
                ((px - ax) * dx + (py - ay) * dy) / lenSq));

        double nearX = ax + t * dx;
        double nearY = ay + t * dy;

        return Math.sqrt((px - nearX) * (px - nearX) + (py - nearY) * (py - nearY));
    }

    /**
     * 簡單的球與球彈性碰撞（質量相等）
     */
    public void handleBallCollision(Ball a, Ball b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        double minDist = a.getRadius() + b.getRadius();

        if (dist < minDist && dist > 0) {
            // 分離兩球，避免重疊
            double overlap = (minDist - dist) / 2.0;
            double nx = dx / dist;
            double ny = dy / dist;
            a.setX(a.getX() - nx * overlap);
            a.setY(a.getY() - ny * overlap);
            b.setX(b.getX() + nx * overlap);
            b.setY(b.getY() + ny * overlap);

            // 交換法線方向的速度分量
            double dvx = b.getVx() - a.getVx();
            double dvy = b.getVy() - a.getVy();
            double dot = dvx * nx + dvy * ny;

            // 只在兩球靠近時才交換（避免黏在一起）
            if (dot < 0) {
                a.setVx(a.getVx() + dot * nx);
                a.setVy(a.getVy() + dot * ny);
                b.setVx(b.getVx() - dot * nx);
                b.setVy(b.getVy() - dot * ny);
            }
        }
    }
}