package player;

import logic.*;
import java.util.ArrayList;

/**
 * IPlayer --- The common interface between players and game logic.
 */
public interface IPlayer {
    // IPlayer implementations must be able to respond to each of the following requests
    // Trade In Cards
    // Deploy New Armies
    // Attack OR Fortify
    // Draw Card
    // IPlayers must also be able to respond appropriately if an opposing player attacks them (A Defend request will be called).


    // For move functions, i.e. tradeInCards, placeArmies etc. Game will check if a move is valid and not allow it otherwise.

    public int getUID();
    public void setUID(int uid);

    // updatePlayer -- need a method to give player info about their hand and the board

    public void updatePlayer(Board board, ArrayList<Card> hand);

    public ArrayList<Card> tradeInCards(String requestMessage);

    // return value should be a list of 2 ints, the first int should be the ID of the territory, the second should be the number of armies to be placed
    // this will be requested until all armies have been placed
    public ArrayList<Integer> placeArmies(String requestMessage, int armiesToPlace);

    // true = yes this player wants to attack, false = do not attack
    // after this request either an attack will commence, or the game will move on
    // after the attack this will be requested again until the player no longer wants to attack
    public boolean decideAttack(String requestMessage);

    // return valie should be a list of 2 ints, the first int should be the ID of the territory you are attacking from, the second should be the id of the territory being attacked
    public ArrayList<Integer> startAttack(String requestMessage);

    // 1, 2, or 3 dice
    public int chooseAttackingDice(String requestMessage);

    // 1 or 2 dice
    public int chooseDefendingDice(String requestMessage);

    // return value should be an array list of ints, one int for each dice roll, each int should be between 1 and 6
    public ArrayList<Integer> rollDice(String requestMessage, int numDice);

    // return value should be the number of armies to move from attacking territory to defending territory.
    // It has to be at least numDice, and you must leave 1 army at the attacking territory (Game will check and enforce this)
    public int occupyTerritory(String requestMessage, int currentArmies, int numDice);
}
