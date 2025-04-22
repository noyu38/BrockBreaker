import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;

public class Block {
    public static final int WIDTH = 50, HEIGHT = 20;
    public double x, y;
    public Color color;

    public Block(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void render(GraphicsContext gc) {
        Stop[] stops = new Stop[] {new Stop(0, Color.WHITE), new Stop(1,color)};
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops
        );

        gc.setFill(gradient);
        gc.fillRoundRect(x, y, WIDTH, HEIGHT, 8, 8);
        gc.setStroke(Color.BLACK);
        gc.strokeRoundRect(x, y, WIDTH, HEIGHT, 8, 8);
    }

    public boolean collideWith(double bx, double by, int size) {
        return bx + size > x && bx < x + WIDTH &&
               by + size > y && by < y + HEIGHT;
    }
}