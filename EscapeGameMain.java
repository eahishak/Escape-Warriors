import javax.swing.SwingUtilities;

public class EscapeGameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EscapeGame game = new EscapeGame();
            game.setVisible(true);
        });
    }
}
// To run the codes just type "java EscapeGameMain" in the terminal