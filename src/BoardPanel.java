import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 100;
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int HEADER_HEIGHT = 100;

    private final Board board;
    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;

    private Cell winner = null;
    private boolean isDraw = false;
    private int hoveredCol = -1;

    public BoardPanel(Board board, Player playerOne, Player playerTwo) {
        this.board = board;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = playerOne;

        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE + HEADER_HEIGHT));
        setBackground(new Color(10, 10, 28));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (winner != null || isDraw) return;
                if (!(currentPlayer instanceof HumanPlayer)) return;

                int col = e.getX() / CELL_SIZE;
                if (col < 0 || col >= COLS) return;

                ((HumanPlayer) currentPlayer).setPendingMove(col);
                processTurn();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = e.getX() / CELL_SIZE;
                if (col != hoveredCol) {
                    hoveredCol = (col >= 0 && col < COLS) ? col : -1;
                    repaint();
                }
            }
        });
        //
        SwingUtilities.invokeLater(this::processTurn);
    }

    private void processTurn() {
        if (winner != null || isDraw) return;

        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer human = (HumanPlayer) currentPlayer;
            if (!human.hasPendingMove()) return;

            int col = human.getMove(board);
            try {
                Board.dropCell(board, col, currentPlayer.getColour());
                checkGameState();
                swapPlayer();
                repaint();
                // trigger AI immediately if next player is AI
                processTurn();
            } catch (IllegalArgumentException ex) {
                // column full, do nothing
            }

        } else if (currentPlayer instanceof AIPlayer) {
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() {
                    // runs on background thread so UI doesn't freeze
                    return currentPlayer.getMove(board);
                }

                @Override
                protected void done() {
                    // runs back on UI thread when AI is done thinking
                    try {
                        int col = get();
                        Board.dropCell(board, col, currentPlayer.getColour());
                        checkGameState();
                        swapPlayer();
                        repaint();
                        // trigger next turn in case it's AI vs AI
                        processTurn();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void checkGameState() {
        winner = Board.isGameWon(board);
        if (winner == Cell.EMPTY) winner = null;
        if (winner == null && Board.isGridFull(board)) isDraw = true;

        if (winner != null || isDraw) {
            SwingUtilities.invokeLater(() -> {
                String message = isDraw ? "It's a draw!" :
                        (winner == Cell.R ? "Red" : "Yellow") + " wins!";
                Window window = SwingUtilities.getWindowAncestor(this);
                DialogBuilder gameOverBuilder = new ChoiceDialogBuilder(window);
                GameOverDialog gameOverDialog = new GameOverDialog(gameOverBuilder);
                int choice = gameOverDialog.construct(message);
                if (choice == GameOverDialog.PLAY_AGAIN) {
                    resetGame();
                } else {
                    System.exit(0);
                }
            });
        }
    }

    private void swapPlayer() {
        if (winner != null || isDraw) return;
        currentPlayer = (currentPlayer == playerOne) ? playerTwo : playerOne;
    }

    private void resetGame() {
        board.resetBoard();
        winner = null;
        isDraw = false;
        currentPlayer = playerOne;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawHeader(g2);
        drawHoverIndicator(g2);
        drawBoard(g2);
    }

    private void drawHeader(Graphics2D g2) {
        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Georgia", Font.BOLD, 30));
        FontMetrics titleMetrics = g2.getFontMetrics();
        String title = "CONNECT FOUR";
        int titleX = (COLS * CELL_SIZE - titleMetrics.stringWidth(title)) / 2;
        g2.drawString(title, titleX, 40);

        // Status
        String status;
        Color statusColor;

        if (winner != null) {
            status = (winner == Cell.R ? "Red" : "Yellow") + " wins!";
            statusColor = winner == Cell.R ? new Color(220, 60, 60) : new Color(240, 200, 40);
        } else if (isDraw) {
            status = "It's a draw!";
            statusColor = Color.LIGHT_GRAY;
        } else {
            String playerLabel = currentPlayer.getColour() == Cell.R ? "Red" : "Yellow";
            status = playerLabel + "'s turn";
            statusColor = currentPlayer.getColour() == Cell.R
                    ? new Color(220, 60, 60)
                    : new Color(240, 200, 40);
        }

        // Coloured dot
        g2.setColor(statusColor);
        int dotX = COLS * CELL_SIZE / 2 - 110;
        g2.fillOval(dotX, 54, 18, 18);

        // Status text
        g2.setFont(new Font("Georgia", Font.PLAIN, 18));
        g2.drawString(status, dotX + 26, 68);
    }

    private void drawHoverIndicator(Graphics2D g2) {
        if (hoveredCol < 0 || winner != null || isDraw) return;
        if (!(currentPlayer instanceof HumanPlayer)) return;

        Color playerColor = currentPlayer.getColour() == Cell.R
                ? new Color(220, 60, 60, 160)
                : new Color(240, 200, 40, 160);

        g2.setColor(playerColor);
        g2.fillOval(
                hoveredCol * CELL_SIZE + 10,
                HEADER_HEIGHT - CELL_SIZE + 10,
                CELL_SIZE - 20,
                CELL_SIZE - 20
        );
    }

    private void drawBoard(Graphics2D g2) {
        // Board shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(6, HEADER_HEIGHT + 6, COLS * CELL_SIZE, ROWS * CELL_SIZE, 18, 18);

        // Board background
        g2.setColor(new Color(25, 70, 185));
        g2.fillRoundRect(0, HEADER_HEIGHT, COLS * CELL_SIZE, ROWS * CELL_SIZE, 18, 18);

        // Cells
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Cell cell = Board.getCell(board.getGrid(), row, col);

                // Cell shadow
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillOval(
                        col * CELL_SIZE + 11,
                        HEADER_HEIGHT + row * CELL_SIZE + 11,
                        CELL_SIZE - 20,
                        CELL_SIZE - 20
                );

                if (cell == Cell.R) {
                    g2.setColor(new Color(220, 60, 60));
                } else if (cell == Cell.Y) {
                    g2.setColor(new Color(240, 200, 40));
                } else {
                    g2.setColor(new Color(10, 10, 28));
                }

                g2.fillOval(
                        col * CELL_SIZE + 10,
                        HEADER_HEIGHT + row * CELL_SIZE + 10,
                        CELL_SIZE - 20,
                        CELL_SIZE - 20
                );

                // Shine effect on empty cells
                if (cell == Cell.EMPTY) {
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fillOval(
                            col * CELL_SIZE + 18,
                            HEADER_HEIGHT + row * CELL_SIZE + 14,
                            (CELL_SIZE - 20) / 2,
                            (CELL_SIZE - 20) / 3
                    );
                }
            }
        }
    }
}