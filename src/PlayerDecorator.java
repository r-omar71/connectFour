// Decorator: wraps a Player and forwards calls to it.
// Concrete decorators extend this class and add one behaviour each.
public abstract class PlayerDecorator implements Player {

    protected Player decoratedPlayer;

    public PlayerDecorator(Player decoratedPlayer) {
        this.decoratedPlayer = decoratedPlayer;
    }

    @Override
    public int getMove(Board board) {
        return decoratedPlayer.getMove(board);
    }

    @Override
    public Cell getColour() {
        return decoratedPlayer.getColour();
    }
}
