package player;

import java.io.*;
import java.util.*;
import logic.*;

/**
 * CommandLinePlayer --- Asks the user to make moves on the command line.
 */
public class CommandLinePlayer implements IPlayer {
    private static Random random = new Random();

    private Scanner reader;
    private PrintWriter writer;

    public CommandLinePlayer(Scanner reader, PrintWriter writer){
        this.reader = reader;
        this.writer = writer;
    }

    private int uid = 0;
    public int getUID(){
        return this.uid;
    }
    public void setUID(int uid){
        this.uid = uid;

    }
    private boolean eliminated = false;
    public boolean isEliminated(){
        return eliminated;
    }
    public void eliminate(){
        eliminated = true;
    }

    private Board board;
    private ArrayList<Card> hand;
    public void updatePlayer(Board board, ArrayList<Card> hand, int currentPlayer, String currentStage){
        this.board = board;
        this.hand = hand;
//        try{
//        Thread.sleep(100);
//        }catch(Exception e){
//        }
        writer.format("%d is currently %s\n", currentPlayer, currentStage);
        writer.flush();
    }

    public int claimTerritory(String requestMessage){
        int tid = random.nextInt(board.getTerritories().size());
        while(!(board.checkTerritoryOwner(-1, tid))){
            tid = random.nextInt(board.getTerritories().size());
        }
        return tid;
    }

    public int reinforceTerritory(String requestMessage, int uid){
        int tid = random.nextInt(board.getTerritories().size());
        while(!(board.checkTerritoryOwner(uid, tid))){
            tid = random.nextInt(board.getTerritories().size());
        }
        return tid;
    }


    public ArrayList<Card> tradeInCards(String requestMessage){ 
        ArrayList<Card> toTradeIn = new ArrayList<Card>();
        int handSize = hand.size();
        if(handSize >= 5){ // Doesn't guarantee that a set will be chosen, Game can do that.
            for(int i = 0; i != 3; ++i){
                int randomCard = random.nextInt(hand.size());
                Card c = hand.get(randomCard);
                hand.remove(randomCard);
                toTradeIn.add(c);
            } 
        }
        return toTradeIn;
    }

    public ArrayList<Integer> placeArmies(String requestMessage, int armiesToPlace){
        ArrayList<Integer> move = new ArrayList<Integer>();
        int randomTerritory = random.nextInt(board.getTerritories().size());
        while(!board.checkTerritoryOwner(uid, randomTerritory)){
            randomTerritory = random.nextInt(board.getTerritories().size());
        }
        move.add(randomTerritory);
        int randomArmies = random.nextInt(armiesToPlace+1); // Can't place 0 armies
        move.add(randomArmies);
        return move;
    }

    public boolean decideAttack(String requestMessage){
        return random.nextBoolean();
    }

    public ArrayList<Integer> startAttack(String requestMessage){
        ArrayList<Integer> move = new ArrayList<Integer>();
        int randomAlly = random.nextInt(board.getTerritories().size());
        while(!board.checkTerritoryOwner(uid, randomAlly) || board.getTerritories().get(randomAlly).getArmies() < 2){
            randomAlly = random.nextInt(board.getTerritories().size());
        }
        ArrayList<Integer> adjacents = board.getTerritories().get(randomAlly).getLinks();
        int randomEnemy = adjacents.get(random.nextInt(adjacents.size())); // Doesn't guarantee that an enemy is chosen, Game can do that // Adjacents might not even have an enemy in it
        move.add(randomAlly);
        move.add(randomEnemy);
        return move;
    }

    // Game won't let it pick a wrong number
    public int chooseAttackingDice(String requestMessage){
        return random.nextInt(3)+1;
    }

    // Game won't let it pick a wrong number
    public int chooseDefendingDice(String requestMessage){
        return random.nextInt(2)+1;
    }

    public ArrayList<Integer> rollDice(String requestMessage, int numDice){
        ArrayList<Integer> roll = new ArrayList<Integer>();
        for(int i = 0; i != numDice; ++i){
            roll.add(random.nextInt(6)+1);
        }
        return roll;
    }

    public int occupyTerritory(String requestMessage, int currentArmies, int numDice){
        int decision = -1;
        while(decision < numDice){
            decision = random.nextInt(currentArmies-1)+1;
        }
        return decision;
    }

    public boolean decideFortify(String requestMessage){
        return random.nextBoolean();
    }

    public ArrayList<Integer> startFortify(String requestMessage){
        ArrayList<Integer> move = new ArrayList<Integer>();
        int randomAlly = random.nextInt(board.getTerritories().size());;
        while(!board.checkTerritoryOwner(uid, randomAlly) || board.getTerritories().get(randomAlly).getArmies() < 2){
            randomAlly = random.nextInt(board.getTerritories().size());
        }
        ArrayList<Integer> adjacents = board.getTerritories().get(randomAlly).getLinks();
        int randomFortify = adjacents.get(random.nextInt(adjacents.size())); // Doesn't guarantee that an ally is chosen, Game can do that
        move.add(randomAlly);
        move.add(randomFortify);
        return move;
    }

    public int chooseFortifyArmies(String requestMessage, int currentArmies){
        return random.nextInt(currentArmies-1)+1;
    }
}

