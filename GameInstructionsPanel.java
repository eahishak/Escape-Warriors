import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GameInstructionsPanel extends JPanel {
    private static final Font INSTRUCTION_FONT = new Font("Arial", Font.BOLD, 18);

    public GameInstructionsPanel() {
        setPreferredSize(new Dimension(600, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(INSTRUCTION_FONT);
        g.setColor(Color.BLACK);
        g.drawString(
                "Use arrow keys to move. Press 'R' to restart and Space to move Up. Above 35: You win! Otherwise You lose!!",
                getWidth() / 4, getHeight() / 2);
    }
}
