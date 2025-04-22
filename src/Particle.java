import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class Particle {
    double x, y;
    double dx, dy;
    double size;
    double life;
    Color color;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        this.dx = (Math.random() - 0.5) * 4;
        this.dy = (Math.random() - 0.5) * 4;
        this.size = 5 + Math.random() * 5;
        this.life = 1.0;
        this.color = Color.rgb(255, (int)(Math.random() * 155), 0, 1.0);
    }

    public void update() {
        x += dx;
        y += dy;
        life -= 0.02;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(color.deriveColor(0, 1, 1, life));
        gc.fillOval(x, y, size, size);
    }
}