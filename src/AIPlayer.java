public class AIPlayer implements Player {

    private Cell colour;
    private Cell opponentColour;
    private int searchDepth;

    public AIPlayer(Cell colour, int searchDepth) {
        this.colour = colour;
        this.opponentColour = (colour == Cell.R) ? Cell.Y : Cell.R;
        this.searchDepth = searchDepth;
    }

    @Override
    public Cell getColour() {
        return colour;
    }

    @Override
    public int getMove(Board board) {
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int col : Board.colsAvaliable(board)) {
            Board clone = board.cloneBoard();
            Board.dropCell(clone, col, colour);
            int score = minimax(clone, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }

        return bestCol;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximising) {
        Cell winner = Board.isGameWon(board);

        if (winner == colour) return 100000 + depth;
        if (winner == opponentColour) return -100000 - depth;
        if (Board.isGridFull(board) || depth == 0) return evaluateBoard(board);

        if (isMaximising) {
            int maxScore = Integer.MIN_VALUE;
            for (int col : Board.colsAvaliable(board)) {
                Board clone = board.cloneBoard();
                Board.dropCell(clone, col, colour);
                int score = minimax(clone, depth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int col : Board.colsAvaliable(board)) {
                Board clone = board.cloneBoard();
                Board.dropCell(clone, col, opponentColour);
                int score = minimax(clone, depth - 1, alpha, beta, true);
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return minScore;
        }
    }

    private int evaluateBoard(Board board) {
        int score = 0;
        Cell[][] grid = board.getGrid();

        // Reward centre column control
        for (int row = 0; row < 6; row++) {
            if (grid[row][3] == colour) score += 3;
        }

        // Score horizontal windows
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 4; col++) {
                score += scoreWindow(
                        grid[row][col], grid[row][col+1],
                        grid[row][col+2], grid[row][col+3], board
                );
            }
        }

        // Score vertical windows
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 7; col++) {
                score += scoreWindow(
                        grid[row][col], grid[row+1][col],
                        grid[row+2][col], grid[row+3][col], board
                );
            }
        }

        // Score diagonal windows
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                score += scoreWindow(
                        grid[row][col], grid[row+1][col+1],
                        grid[row+2][col+2], grid[row+3][col+3], board
                );
            }
        }

        // Score anti-diagonal windows
        for (int row = 0; row < 3; row++) {
            for (int col = 3; col < 7; col++) {
                score += scoreWindow(
                        grid[row][col], grid[row+1][col-1],
                        grid[row+2][col-2], grid[row+3][col-3], board
                );
            }
        }
        score += scoreForks(board);
        return score;
    }

    private int scoreWindow(Cell a, Cell b, Cell c, Cell d, Board board) {
        Cell[] window = {a, b, c, d};
        int aiCount = 0;
        int oppCount = 0;
        int emptyCount = 0;

        for (Cell cell : window) {
            if (cell == colour) aiCount++;
            else if (cell == opponentColour) oppCount++;
            else emptyCount++;
        }

        if (aiCount == 4) return 100;
        if (aiCount == 3 && emptyCount == 1) return 5;
        if (aiCount == 2 && emptyCount == 2) return 2;
        if (oppCount == 3 && emptyCount == 1) return -4;

        return 0;
    }

    private int scoreForks(Board board) {
        int aiThreats = 0;
        int opponentThreats = 0;

        // count how many moves create a new threat for AI
        for (int col : Board.colsAvaliable(board)) {
            Board clone = board.cloneBoard();
            Board.dropCell(clone, col, colour);
            if (countThreats(clone, colour) >= 2) {
                aiThreats++;
            }
        }

        // count how many moves create a new threat for opponent
        for (int col : Board.colsAvaliable(board)) {
            Board clone = board.cloneBoard();
            Board.dropCell(clone, col, opponentColour);
            if (countThreats(clone, opponentColour) >= 2) {
                opponentThreats++;
            }
        }

        if (aiThreats > 0 && opponentThreats > 0) return 10; // both have forks, neutral-ish
        if (aiThreats > 0) return 20;                        // AI has a fork
        if (opponentThreats > 0) return -20;                 // opponent has a fork, punish

        return 0;
    }

    // counts how many three-in-a-row threats exist for a given colour
    private int countThreats(Board board, Cell targetColour) {
        Cell[][] grid = board.getGrid();
        int threats = 0;

        // horizontal
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 4; col++) {
                threats += isThreateningWindow(
                        grid[row][col], grid[row][col+1],
                        grid[row][col+2], grid[row][col+3],
                        targetColour
                );
            }
        }

        // vertical
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 7; col++) {
                threats += isThreateningWindow(
                        grid[row][col], grid[row+1][col],
                        grid[row+2][col], grid[row+3][col],
                        targetColour
                );
            }
        }

        // diagonal
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                threats += isThreateningWindow(
                        grid[row][col], grid[row+1][col+1],
                        grid[row+2][col+2], grid[row+3][col+3],
                        targetColour
                );
            }
        }

        // anti-diagonal
        for (int row = 0; row < 3; row++) {
            for (int col = 3; col < 7; col++) {
                threats += isThreateningWindow(
                        grid[row][col], grid[row+1][col-1],
                        grid[row+2][col-2], grid[row+3][col-3],
                        targetColour
                );
            }
        }

        return threats;
    }

    // returns 1 if this window has exactly three of targetColour and one empty
    private int isThreateningWindow(Cell a, Cell b, Cell c, Cell d, Cell targetColour) {
        Cell[] window = {a, b, c, d};
        int count = 0;
        int empty = 0;
        for (Cell cell : window) {
            if (cell == targetColour) count++;
            else if (cell == Cell.EMPTY) empty++;
        }
        return (count == 3 && empty == 1) ? 1 : 0;
    }



}