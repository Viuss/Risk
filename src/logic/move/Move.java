package logic.move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.Card;
import logic.state.Board;

public class Move {
    public final int uid;

    public enum Stage {
        // Moves - Stages that IPlayers have to react to
        CLAIM_TERRITORY, REINFORCE_TERRITORY, TRADE_IN_CARDS, PLACE_ARMIES,
        DECIDE_ATTACK, START_ATTACK, CHOOSE_ATTACK_DICE, CHOOSE_DEFEND_DICE, OCCUPY_TERRITORY,
        DECIDE_FORTIFY, START_FORTIFY, FORTIFY_TERRITORY,
        // Events - Stages used by Game (IPlayers are only updated with these)
        END_ATTACK, PLAYER_ELIMINATED, CARD_DRAWN,
        SETUP_BEGIN, SETUP_END, GAME_BEGIN, GAME_END
    }

    private final Stage stage;
    private boolean readOnly;
    private boolean readOnlyInputs;

    public Move(int uid, Stage stage){
        this.uid = uid;
        this.stage = stage;
        this.readOnly = false;
        this.readOnlyInputs = false;
    }

    public int getUID(){
        return this.uid;
    }

    public Stage getStage(){
        return this.stage;
    }

    // CLAIM_TERRITORY, REINFORCE_TERRITORY, PLACE_ARMIES
    private int territory = -1;
    public void setTerritory(int territory) throws WrongMoveException{
        checkStage(Stage.CLAIM_TERRITORY, Stage.REINFORCE_TERRITORY, Stage.PLACE_ARMIES);
        checkPermissions();
        this.territory = territory;
    }
    public int getTerritory() throws WrongMoveException{
        checkStage(Stage.CLAIM_TERRITORY, Stage.REINFORCE_TERRITORY, Stage.PLACE_ARMIES);
        return this.territory;

    }

