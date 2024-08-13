package ui.Paddles;

import org.w3c.dom.css.Rect;
import ui.Ball;

import java.awt.*;

public class PaddleBot extends Paddle {

    private int ballY;

    public PaddleBot(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id) {
        super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT, id);
    }

    public void updateBallY(int ballY) {
        this.ballY = ballY;
    }

    public void move() {

    }

}
