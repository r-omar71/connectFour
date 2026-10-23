public class PlayerFactory {
    public static final int HUMAN = 1;
    public static final int AI = 2;

    public static Player createPlayer(int type, Cell colour, int difficulty) {
        switch (type) {
            case HUMAN: return new HumanPlayer(colour);
            case AI: return createAIPlayer(colour, difficulty);
            default: return new HumanPlayer(colour);
        }
    }

    // Difficulty decides the minimax depth and which decorators wrap the AI.
    private static Player createAIPlayer(Cell colour, int difficulty) {
        switch (difficulty) {
            case 1:
                 return new TimerDecorator(new RandomMoveDecorator(new AIPlayer(colour, 2), 0.55));
            case 2:
                 return new TimerDecorator(new RandomMoveDecorator(new AIPlayer(colour, 4), 0.30));
            default:
                return new TimerDecorator(new AIPlayer(colour, 6));
        }
    }
}

