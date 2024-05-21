import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class EscapeGame extends JFrame implements ActionListener, KeyListener {
    private static final int SHAPE_SIZE = 30;
    private static final int SHAPE_SPEED = 5;
    private static final int BARRIER_WIDTH = 20;
    private static final int BARRIER_HEIGHT = 100;
    private static final int BARRIER_SPEED = 5;
    private static final int BORDER_WIDTH = 40;
    private static final int WINNING_SCORE = 45;

    private int shapeX;
    private int shapeY;
    private int shapeSpeedX;
    private int shapeSpeedY;

    private List<Barrier> barriers;
    private int score;
    private int highestScore;

    private Timer timer;

    private Color backgroundColor;
    private Color scoreSpaceColor;

    private GamePanel gamePanel;

    private boolean showInstructions = true;
    private Timer instructionsTimer;

    private JButton endButton;
    private JButton pausePlayButton;

    // Constructor
    public EscapeGame() {
        setTitle("Escape Game");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setBackgroundColor(new Color(255, 192, 203));
        setScoreSpaceColor(new Color(255, 215, 0));
        ImageIcon icon = new ImageIcon("globe.png");
        setIconImage(icon.getImage());
        shapeX = getWidth() / 2;
        shapeY = getHeight() / 2;
        shapeSpeedX = 0;
        shapeSpeedY = 0;

        barriers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            barriers.add(new Barrier(getWidth() + i * 100,
                    50 + (int) (Math.random() * (getHeight() - 50 - BARRIER_HEIGHT))));
        }

        score = 0;
        highestScore = 0;

        addKeyListener(this);

        timer = new Timer(20, this);
        timer.start();

        gamePanel = new GamePanel();
        // Make the GamePanel focusable
        gamePanel.setFocusable(true);
        add(gamePanel);

        endButton = new JButton("End");
        endButton.addActionListener(e -> endGame());
        endButton.setBounds(getWidth() / 2 - 75, 5, 150, 40); // Increased button size
        gamePanel.add(endButton);

        pausePlayButton = new JButton("Pause");
        pausePlayButton.addActionListener(e -> togglePausePlay());
        pausePlayButton.setBounds(getWidth() / 2 - 75, 55, 150, 40); // Increased button size
        gamePanel.add(pausePlayButton);

        this.setFocusable(true);
    }

    // Handle end game button click
    private void endGame() {
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to end the game?",
                "End Game",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[] { "Yes", "No" },
                "No");

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // Handle pause/play button click
    private void togglePausePlay() {
        if (timer.isRunning()) {
            timer.stop();
            pausePlayButton.setText("Play");
        } else {
            timer.start();
            pausePlayButton.setText("Pause");
        }

        this.requestFocusInWindow();
    }

    // Action performed on timer tick
    @Override
    public void actionPerformed(ActionEvent e) {
        shapeX += shapeSpeedX;
        shapeY += shapeSpeedY;

        shapeX = Math.max(BORDER_WIDTH, Math.min(getWidth() - BORDER_WIDTH - SHAPE_SIZE, shapeX));
        shapeY = Math.max(BORDER_WIDTH, Math.min(getHeight() - BORDER_WIDTH - SHAPE_SIZE, shapeY));

        for (Barrier barrier : barriers) {
            barrier.setX(barrier.getX() - getBarrierSpeed());
            if (barrier.getX() + BARRIER_WIDTH < 0) {
                barrier.setX(getWidth());
                barrier.setY(50 + (int) (Math.random() * (getHeight() - 50 - BARRIER_HEIGHT)));
                score++;
            }

            if (checkCollision(barrier)) {
                handleCollision();
            }
        }

        if (score > highestScore) {
            highestScore = score;
            setBackgroundColor(new Color(255, 255, 224));
        } else {
            setBackgroundColor(backgroundColor);
        }

        gamePanel.repaint();
    }

    // Get barrier speed based on the score
    private int getBarrierSpeed() {
        return BARRIER_SPEED + score / 5;
    }

    // Check collision with a barrier
    private boolean checkCollision(Barrier barrier) {
        return shapeX < barrier.getX() + BARRIER_WIDTH &&
                shapeX + SHAPE_SIZE > barrier.getX() &&
                shapeY < barrier.getY() + BARRIER_HEIGHT &&
                shapeY + SHAPE_SIZE > barrier.getY();
    }

    // Handle collision events
    private void handleCollision() {
        if (score < WINNING_SCORE) {
            gameOver("You Lost!!");
            setBackgroundColor(Color.GRAY);
        } else {
            gameOver("You Won!!");
            setBackgroundColor(new Color(135, 206, 250));
        }
    }

    // Show game over dialog
    private void gameOver(String message) {
        timer.stop();
        if (score > highestScore) {
            highestScore = score;
        }

        int choice = JOptionPane.showOptionDialog(
                this,
                message + " Score: " + score,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[] { "Restart", "Exit" },
                "Restart");

        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    // Reset the game state
    private void resetGame() {
        shapeX = getWidth() / 2;
        shapeY = getHeight() / 2;
        shapeSpeedX = 0;
        shapeSpeedY = 0;

        barriers.clear();
        for (int i = 0; i < 15; i++) {
            barriers.add(new Barrier(getWidth() + i * 100,
                    50 + (int) (Math.random() * (getHeight() - 50 - BARRIER_HEIGHT))));
        }

        score = 0;

        timer.start();
    }

    // GamePanel for drawing components
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            setBackground(backgroundColor);

            g.setColor(scoreSpaceColor);
            g.fillRect(0, 0, getWidth(), 50);

            if (showInstructions) {
                Font instructionFont = new Font("Arial", Font.BOLD, 18);
                g.setFont(instructionFont);
                g.setColor(Color.BLACK);
                g.drawString(
                        "Use arrow keys to move. Press 'R' to restart and Space to move Up. Above 45: You win! Otherwise You lose!!",
                        getWidth() / 4,
                        getHeight() / 2);
                instructionsTimer = new Timer(3000, e -> {
                    showInstructions = false;
                    instructionsTimer.stop();
                });
                instructionsTimer.start();
            }

            g.setColor(Color.BLACK);
            g.fillOval(shapeX, shapeY, SHAPE_SIZE, SHAPE_SIZE);

            g.setColor(Color.BLUE);
            for (Barrier barrier : barriers) {
                g.fillRect(barrier.getX(), barrier.getY(), BARRIER_WIDTH, BARRIER_HEIGHT);
            }

            Font scoreFont = new Font("Arial", Font.BOLD, 24);
            g.setFont(scoreFont);
            g.setColor(new Color(28, 158, 84));
            g.drawString("Score: " + score, 10, 30);

            g.setColor(new Color(255, 0, 0));
            g.drawString("New Record: " + highestScore, getWidth() - 200, 30);
        }

        // Make the GamePanel focusable
        @Override
        public boolean isFocusable() {
            return true;
        }
    }

    // Handle key typed event
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Handle key pressed event
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE) {
            shapeSpeedY = -SHAPE_SPEED;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            shapeSpeedY = SHAPE_SPEED;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            shapeSpeedX = -SHAPE_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            shapeSpeedX = SHAPE_SPEED;
        } else if (keyCode == KeyEvent.VK_R) {
            resetGame();
        }
    }

    // Handle key released event
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_SPACE) {
            shapeSpeedY = 0;
        } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
            shapeSpeedX = 0;
        }
    }

    // Barrier class for representing barriers
    private class Barrier {
        private int x;
        private int y;

        public Barrier(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    // Set background color
    private void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    // Set score space color
    private void setScoreSpaceColor(Color color) {
        scoreSpaceColor = color;
    }
}
