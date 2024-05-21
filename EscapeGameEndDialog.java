import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class EscapeGameEndDialog extends JDialog {
    public EscapeGameEndDialog(JFrame parent, String message, int score) {
        super(parent, true);

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
            ((EscapeGame) parent).resetGame();
        } else {
            System.exit(0);
        }
    }
}
