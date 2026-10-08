import java.awt.*;

// Director: decides which steps build the game mode dialog
public class ModeDialog {

    public static final int HUMAN_VS_HUMAN = 1;
    public static final int HUMAN_VS_AI    = 2;
    public static final int AI_VS_AI       = 3;
    public static final int CANCELLED      = -1;

    private final DialogBuilder builder;

    public ModeDialog(DialogBuilder builder) {
        this.builder = builder;
    }

    public int construct() {
        return builder
                .setWindowTitle("Select Game Mode")
                .setTitle("CONNECT FOUR")
                .setSubtitle("Choose a game mode to begin")
                .addButton("Human vs Human", new Color(220, 60, 60),  HUMAN_VS_HUMAN)
                .addButton("Human vs AI",    new Color(240, 200, 40), HUMAN_VS_AI)
                .addButton("AI vs AI",       new Color(80, 160, 255), AI_VS_AI)
                .setDefaultValue(CANCELLED)
                .build()
                .showAndGetResult();
    }

    public static int show(Window parent) {
        return new ModeDialog(new ChoiceDialogBuilder(parent)).construct();
    }
}
