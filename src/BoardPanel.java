import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Client: talks to GameFacade only, never to Board directly
public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 100;
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private static final int HEADER_HEIGHT = 100;

    private final GameFacade game;   // the Facade
    private int hoveredCol = -1;     // UI only: which column the mouse is over

    public BoardPanel(GameFacade game){
        this.game = game;

        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE + HEADER_HEIGHT));
        setBackground(new Color(10, 10, 28));

        // Mouse click: a human chooses a column
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (game.isGameOver())
                     return;
                if (!(game.getCurrentPlayer() instanceof HumanPlayer))
                     return;

                int col = e.getX() / CELL_SIZE;
                if (col < 0 || col >= COLS) 
                    return;

                ((HumanPlayer) game.getCurrentPlayer()).setPendingMove(col);
                processTurn();
            }
        });

        // Mouse move: update the hover indicator
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

        // Start the first turn (needed if the first player is AI)
        SwingUtilities.invokeLater(this::processTurn);
    }

        // Runs one turn: Human (already clicked) or AI (thinks in background)
    private void processTurn() {
        if (game.isGameOver()) 
            return;

        if (game.getCurrentPlayer() instanceof HumanPlayer) {
            HumanPlayer human = (HumanPlayer) game.getCurrentPlayer();
            if (!human.hasPendingMove()) 
                return;

            int col = game.requestMove();
            if (game.playMove(col)) {      // false = column full, do nothing
                afterMove();
            }

        } else {
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() {
                    // runs on background thread so UI doesn't freeze
                    return game.requestMove();
                }

                @Override
                protected void done() {
                    // runs back on UI thread when AI is done thinking
                    try {
                        game.playMove(get());
                        afterMove();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

        // UI-only reaction after a move (the logic itself is inside the Facade)
    private void afterMove() {
        repaint();
        if (game.isGameOver()) {
            showGameOverDialog();
        } else {
            processTurn();   // trigger next turn in case it's AI
        }
    }

    // Game Over window (Builder from Stage 1) + Play Again / Quit
    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            String message = game.isDraw() ? "It's a draw!" :
                    (game.getWinner() == Cell.R ? "Red" : "Yellow") + " wins!";
            Window window = SwingUtilities.getWindowAncestor(this);
            DialogBuilder gameOverBuilder = new ChoiceDialogBuilder(window);
            GameOverDialog gameOverDialog = new GameOverDialog(gameOverBuilder);
            int choice = gameOverDialog.construct(message);
            if (choice == GameOverDialog.PLAY_AGAIN) {
                game.reset();
                repaint();
                processTurn();
            } else {
                System.exit(0);
            }
        });
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

        if (game.getWinner() != null) {
            status = (game.getWinner() == Cell.R ? "Red" : "Yellow") + " wins!";
            statusColor = game.getWinner() == Cell.R ? new Color(220, 60, 60) : new Color(240, 200, 40);
        } else if (game.isDraw()) {
            status = "It's a draw!";
            statusColor = Color.LIGHT_GRAY;
        } else {
            String playerLabel = game.getCurrentPlayer().getColour() == Cell.R ? "Red" : "Yellow";
            status = playerLabel + "'s turn";
            statusColor = game.getCurrentPlayer().getColour() == Cell.R
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
        if (hoveredCol < 0 || game.isGameOver()) return;
        if (!(game.getCurrentPlayer() instanceof HumanPlayer)) return;

        Color playerColor = game.getCurrentPlayer().getColour() == Cell.R
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
                Cell cell = game.getCell(row, col);

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


