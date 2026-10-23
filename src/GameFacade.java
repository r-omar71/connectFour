// Facade: one simple interface to the game subsystem (Board + Players + game state).
// Client: BoardPanel.
public class GameFacade {

    //Subsystem objects
    private final Board board;
    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;

    //Game state moved out of BoardPanel
    private Cell winner = null;
    private boolean isDraw = false;

    public GameFacade(Player playerOne, Player playerTwo){
        this.board = new Board();           // Facade creates its subsystem object
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = playerOne;
    }

        // Asks the current player for its move (AI computes it, Human returns its pending click)
    public int requestMove() {
        return currentPlayer.getMove(board);
    }

    // Drop -> check state -> swap. Returns false if the move was not applied.
    public boolean playMove(int col) {
        if (isGameOver() || !Board.isMoveValid(board, col)) return false;
        Board.dropCell(board, col, currentPlayer.getColour());
        checkGameState();
        swapPlayer();
        return true;
    }

    public void reset() {
        board.resetBoard();
        winner = null;
        isDraw = false;
        currentPlayer = playerOne;
    }

    public Player getCurrentPlayer() { 
        return currentPlayer;
    }
    public Cell getWinner(){ 
        return winner; 
    }
    public boolean isDraw(){ 
        return isDraw; 
    }
    public boolean isGameOver(){
         return winner != null || isDraw;
    }

    public Cell getCell(int row, int col) {
        return Board.getCell(board.getGrid(), row, col);
    }

    private void checkGameState() {
        winner = Board.isGameWon(board);
        if (winner == Cell.EMPTY) winner = null;
        if (winner == null && Board.isGridFull(board)) isDraw = true;
    }

    private void swapPlayer() {
        if (isGameOver()) return;
        currentPlayer = (currentPlayer == playerOne) ? playerTwo : playerOne;
    }
}