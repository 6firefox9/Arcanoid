package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

public class ArkanoidGame extends JPanel implements Runnable, KeyListener {
    final int WIDTH = 520;
    final int HEIGHT = 450;

    boolean isGameOver;
    Thread thread;

    BufferedImage view;
    Graphics graphics;

    BufferedImage background, paddle, ball, block, gameOver;

    int n = 0;
    Position[] blocksPosition;
    Position paddlePosition;

    boolean right, left;
    int paddleX, paddleY;
    int ballX, ballY;
    int ballDx, ballDy;

    public ArkanoidGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        JFrame mainWindow = new JFrame("Arcanoid Game");
        mainWindow.setResizable(false);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.add(new ArkanoidGame());

        mainWindow.setIconImage(Toolkit.getDefaultToolkit().getImage("/ball.png"));

        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    public void start() {
        try {
            view = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            graphics = (Graphics2D) view.getGraphics();
            background = ImageIO.read(getClass().getResource("/background.png"));
            paddle = ImageIO.read(getClass().getResource("/paddle.png"));
            ball = ImageIO.read(getClass().getResource("/ball.png"));
            block = ImageIO.read(getClass().getResource("/block.png"));
            gameOver = ImageIO.read(getClass().getResource("/gameOver.png"));

            blocksPosition = new Position[100];
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    blocksPosition[n] = new Position(i * 43, j * 20);
                    blocksPosition[n].setBoundsSize(block.getWidth(), block.getHeight());
                    n++;
                }
            }

            paddleX = (WIDTH / 2) - (paddle.getWidth() / 2);
            paddleY = HEIGHT - paddle.getHeight();
            paddlePosition = new Position(paddleX, paddleY);
            paddlePosition.setBoundsSize(paddle.getWidth(), paddle.getHeight());

            ballX = (WIDTH / 2) - (ball.getWidth() / 2);
            ballY = HEIGHT - paddle.getHeight();

            ballDx = -(new Random().nextInt(4) % 4 + 3);
            ballDy = -5;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restart() {
            blocksPosition = new Position[100];
            for (int i = 1; i <= 10; i++) {
                for (int j = 1; j <= 10; j++) {
                    blocksPosition[n] = new Position(i * 43, j * 20);
                    blocksPosition[n].setBoundsSize(block.getWidth(), block.getHeight());
                    n++;
                }
            }

            paddleX = (WIDTH / 2) - (paddle.getWidth() / 2);
            paddleY = HEIGHT - paddle.getHeight();
            paddlePosition = new Position(paddleX, paddleY);
            paddlePosition.setBoundsSize(paddle.getWidth(), paddle.getHeight());

            ballX = (WIDTH / 2) - (ball.getWidth() / 2);
            ballY = HEIGHT - paddle.getHeight();

            ballDx = -(new Random().nextInt(4) % 4 + 3);
            ballDy = -5;
    }

    public void update() {
        ballX += ballDx;
        ballY += ballDy;

        for(int i = 0; i < n; i++){
            if(new Rectangle((ballX + 3), (ballY + 3), 8, 8).intersects(blocksPosition[i].bounds)) {
                blocksPosition[i].setPosition(-100,0);
                ballDx = -(new Random().nextInt(4) % 4 + 3);
                ballDy = -ballDy;
            }
        }

        if(ballX < 0 || ballX > WIDTH - ball.getWidth()) {
            ballDx = -ballDx;
        } else if (ballY < 0) {
            ballDy = -ballDy;
        } else if(ballY > HEIGHT - ball.getHeight()) {
            isGameOver = true;
            ballDx = 0;
            ballDy = 0;
            ballX = -50;
            ballY = -50;
        }


        if(right) {
            paddleX += 6;
        } else if(left) {
            paddleX -= 6;
        }

        if(paddleX >= WIDTH - paddle.getWidth()) {
            paddleX = WIDTH - paddle.getWidth();
        } else if (paddleX <= 0) {
            paddleX = 0;
        }

        if(new Rectangle(ballX, ballY, 20, 20).intersects(paddlePosition.bounds)) {
            ballDy = -(new Random().nextInt(4) % 4 + 3);
        }
        paddlePosition.setPosition(paddleX, paddleY);
    }

    public void draw() {
        graphics.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
        graphics.drawImage(ball, ballX, ballY, ball.getWidth(), ball.getHeight(), null);
        graphics.drawImage(paddle, paddleX, paddleY, paddle.getWidth(), paddle.getHeight(), null);

        for(int i = 0; i < n; i++){
            graphics.drawImage(block, blocksPosition[i].x, blocksPosition[i].y, block.getWidth(), block.getHeight(), null);
        }

        if (isGameOver) {
            graphics.drawImage(gameOver, 0, 0, WIDTH, HEIGHT, null);
        }

        Graphics graphics2 = getGraphics();
        graphics2.drawImage(view, 0, 0, WIDTH, HEIGHT, null);
        graphics2.dispose();
    }

    @Override
    public void run() {
        requestFocus();
        start();
        while (true) {
            update();
            draw();
            try {
                Thread.sleep(1000/70);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_SPACE:
                if(isGameOver) {
                    n = 0;
                    isGameOver = false;
                    restart();
                }
        }
    }

    class Position {
        int x, y;
        Rectangle bounds;

        public Position(int x, int y) {
            setPosition(x, y);
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            if (bounds != null) {
                bounds.setLocation(x, y);
            }
        }

        public void setBoundsSize(int width, int height) {
            bounds = new Rectangle(x, y, width, height);
        }
}

}