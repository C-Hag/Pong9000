package pong;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.*;

public class GamePanel extends JPanel implements Runnable {
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = 400;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    static final int PADDLE_SPEED = 50;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public enum OpponentType {
        PLAYER,
        AI
    }

    private static Difficulty difficulty = Difficulty.MEDIUM;
    private static OpponentType opponentType = OpponentType.PLAYER;

    private boolean isMuted = false;
    private Clip backgroundMusic;
    private boolean gameStarted = false;
    private boolean isPaused = false;

    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        Toolkit.getDefaultToolkit().sync();

        if (!gameStarted && (score.player1 == 5 || score.player2 == 5)) {
            displayWinner(g);
        }
    }

    private void displayWinner(Graphics g) {
        String winnerString;
        if (score.player1 == 5) {
            winnerString = "Player 1 Wins!";
        } else {
            winnerString = "Player 2 Wins!";
        }
    
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 50));
        FontMetrics winnerMetrics = g.getFontMetrics();  // Get FontMetrics for winner string
        int x = (GAME_WIDTH - winnerMetrics.stringWidth(winnerString)) / 2;
        int y = GAME_HEIGHT / 2;
        g.drawString(winnerString, x, y);
    
        String replayString = "Press ENTER to Play Again or ESC to Exit";
        g.setFont(new Font("Tahoma", Font.PLAIN, 30));
        FontMetrics replayMetrics = g.getFontMetrics();  // Get FontMetrics for replay string
        x = (GAME_WIDTH - replayMetrics.stringWidth(replayString)) / 2;
        y += 50;
        g.drawString(replayString, x, y);
    }
    

    public void mute() {
        if (!isMuted) {
            backgroundMusic.stop();
            isMuted = true;
        }
    }

    public void unmute() {
        if (isMuted) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMuted = false;
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            this.requestFocusInWindow();  // Ensure focus is back to the GamePanel
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void playMusic(String musicName) {
    if (backgroundMusic != null && backgroundMusic.isRunning()) {
        backgroundMusic.stop();
    }
    try (InputStream audioInputStream = getClass().getResourceAsStream("/music/" + musicName);
         BufferedInputStream bis = new BufferedInputStream(audioInputStream);
         AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {
        
        backgroundMusic = AudioSystem.getClip();
        backgroundMusic.open(ais);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

        if (isMuted) {
            backgroundMusic.stop();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if (ball.yVelocity > 0) {
                ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;
            if (ball.yVelocity > 0) {
                ball.yVelocity++;
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if (paddle1.y >= GAME_HEIGHT - PADDLE_HEIGHT) {
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }
        if (paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if (paddle2.y >= GAME_HEIGHT - PADDLE_HEIGHT) {
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }

        if (ball.x <= 0) {
            score.player2++;
            newBall();
            newPaddles();
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newBall();
            newPaddles();
        }

        if (score.player1 == 5 || score.player2 == 5) {
            gameStarted = false;
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            if (gameStarted && !isPaused()) { // Check gameStarted flag
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                if (delta >= 1) {
                    move();
                    aiMove(); 
                    checkCollision();
                    repaint();
                    delta--;
                }
            } else {
                // This helps avoid busy-waiting when the game is paused or not started
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void aiMove() {
        if (opponentType == OpponentType.AI) {
            if (paddle2.y < ball.y) {
                paddle2.y += 2;
            }
            if (paddle2.y > ball.y) {
                paddle2.y -= 2;
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);

            if (!gameStarted) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Start the game
                    score.player1 = 0;
                    score.player2 = 0;
                    newPaddles();
                    newBall();
                    gameStarted = true;
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Return to the menu
                    GameFrame gameFrame = (GameFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                    gameFrame.showMenu();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }

    public static void setDifficulty(Difficulty difficulty) {
        GamePanel.difficulty = difficulty;
    }

    public static void setOpponentType(OpponentType opponentType) {
        GamePanel.opponentType = opponentType;
    }

    public static OpponentType getOpponentType() {
        return opponentType;
    }

    public void startGame() {
        gameStarted = true;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
}