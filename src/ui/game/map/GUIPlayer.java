package ui.game.map;

import java.io.*;
import java.util.*;

import logic.move.Move;
import logic.move.MoveChecker;
import logic.state.Board;
import logic.state.Player;
import player.IPlayer;
import player.PlayerController;
import ui.game.GameController;
import ui.game.Main;

/**
 * GUIPlayer
 */
public class GUIPlayer implements IPlayer {
    private PlayerController controller;
    private Scanner reader;
    private PrintWriter writer;

    public Main gui = new Main();

    private Board board;
    private Player player;

    public GUIPlayer(PlayerController controller, Scanner reader, PrintWriter writer){
        this.controller = controller;
        this.reader = reader;
        this.writer = writer;
    }

    public void setup(Player player, Board board, MoveChecker checker){
        this.board = board;
        this.player = player;
    } 

    public void nextMove(String move){
        writer.println(move); 
    }

    public void updatePlayer(Move move){
        String message = Move.describeMove(move, board);
        
        GameController controller = (GameController) gui.getLoader().getController();
        
        controller.console.write(message);
    }

    public void getMove(Move move){
        controller.getMove(move); 
    }
}

