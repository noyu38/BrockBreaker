import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import javafx.stage.Stage;

public class BlockBreaker extends Application {
    public static final int WIDTH = 500, HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group root = new Group();
        Game game = new Game(gc);
        game.initializeUI(stage);

        root.getChildren().add(canvas);
        root.getChildren().add(game.getStartButton());
        root.getChildren().add(game.getRestartButton());
        root.getChildren().add(game.getExitButton());

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                game.setLeftPressed(true);
            } else if (e.getCode() == KeyCode.RIGHT) {
                game.setRightPressed(true);
            } else if (e.getCode() == KeyCode.SPACE) {
                game.handleSpacePress();
            } else if (e.getCode() == KeyCode.F1) {
                game.forceGameOver();
            } else if (e.getCode() == KeyCode.B) {
                game.bomb();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                game.setLeftPressed(false);
            } else if (e.getCode() == KeyCode.RIGHT) {
                game.setRightPressed(false);
            }
        });

        stage.setScene(scene);
        stage.setTitle("ブロック崩し");

        stage.show();

        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}