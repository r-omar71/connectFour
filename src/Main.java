import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //Board.printBoard(grid);
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}