import java.awt.*;

// Director: decides which steps build the game over dialog
public class GameOverDialog {

    public static final int PLAY_AGAIN = 0;
    public static final int QUIT       = 1;

    private final DialogBuilder builder;

    public GameOverDialog(DialogBuilder builder) {
        this.builder = builder;
    }

    public int construct(String message) {
        return builder
                .setWindowTitle("Game Over")
                .setTitle(message)
                .setHorizontal(true)
                .setButtonSize(120, 44)
                .setButtonFont(14)
                .addButton("Play Again", new Color(80, 200, 120), PLAY_AGAIN)
                .addButton("Quit",       new Color(180, 60, 60),  QUIT)
                .setDefaultValue(QUIT)
                .showAndGetResult();
    }

    public static int show(Window parent, String message) {
        return new GameOverDialog(new ChoiceDialogBuilder(parent)).construct(message);
    }
}
