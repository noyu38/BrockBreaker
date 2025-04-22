import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    public static final int SIZE = 10;
    private double x, y, dx, dy;

    public Ball(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public void reverseX() {
        dx *= -1;
    }

    public void reverseY() {
        dy *= -1;
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.dy = -3;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, SIZE, SIZE);
    }

    public void addSpin(double spin) {
        dx += spin;

        double maxSpeed = 6.0;
        if (dx > maxSpeed) dx = maxSpeed;
        if (dx < -maxSpeed) dx = -maxSpeed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}