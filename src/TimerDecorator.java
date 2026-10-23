
/**
 * TimerDecorator is a Concrete Decorator.
 * It measures the time taken by a player to calculate a move
 * without modifying the original player's logic.
 */
public class TimerDecorator extends PlayerDecorator {

    // Receives the player to be decorated (AIPlayer or another Decorator).
    public TimerDecorator(Player decoratedPlayer) {

        // Passes the player to PlayerDecorator to store its reference.
        super(decoratedPlayer);
    }

    @Override
    public int getMove(Board board) {

        // Record the start time before calculating the move.
        long startTime = System.currentTimeMillis();

        // Call getMove() from the wrapped player to get its move.
        // The wrapped player may use Minimax or RandomMoveDecorator.
        int move = decoratedPlayer.getMove(board);

        // Record the end time after the move is calculated.
        long endTime = System.currentTimeMillis();

        // Calculate the total execution time in milliseconds.
        long duration = endTime - startTime;

        // Added Behavior: Display the time taken to calculate the move.
        System.out.println(
                "AI calculated move in " + duration + "ms"
        );

        // Return the original move without changing it.
        return move;
    }
}
