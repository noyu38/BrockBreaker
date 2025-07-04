import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Game {
    private GraphicsContext gc;
    private Button startButton;
    private Button restartButton;
    private Button exitButton;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private Ball ball;
    private Paddle paddle;
    private List<Block> blocks;
    private List<Particle> particles = new ArrayList<>();
    private GameState state = GameState.START;

    private int lives = 3;
    private int score = 0;
    private double prevPaddleX;

    private static final Color[] COLORS = {
            Color.RED, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.PURPLE
    };

    public Game(GraphicsContext gc) {
        this.gc = gc;
        this.paddle = new Paddle(BlockBreaker.WIDTH / 2 - Paddle.WIDTH / 2, BlockBreaker.HEIGHT - Paddle.HEIGHT);
        this.ball = new Ball(BlockBreaker.WIDTH / 2, BlockBreaker.HEIGHT / 2, 3, 3);
        initializeBlocks();
    }

    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    public void initializeUI(Stage stage) {
        startButton = new Button("START");
        restartButton = new Button("RESTART");
        exitButton = new Button("EXIT");

        startButton.setLayoutX(BlockBreaker.WIDTH / 2 - 80);
        startButton.setLayoutY(BlockBreaker.HEIGHT / 2 + 100);
        startButton.setPrefWidth(160);

        restartButton.setLayoutX(BlockBreaker.WIDTH / 2 - 80);
        restartButton.setLayoutY(BlockBreaker.HEIGHT / 2);
        restartButton.setPrefWidth(160);

        exitButton.setLayoutX(BlockBreaker.WIDTH / 2 - 80);
        exitButton.setLayoutY(BlockBreaker.HEIGHT / 2 + 50);
        exitButton.setPrefWidth(160);

        startButton.setVisible(true);
        restartButton.setVisible(false);
        exitButton.setVisible(false);

        startButton.setOnAction(e -> {
            state = GameState.PLAYING;
            startButton.setVisible(false);
        });
        restartButton.setOnAction(e -> restartGame());
        exitButton.setOnAction(e -> Platform.exit());
    }

    public void setLeftPressed(boolean pressed) {
        this.leftPressed = pressed;
    }

    public void setRightPressed(boolean pressed) {
        this.rightPressed = pressed;
    }

    private void initializeBlocks() {
        blocks = new ArrayList<>();
        Random rand = new Random();
        int rows = 5, cols = BlockBreaker.WIDTH / Block.WIDTH;

        Color[][] colorGrid = new Color[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                List<Color> availableColors = new ArrayList<>(List.of(COLORS));

                if (col > 0) {
                    availableColors.remove(colorGrid[row][col - 1]);
                }
                if (row > 0) {
                    availableColors.remove(colorGrid[row - 1][col]);
                }

                Color selected = availableColors.get(rand.nextInt(availableColors.size()));
                colorGrid[row][col] = selected;

                blocks.add(new Block(col * Block.WIDTH, row * Block.HEIGHT, selected));
            }
        }
    }

    private void update() {
        if (state != GameState.PLAYING)
            return;

        if (leftPressed)
            paddle.moveLeft();
        if (rightPressed)
            paddle.moveRight();

        ball.update();

        // 壁と衝突したら向きを反転
        if (ball.getX() <= 0 || ball.getX() >= BlockBreaker.WIDTH - Ball.SIZE)
            ball.reverseX();
        if (ball.getY() <= 0)
            ball.reverseY();

        // パドルと衝突したら向きを反転
        if (paddle.intersects(ball)) {

            // ボールの中心とパドルの中心の座標
            double ballCenterX = ball.getX() + Ball.SIZE / 2.0;
            double ballCenterY = ball.getY() + Ball.SIZE / 2.0;
            double paddleCenterX = paddle.getX() + Paddle.WIDTH / 2.0;
            double paddleCenterY = paddle.getY() + Paddle.HEIGHT / 2.0;

            // 中心間の距離
            double dx = Math.abs(ballCenterX - paddleCenterX);
            double dy = Math.abs(ballCenterY - paddleCenterY);

            double combinedHalfWidth = (Ball.SIZE + Paddle.WIDTH) / 2.0;
            double combinedHalfHeight = (Ball.SIZE + Paddle.HEIGHT) / 2.0;

            if (combinedHalfWidth * dy > combinedHalfHeight * dx) {
                ball.reverseY();
                ball.setY(paddle.getY() - Ball.SIZE);

                double paddleSpeed = paddle.getX() - prevPaddleX;
                if (paddleSpeed != 0) {
                    ball.addSpin(paddleSpeed * 0.3);
                }

                if (Math.abs(ball.getDx()) < 0.1) {
                    double nudge = (Math.random() - 0.5) * 0.5;
                    ball.addSpin(nudge);
                }
            } else {
                ball.reverseX();
                if (ballCenterX < paddleCenterX) {
                    ball.setX(paddle.getX() - Ball.SIZE);
                } else {
                    ball.setX(paddle.getX() + Paddle.WIDTH);
                }
            }

            double paddleSpeed = paddle.getX() - prevPaddleX;

            if (paddleSpeed != 0) {
                ball.addSpin(paddleSpeed * 0.3);
            }

            // ボールのdxがあまりにも小さい場合は水平方向へのスピンを追加させる
            if (Math.abs(ball.getDx()) < 0.1) {
                double nudge = (Math.random() - 0.5) * 0.5;
                ball.addSpin(nudge);
            }
        }

        // ブロックと衝突したときの操作
        Iterator<Block> iterator = blocks.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.collideWith(ball.getX(), ball.getY(), Ball.SIZE)) {
                iterator.remove();
                score += 10;

                double ballCenterX = ball.getX() + Ball.SIZE / 2;
                double ballCenterY = ball.getY() + Ball.SIZE / 2;

                double blockCenterX = block.x + Block.WIDTH / 2.0;
                double blockCenterY = block.y + Block.HEIGHT / 2.0;

                double dx = Math.abs(ballCenterX - blockCenterX);
                double dy = Math.abs(ballCenterY - blockCenterY);

                if (dx > dy) {
                    if (dy > 10) {
                        ball.reverseY();
                    }
                    ball.reverseX();
                } else {
                    ball.reverseY();
                }
                break;
            }
        }

        // 下に落ちたとき、ボールの位置をリセット
        if (ball.getY() > BlockBreaker.HEIGHT) {
            lives--;
            if (lives <= 0) {
                state = GameState.GAME_OVER;
                restartButton.setVisible(true);
                exitButton.setVisible(true);
            } else {
                ball.reset(BlockBreaker.WIDTH / 2, BlockBreaker.HEIGHT / 2);
            }
        }

        particles.removeIf(p -> !p.isAlive());
        for (Particle p : particles) {
            p.update();
        }

        prevPaddleX = paddle.getX();

        if (blocks.isEmpty() && state == GameState.PLAYING) {
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                state = GameState.CLEAR;
                restartButton.setVisible(true);
                exitButton.setVisible(true);
            }));
            delay.setCycleCount(1);
            delay.play();
        }
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, BlockBreaker.WIDTH, BlockBreaker.HEIGHT);

        switch (state) {

            // スタート画面を設定
            case START -> {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
                gc.fillText("ブロック崩し", BlockBreaker.WIDTH / 2 - 80, 200);
                // gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
                // gc.fillText("Press SPACE to start", 150, 300);
            }

            // プレイ中の画面を設定
            case PLAYING -> {
                gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.DARKSLATEBLUE),
                        new Stop(1, Color.BLACK)));
                gc.fillRect(0, 0, BlockBreaker.WIDTH, BlockBreaker.HEIGHT);

                paddle.render(gc);
                ball.render(gc);

                for (Block block : blocks) {
                    block.render(gc);
                }

                for (Particle p : particles) {
                    p.render(gc);
                }

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                gc.fillText("Life: " + lives, 10, 20);
            }

            // ゲームオーバー画面を設定
            case GAME_OVER -> {
                gc.setFill(Color.rgb(0, 0, 0, 0.7));
                gc.fillRect(0, 0, BlockBreaker.WIDTH, BlockBreaker.HEIGHT);
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
                gc.fillText("GAME OVER", 130, 200);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(24));
                gc.fillText("Score: " + score, 200, 280);
                // gc.fillText("Press SPACE to Restart", 130, 360);
            }

            // クリア画面を設定
            case CLEAR -> {
                gc.setFill(Color.rgb(0, 0, 0, 0.7));
                gc.fillRect(0, 0, BlockBreaker.WIDTH, BlockBreaker.HEIGHT);
                gc.setFill(Color.LIME);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
                gc.fillText("CLEAR!!", 150, 200);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(24));
                gc.fillText("Score: " + score, 200, 280);
            }
        }
    }

    public void handleSpacePress() {
        switch (state) {
            case START -> {
                state = GameState.PLAYING;
                if (startButton != null)
                    startButton.setVisible(false);
            }
            case GAME_OVER -> {
                score = 0;
                lives = 3;
                initializeBlocks();
                ball.reset(BlockBreaker.WIDTH / 2, BlockBreaker.HEIGHT / 2);
                state = GameState.PLAYING;
                restartButton.setVisible(false);
                exitButton.setVisible(false);
                if (startButton != null)
                    startButton.setVisible(false);
            }
            default -> {
                return;
            }
        }
    }

    private void restartGame() {
        initializeBlocks();
        ball.reset(BlockBreaker.WIDTH / 2, BlockBreaker.HEIGHT / 2);
        lives = 3;
        score = 0;
        state = GameState.PLAYING;

        restartButton.setVisible(false);
        exitButton.setVisible(false);
    }

    public void forceGameOver() {
        if (state == GameState.PLAYING) {
            state = GameState.GAME_OVER;
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
    }

    public void bomb() {
        if (state == GameState.PLAYING) {
            for (Block block : blocks) {
                createExplosion(block.x + Block.WIDTH / 2, block.y + Block.HEIGHT / 2);
            }
            blocks.clear();
            score += 10 * 5 * (BlockBreaker.WIDTH / Block.WIDTH);
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                state = GameState.CLEAR;
                restartButton.setVisible(true);
                exitButton.setVisible(true);
            }));
            delay.setCycleCount(1);
            delay.play();
        }
    }

    private void createExplosion(double x, double y) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x, y));
        }
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
