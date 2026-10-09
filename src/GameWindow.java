import javax.swing.*;
import java.util.Random;

public class GameWindow extends JFrame {

    public GameWindow() {
        setTitle("Connect 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Show mode selection dialog
        DialogBuilder modeBuilder = new ChoiceDialogBuilder(this);
        ModeDialog modeDialog = new ModeDialog(modeBuilder);
        int mode = modeDialog.construct();
        if (mode == ModeDialog.CANCELLED) {
            System.exit(0);
        }

        // Randomly assign colours to players
        boolean firstIsRed = new Random().nextBoolean();
        Cell colourOne = firstIsRed ? Cell.R : Cell.Y;
        Cell colourTwo = firstIsRed ? Cell.Y : Cell.R;

        // Build players based on chosen mode
        
        int difficulty = 0;
        if (mode == ModeDialog.HUMAN_VS_AI ) {
            DialogBuilder difficultyBuilder = new ChoiceDialogBuilder(this);
            DifficultyDialog difficultyDialog = new DifficultyDialog(difficultyBuilder);
            difficulty = difficultyDialog.construct();
            if (difficulty == DifficultyDialog.CANCELLED) {
                System.exit(0);
            }
        }

        // Decide what type of player sits in each seat (game rules)
            int typeOne, typeTwo, aiLevel;
            switch (mode) {
                case ModeDialog.HUMAN_VS_AI:
                    typeOne = PlayerFactory.HUMAN;
                    typeTwo = PlayerFactory.AI;
                    aiLevel = difficulty;
                break;
                case ModeDialog.AI_VS_AI:
                    typeOne = PlayerFactory.AI;
                    typeTwo = PlayerFactory.AI;
                    aiLevel = 3;
                break;
                default: // HUMAN_VS_HUMAN
                    typeOne = PlayerFactory.HUMAN;
                    typeTwo = PlayerFactory.HUMAN;
                    aiLevel = 0;
            }

             // Ask the factory to create them (object creation)
            Player playerOne = PlayerFactory.createPlayer(typeOne, colourOne, aiLevel);
            Player playerTwo = PlayerFactory.createPlayer(typeTwo, colourTwo, aiLevel);
        


        Board board = new Board();
        add(new BoardPanel(board, playerOne, playerTwo));
        pack();
        setLocationRelativeTo(null);
    }
}