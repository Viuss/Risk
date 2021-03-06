package player;

import logic.move.Move;
import logic.move.MoveChecker;
import logic.state.Board;
import logic.state.Player;
import networking.LocalPlayerHandler;

import java.util.Map;

/**
 * IPlayer --- The common interface between players and game logic.
 */
public interface IPlayer {
    // Provides references to the board / player objects
    public void setup(Player player, Map<Integer, String> names, Board board, MoveChecker checker, LocalPlayerHandler handler);

    // Lets the player know who is currently acting, and what they are doing
    public void nextMove(String currentMove, int playerID);

    // Lets the player know when ever the gamestate changes, and describes how it changed
    public void updatePlayer(Move move);

    // Asks the player to make their move
    public void getMove(Move move);

    // Get this players name    
    public String getPlayerName();
    
    public int getPlayerid();
}