    // PLACE_ARMIES, OCCUPY_TERRITORY, FORTIFY_TERRITORY
    private int armies = -1;
    public void setArmies(int numArmies) throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES, Stage.OCCUPY_TERRITORY, Stage.FORTIFY_TERRITORY);
        checkPermissions();
        this.armies = numArmies;
    }
    public int getArmies() throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES, Stage.OCCUPY_TERRITORY, Stage.FORTIFY_TERRITORY);
        return this.armies;
    }

    // PLACE_ARMIES, OCCUPY_TERRITORY, FORTIFY_TERRITORY
    private int currentArmies = 0;
    public void setCurrentArmies(int numArmies) throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES, Stage.OCCUPY_TERRITORY, Stage.FORTIFY_TERRITORY);
        checkPermissions(Stage.PLACE_ARMIES, Stage.OCCUPY_TERRITORY, Stage.FORTIFY_TERRITORY);
        this.currentArmies = numArmies;
    }
    public int getCurrentArmies() throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES, Stage.OCCUPY_TERRITORY, Stage.FORTIFY_TERRITORY);
        return this.currentArmies;
    }

    // PLACE_ARMIES
    private int extraArmies = 0;
    public void setExtraArmies(int extraArmies) throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES);
        checkPermissions(Stage.PLACE_ARMIES);
        this.extraArmies = extraArmies;
    }
    public int getExtraArmies() throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES);
        return this.extraArmies;
    }

    // PLACE_ARMIES
    private List<Integer> matches = null;
    public void setMatches(List<Integer> matches) throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES);
        checkPermissions(Stage.PLACE_ARMIES);
        this.matches = new ArrayList<Integer>(matches);
    }
    public List<Integer> getMatches() throws WrongMoveException{
        checkStage(Stage.PLACE_ARMIES);
        return Collections.unmodifiableList(this.matches);
    }
    
    // TRADE_IN_CARDS
    private List<Card> toTradeIn = null;
    public void setToTradeIn(List<Card> cards) throws WrongMoveException{
        checkStage(Stage.TRADE_IN_CARDS);
        checkPermissions();
        this.toTradeIn = new ArrayList<Card>(cards);
    }
    public List<Card> getToTradeIn() throws WrongMoveException{
        checkStage(Stage.TRADE_IN_CARDS);
        return Collections.unmodifiableList(this.toTradeIn);
    }

    // DECIDE_ATTACK, DECIDE_FORTIFY
    private boolean decision = false;
    public void setDecision(boolean decision) throws WrongMoveException{
        checkStage(Stage.DECIDE_ATTACK, Stage.DECIDE_FORTIFY);
        checkPermissions();
        this.decision = decision;
    }
    public boolean getDecision() throws WrongMoveException{
        checkStage(Stage.DECIDE_ATTACK, Stage.DECIDE_FORTIFY);
        return this.decision;
    }

    // START_ATTACK, START_FORTIFY, CHOOSE_ATTACK_DICE, CHOOSE_DEFEND_DICE
    private int from = -1;
    public void setFrom(int territory) throws WrongMoveException{
        checkStage(Stage.START_ATTACK, Stage.START_FORTIFY, Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        checkPermissions(Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        this.from = territory;
    }
    public int getFrom() throws WrongMoveException{
        checkStage(Stage.START_ATTACK, Stage.START_FORTIFY, Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        return this.from;
    }

    // START_ATTACK, START_FORTIFY, CHOOSE_ATTACK_DICE, CHOOSE_DEFEND_DICE
    private int to = -1;
    public void setTo(int territory) throws WrongMoveException{
        checkStage(Stage.START_ATTACK, Stage.START_FORTIFY, Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        checkPermissions(Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        this.to = territory;
    }
    public int getTo() throws WrongMoveException{
        checkStage(Stage.START_ATTACK, Stage.START_FORTIFY, Stage.CHOOSE_ATTACK_DICE, Stage.CHOOSE_DEFEND_DICE);
        return this.to;
    }

    // CHOOSE_ATTACK_DICE, OCCUPY_TERRITORY
    private int attackDice = 0;
    public void setAttackDice(int numDice) throws WrongMoveException{
        checkStage(Stage.CHOOSE_ATTACK_DICE, Stage.OCCUPY_TERRITORY);
        checkPermissions(Stage.OCCUPY_TERRITORY);
        this.attackDice = numDice;
    }
    public int getAttackDice() throws WrongMoveException{
        checkStage(Stage.CHOOSE_ATTACK_DICE, Stage.OCCUPY_TERRITORY);
        return this.attackDice;
    }
 
    // CHOOSE_DEFEND_DICE
    private int defendDice = 0;
    public void setDefendDice(int numDice) throws WrongMoveException{
        checkStage(Stage.CHOOSE_DEFEND_DICE);
        checkPermissions();
        this.defendDice = numDice;
    }
    public int getDefendDice() throws WrongMoveException{
        checkStage(Stage.CHOOSE_DEFEND_DICE);
        return this.defendDice;
    }

    // END_ATTACK
    private int attackerLosses = 0;
    public void setAttackerLosses(int numLosses) throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        checkPermissions(Stage.END_ATTACK);
        this.attackerLosses = numLosses;
    }
    public int getAttackerLosses() throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        return this.attackerLosses;
    }

    // END_ATTACK
    private int defenderLosses = 0;
    public void setDefenderLosses(int numLosses) throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        checkPermissions(Stage.END_ATTACK);
        this.defenderLosses = numLosses;
    }
    public int getDefenderLosses() throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        return this.defenderLosses;
    }

    // END_ATTACK
    private List<Integer> attackDiceRolls = null;
    public void setAttackDiceRolls(List<Integer> results) throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        checkPermissions(Stage.END_ATTACK);
        this.attackDiceRolls = Collections.unmodifiableList(new ArrayList<Integer>(results));
    }
    public List<Integer> getAttackDiceRolls() throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        return this.attackDiceRolls;
    }

    // END_ATTACK
    private List<Integer> defendDiceRolls = null;
    public void setDefendDiceRolls(List<Integer> results) throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        checkPermissions(Stage.END_ATTACK);
        this.defendDiceRolls = Collections.unmodifiableList(new ArrayList<Integer>(results));
    }
    public List<Integer> getDefendDiceRolls() throws WrongMoveException{
        checkStage(Stage.END_ATTACK);
        return this.defendDiceRolls;
    }

    // SETUP_BEGIN, PLAYER_ELIMINATED, GAME_END
    private int player = -1;
    public void setPlayer(int player) throws WrongMoveException{
        checkStage(Stage.SETUP_BEGIN, Stage.PLAYER_ELIMINATED, Stage.GAME_END);
        checkPermissions(Stage.SETUP_BEGIN, Stage.PLAYER_ELIMINATED, Stage.GAME_END);
        this.player = player;
    }
    public int getPlayer() throws WrongMoveException{
        checkStage(Stage.SETUP_BEGIN, Stage.PLAYER_ELIMINATED, Stage.GAME_END);
        return this.player;
    }

    // GAME_END
    private int turns = -1;
    public void setTurns(int turns) throws WrongMoveException{
        checkStage(Stage.GAME_END);
        checkPermissions(Stage.GAME_END);
        this.turns = turns;
    }
    public int getTurns() throws WrongMoveException{
        checkStage(Stage.GAME_END);
        return this.turns;
    }

    private void checkStage(Stage... stages) throws WrongMoveException{
        boolean ok = false;
        for(Stage s : stages){
            if(this.stage == s){
                ok = true;
            }

        }
        if(!ok){
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            String callingMethod = ste[2].getMethodName();
            String message = String.format("%s cannot be accessed from stage %s.", callingMethod, Move.stageName(stage));
            throw new WrongMoveException(message);
        }
    }

    public void setReadOnly(){
        this.readOnly = true;
    }

    public void setReadOnlyInputs(){
        this.readOnlyInputs = true;
    }

    /**
     * Stops players from writing to read only moves, and from editing the inputs received from Game.
     */
    private void checkPermissions(Stage... stages) throws WrongMoveException{
        if(readOnly){
            throw new WrongMoveException("Attempted to write to a read only move.");
        }
        if(readOnlyInputs){
            for(Stage s : stages){
                if(s == this.stage){
                    throw new WrongMoveException("Attempted to write to an input only field.");
                }
            }
        }
    }

    // Returns a string describing what has just happened.
    public static String describeMove(Move move, Board board){
        int uid = move.getUID();
        try{
            String message;
            switch(move.getStage()){
                case CLAIM_TERRITORY:
                    int claimedTerritory = move.getTerritory();
                    String claimedTerritoryName = board.getName(claimedTerritory);
                    message = String.format("Player %d has claimed territory [%d-%s].\n", uid, claimedTerritory, claimedTerritoryName);
                    return message;
                case REINFORCE_TERRITORY:
                    int reinforcedTerritory = move.getTerritory();
                    String reinforcedTerritoryName = board.getName(reinforcedTerritory);
                    message = String.format("Player %d has reinforced territory [%d-%s].\n", uid, reinforcedTerritory, reinforcedTerritoryName);
                    return message;
                case TRADE_IN_CARDS:
                    List<Card> toTradeIn = move.getToTradeIn();
                    if(toTradeIn.size() > 0){
                        String handMessage = Card.printHand(null, toTradeIn);
                        message = String.format("Player %d has traded in %s", uid, handMessage);
                    }else{
                        message = String.format("Player %d has not traded in any cards.\n", uid);
                    }
                    return message;
                case PLACE_ARMIES:
                    int placeArmiesTerritory = move.getTerritory();
                    int placeArmiesNum = move.getArmies();
                    String placeArmiesName = board.getName(placeArmiesTerritory);
                    message = String.format("Player %d has placed %d armies at [%d-%s].\n", uid, placeArmiesNum, placeArmiesTerritory, placeArmiesName); 
                    return message;
                case DECIDE_ATTACK:
                    boolean decideAttack = move.getDecision();
                    if(decideAttack){
                        message = String.format("Player %d has chosen to attack.\n", uid);
                    }else{
                        message = String.format("Player %d has chosen not to attack.\n", uid);
                    }
                    return message;
                case START_ATTACK:
                    int attackFrom = move.getFrom();
                    int attackTo = move.getTo();
                    String attackFromName = board.getName(attackFrom);
                    String attackToName = board.getName(attackTo);
                    int enemyUID = board.getOwner(attackTo);
                    message = String.format("Player %d is attacking Player %d owned territory [%d-%s] from [%d-%s].\n", uid, enemyUID, attackTo, attackToName, attackFrom, attackFromName);
                    return message;
                case CHOOSE_ATTACK_DICE:
                    int numAttackingDice = move.getAttackDice();
                    message = String.format("Player %d has chosen to attack with %d dice.\n", uid, numAttackingDice);
                    return message;
                case CHOOSE_DEFEND_DICE:
                    int numDefendingDice = move.getDefendDice();
                    message = String.format("Player %d has chosen to defend with %d dice.\n", uid, numDefendingDice);
                    return message;
                case OCCUPY_TERRITORY:
                    int numOccupyArmies = move.getArmies();
                    message = String.format("Player %d was successful in their attack and has moved %d armies forward.\n", uid, numOccupyArmies);
                    return message;
                case DECIDE_FORTIFY:
                    boolean decideFortify = move.getDecision();
                    if(decideFortify){
                        message = String.format("Player %d has chosen to fortify.\n", uid);
                    }else{
                        message = String.format("Player %d has chosen not to fortify.\n", uid);
                    }
                    return message;
                case START_FORTIFY:
                    int fortifyFrom = move.getFrom();
                    int fortifyTo = move.getTo();
                    String fortifyFromName = board.getName(fortifyFrom);
                    String fortifyToName = board.getName(fortifyTo);
                    message = String.format("Player %d is fortifying [%d-%s] from [%d-%s].\n", uid, fortifyTo, fortifyToName, fortifyFrom, fortifyFromName);
                    return message;
                case FORTIFY_TERRITORY:
                    int numFortifyArmies = move.getArmies();
                    message = String.format("Player %d has fortified with %d armies.\n", uid, numFortifyArmies);
                    return message;
                case END_ATTACK:
                    int attackerLosses = move.getAttackerLosses();
                    int defenderLosses = move.getDefenderLosses();
                    List<Integer> attackDiceRolls = move.getAttackDiceRolls();
                    message = "Dice Rolls : Attacker - ";
                    for(int r : attackDiceRolls){
                        message += String.format("(%d) ", r);
                    }
                    message += "- Defender - ";
                    List<Integer> defendDiceRolls = move.getDefendDiceRolls();
                    for(int r : defendDiceRolls){
                        message += String.format("(%d) ", r);
                    }
                    message += "\n";
                    message += String.format("The attacker lost %d armies, the defender lost %d armies.\n", attackerLosses, defenderLosses);
                    return message;
                case PLAYER_ELIMINATED:
                    int eliminatedPlayer = move.getPlayer();
                    message = String.format("Player %d has just been eliminated by player %d.\n", eliminatedPlayer, uid);
                    return message;
                case CARD_DRAWN:
                    message = String.format("Player %d has drawn a card.\n", uid);
                    return message;
                case SETUP_BEGIN:
                    int numPlayers = move.getPlayer();
                    message = String.format("Game setup is beginning with %d players, player %d is first to go.\n", uid, numPlayers);
                    return message;
                case SETUP_END:
                    return "Game setup has ended.\n";
                case GAME_BEGIN:
                    message = String.format("Game is beginning, player %d is first to go.\n", uid);
                    return message;
                case GAME_END:
                    int turns = move.getTurns();
                    int winner = move.getPlayer();
                    message = String.format("Game has ended after %d turns, player %d is the winner!\n", turns, winner);
                    return message;
                default:
                     return "";
            }
        }catch(WrongMoveException e){
            System.out.println("Move is describing a move incorrectly.");
            return "";
        }
    }

    // Returns a string describe what the player is about to do. (To be displayed while waiting)
    public static String describeStatus(Move move){
        int uid = move.getUID();
        Stage stage = move.getStage();
        String message = "";
        switch(stage){
            case CLAIM_TERRITORY:
                message = String.format("Player %d is claiming a territory.", uid);
                break;
            case REINFORCE_TERRITORY:
                message = String.format("Player %d is reinforcing a territory.", uid);
                break;
            case TRADE_IN_CARDS:
                message = String.format("Player %d is trading in cards.", uid);
                break;
            case PLACE_ARMIES:
                message = String.format("Player %d is placing armies.", uid);
                break;
            case DECIDE_ATTACK:
                message = String.format("Player %d is deciding whether or not to attack.", uid);
                break;
            case START_ATTACK:
                message = String.format("Player %d is choosing where to attack.", uid);
                break;
            case CHOOSE_ATTACK_DICE:
                message = String.format("Player %d is deciding how many dice to attack with.", uid);
                break;
            case CHOOSE_DEFEND_DICE:
                message = String.format("Player %d is deciding how many dice to defend with.", uid);
                break;
            case OCCUPY_TERRITORY:
                message = String.format("Player %d is deciding how many armies to move into the captured territory.", uid);
                break;
            case DECIDE_FORTIFY:
                message = String.format("Player %d is deciding whether or not to fortify.", uid);
                break;
            case START_FORTIFY:
                message = String.format("Player %d is choosing where to fortify.", uid);
                break;
            case FORTIFY_TERRITORY:
                message = String.format("Player %d is deciding how many armies to fortify with.", uid);
                break;
            default:
                break;
        }
        return message;
    }

    // Returns a string describing what has just happened.
    public static String stageName(Stage s){
        switch(s){
            case CLAIM_TERRITORY:
                return "CLAIM_TERRITORY";
            case REINFORCE_TERRITORY:
                return "REINFORCE_TERRITORY";
            case TRADE_IN_CARDS:
                return "TRADE_IN_CARDS";
            case PLACE_ARMIES:
                return "PLACE_ARMIES";
            case DECIDE_ATTACK:
                return "DECIDE_ATTACK";
            case START_ATTACK:
                return "START_ATTACK";
            case CHOOSE_ATTACK_DICE:
                return "CHOOSE_ATTACK_DICE";
            case CHOOSE_DEFEND_DICE:
                return "CHOOSE_DEFEND_DICE";
            case OCCUPY_TERRITORY:
                return "OCCUPY_TERRITORY";
            case DECIDE_FORTIFY:
                return "DECIDE_FORTIFY";
            case START_FORTIFY:
                return "START_FORTIFY";
            case FORTIFY_TERRITORY:
                return "FORTIFY_TERRITORY";
            case END_ATTACK:
                return "END_ATTACK";
            case PLAYER_ELIMINATED:
                return "PLAYER_ELIMINATED";
            case CARD_DRAWN:
                return "CARD_DRAWN";
            case SETUP_BEGIN:
                return "SETUP_BEGIN";
            case SETUP_END:
                return "SETUP_END";
            case GAME_BEGIN:
                return "GAME_BEGIN";
            case GAME_END:
                return "GAME_END";
        }
        return null;
    }
}