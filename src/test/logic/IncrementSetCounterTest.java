package test.logic;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;

import logic.*;
import player.*;

public class IncrementSetCounterTest{

    private static Game game;

    @BeforeClass
    public static void setupGame(){
        ArrayList<IPlayer> players = new ArrayList<IPlayer>();
        game = new Game(players, 0, 0, "resources/risk_map.json");
    }

    @Test
    public void testSetCounter(){
        int expectedValues[] = {4, 6, 8, 10, 12, 15, 20, 25};
        for(int i : expectedValues){
            assertEquals(i, game.incrementSetCounter());
        }
    } 
}