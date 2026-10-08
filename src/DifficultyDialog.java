import java.awt.*;

// Director: decides which steps build the difficulty dialog
public class DifficultyDialog {

    public static final int EASY      = 1;
    public static final int MEDIUM    = 2;
    public static final int HARD      = 3;
    public static final int CANCELLED = -1;

    private final DialogBuilder builder;

    public DifficultyDialog(DialogBuilder builder) {
        this.builder = builder;
    }

    public int construct() {
        return builder
                .setWindowTitle("Select Difficulty")
                .setTitle("CONNECT FOUR")
                .setSubtitle("Choose a difficulty.")
                .addButton("Easy",   new Color(220, 60, 60),  EASY)
                .addButton("Medium", new Color(240, 200, 40), MEDIUM)
                .addButton("Hard",   new Color(80, 160, 255), HARD)
                .setDefaultValue(CANCELLED)
                .build()
                .showAndGetResult();
    }
}
