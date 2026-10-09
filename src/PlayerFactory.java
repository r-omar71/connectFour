public class PlayerFactory {
    public static final int HUMAN = 1;
    public static final int AI = 2;

    public static Player createPlayer(int type, Cell colour, int difficulty) {
        switch (type) {
            case HUMAN: return new HumanPlayer(colour);
            case AI: return new AIPlayer(colour, difficulty);
            default: return new HumanPlayer(colour);
        }
    }
}

