import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 10;
    private double x;
    private double y;

    public Paddle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveLeft() {
        if (x > 0)
            x -= 5;
    }

    public void moveRight() {
        if (x < BlockBreaker.WIDTH - WIDTH)
            x += 5;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, BlockBreaker.HEIGHT - HEIGHT, WIDTH, HEIGHT);
    }

    public boolean intersects(Ball ball) {
        return ball.getY() + Ball.SIZE >= y && ball.getX() + Ball.SIZE >= x
            && ball.getX() <= x + WIDTH && ball.getY() <= y + HEIGHT;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}