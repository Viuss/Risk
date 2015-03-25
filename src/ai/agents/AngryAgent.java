package ai.agent;

import ai.strategy.AggressiveStrategy;
import ai.strategy.RandomStrategy;
import logic.move.Move;
import logic.state.Board;
import logic.state.Player;

public class AngryAgent extends Agent {

    private AggressiveStrategy as;
    private RandomStrategy rs;

    public AngryAgent(){
    }

    public void setup(Player player, Board board){
        super.setup(player, board);
        as = new AggressiveStrategy(player, board, random);
        rs = new RandomStrategy(player, board, random);
    }

    public String getName(){
        return "Angry";
    }

    public String getDescription(){
        return "Always attacks, fortifies outwards.";
    }

    public void getMove(Move move){
        switch(move.getStage()){
            case CLAIM_TERRITORY:
                rs.getMove(move);
                return;
            case REINFORCE_TERRITORY:
                rs.getMove(move);
                return;
            case TRADE_IN_CARDS:
                rs.getMove(move);
                return;
            case PLACE_ARMIES:
                rs.getMove(move);
                return;
            case DECIDE_ATTACK:
                as.getMove(move);
                return;
            case START_ATTACK:
                rs.getMove(move);
                return;
            case CHOOSE_ATTACK_DICE:
                as.getMove(move);
                return;
            case CHOOSE_DEFEND_DICE:
                as.getMove(move);
                return;
            case OCCUPY_TERRITORY:
                as.getMove(move);
                return;
            case DECIDE_FORTIFY:
                as.getMove(move);
                return;
            case START_FORTIFY:
                as.getMove(move);
                return;
            case FORTIFY_TERRITORY:
                as.getMove(move);
                return;
            default:
                assert false : move.getStage();
        }
    }
}
