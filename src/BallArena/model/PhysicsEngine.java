package BallArena.model;

/** 負責移動、牆壁反彈、球與球碰撞（純 Java，不依賴 JavaFX） */
public class PhysicsEngine {

    /**
     * 更新一顆球的位置，並處理牆壁反彈
     * @param ball      要更新的球
     * @param deltaTime 經過的秒數
     */
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

            // 若是 SpikeBall，在碰撞點產生尖刺
            if (ball instanceof SpikeBall spikeBall) {
                spikeBall.addSpike(ball.getX(), ball.getY());
            }
        }
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