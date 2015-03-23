package player;

import logic.move.Move;
import logic.move.MoveChecker;
import logic.move.WrongMoveException;
import logic.state.Board;
import logic.state.Player;

/**
 * IPlayer --- The common interface between players and game logic.
 */
public interface IPlayer {
    // Provides references to the board / player objects
    public void setup(Player player, Board board, MoveChecker checker);

    // Lets the player know who is currently acting, and what they are doing
    public void nextMove(String currentMove);

    // Lets the player know when ever the gamestate changes, and describes how it changed
    public void updatePlayer(Move move) throws WrongMoveException;

    // Asks the player to make their move
    public void getMove(Move move) throws WrongMoveException;
}
