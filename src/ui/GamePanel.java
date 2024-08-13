package ui;

import ui.Paddles.Paddle;
import ui.Paddles.PaddleUser;
import ui.Paddles.PaddleBot;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable {

    private enum GameMode { ONE_PLAYER, TWO_PLAYER }
    private GameMode gameMode;
    private static final int GAME_WIDTH = 1000;
    private static final int GAME_HEIGHT = (int)(GAME_WIDTH * 5 / 9);
    private static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    private static final int BALL_DIAMETER = 20;
    private static final int PADDLE_WIDTH = 25;
    private static final int PADDLE_HEIGHT = 100;
    private Thread gameThread;
    private Image image;
    private Graphics graphics;
    private Random random;
    private Paddle paddle1;
    private Paddle paddle2;
    private Ball ball;
    private Score score;


    public GamePanel() {
        String[] options = {"One Player", "Two Players"};
        int choice = JOptionPane.showOptionDialog(null, "Select Game Mode", "Game Mode",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            gameMode = GameMode.ONE_PLAYER;
        } else {
            gameMode = GameMode.TWO_PLAYER;
        }
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

        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER - 2), random.nextInt((GAME_HEIGHT - BALL_DIAMETER)),
                BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2),
                PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2),
                PADDLE_WIDTH, PADDLE_HEIGHT, 2);

    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }


    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }
    public void move() {
        paddle1.move();
        movePaddle2();
        ball.move();
    }

    public void movePaddle2() {

        if (gameMode == GameMode.TWO_PLAYER) {
            paddle2.move();
        } else {

            // Set the speed of the bot paddle
            int botSpeed = 5;

            // Calculate the center of the paddle and ball
            int paddleCenterY = paddle2.y + paddle2.height / 2;
            int ballCenterY = ball.y + ball.height / 2;

            // Move the paddle towards the ball's position
            if (paddleCenterY < ballCenterY) {
                paddle2.y += botSpeed;
            } else if (paddleCenterY > ballCenterY) {
                paddle2.y -= botSpeed;
            }

        }

    }

    public void checkCollision() {
        // bounce ball off top and bottom window edges
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        // bounce ball off paddles
        if (ball.intersects(paddle1)) {
            ball.xVelocity = -1 * ball.xVelocity;
            ball.xVelocity++; //optional for more difficulty
            if (ball.yVelocity > 0) {
                ball.yVelocity++; //optional for more difficulty
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = -1 * ball.xVelocity;
            ball.xVelocity++; //optional for more difficulty
            if (ball.yVelocity > 0) {
                ball.yVelocity++; //optional for more difficulty
            } else {
                ball.yVelocity--;
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        // Stops paddles at window edges
        if (paddle1.y <= 0) {
            paddle1.y = 0;
        }
        if (paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT)) {
            paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        }

        if (paddle2.y <= 0) {
            paddle2.y = 0;
        }
        if (paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT)) {
            paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
        }

        // give a player 1 point and create new paddles & ball
        if (ball.x <= 0) {
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2: " + score.player2);
        }


        // give a player 1 point and create new paddles & ball
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1: " + score.player2);
        }
    }

    public void run() {
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns =  1000000000 / amountOfTicks;
        double delta = 0;
        while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);

        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }

}
