
public class PlayerFactory {
    //
    public static Player createPlayer(int mode, Cell colour, int difficulty, boolean isPlayerOne) {
       switch (mode) {
            case ModeDialog.HUMAN_VS_HUMAN:
                return new HumanPlayer(colour);
            case ModeDialog.HUMAN_VS_AI:
                if (isPlayerOne) {
                    return new HumanPlayer(colour);
                } else {
                    return new AIPlayer(colour, difficulty);
                }
            case ModeDialog.AI_VS_AI:
                return new AIPlayer(colour, 3);
            default:
                return new HumanPlayer(colour);
        }
   
  } 
}
