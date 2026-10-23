
import java.util.List;

// RandomMoveDecorator adds random moves to the AI
// without changing the original AIPlayer class.
public class RandomMoveDecorator extends PlayerDecorator {

    // Stores the probability of choosing a random move.
    private final double randomChance;

    public RandomMoveDecorator(Player decoratedPlayer, double randomChance) {

        // Pass the original player to the parent class.
        super(decoratedPlayer);

        // Store the random move probability (e.g., 0.55 = 55%).
        this.randomChance = randomChance;
    }

    @Override
    public int getMove(Board board) {

        // Decide whether to choose a random move
        // based on the probability set for the difficulty level.
        if (Math.random() < randomChance) {

            // Get only the available columns to avoid full columns.
            List<Integer> available = Board.colsAvaliable(board);

            // Select a random index from the available columns.
            int randomIndex = (int) (Math.random() * available.size());

            // Get the column at the randomly selected index.
            int randomCol = available.get(randomIndex);

            // Print a message when the AI chooses a random move.
            System.out.println("AI chose a random move.");

            // Return the randomly selected column.
            return randomCol;
        }

        // If no random move is chosen, call getMove()
        // from the wrapped AIPlayer to calculate the best move using Minimax.
        return decoratedPlayer.getMove(board);
    }
}
