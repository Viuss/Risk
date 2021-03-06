package test.logic.state.card;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import logic.Card;

public class IsSubsetTest{

    private Card infantry; // an infantry card
    private Card cavalry; // a cavalry card
    private Card artillery; // an artillery card

    @Before
    public void setupCards(){
        infantry = new Card(1, 1, "country");
        cavalry = new Card(1, 5, "country");
        artillery = new Card(1, 10, "country");
    }

    // both lists are empty
    @Test
    public void emptyEmpty(){
        List<Card> toTradeIn = new ArrayList<Card>();
        List<Card> hand = new ArrayList<Card>();
        assertEquals(true, Card.isSubset(toTradeIn, hand));
    }

    // toTradeIn is empty
    @Test
    public void emptyTradeIn(){
        List<Card> toTradeIn = new ArrayList<Card>();
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        assertEquals(true, Card.isSubset(toTradeIn, hand));
    }

    // hand is empty
    @Test
    public void emptyHand(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        assertEquals(false, Card.isSubset(toTradeIn, hand));
    }

    // both lists contain the same card
    @Test
    public void same(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        assertEquals(true, Card.isSubset(toTradeIn, hand));
    }

    // each list has a different card
    @Test
    public void different(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(cavalry);
        assertEquals(false, Card.isSubset(toTradeIn, hand));
    }

    // the lists have one same card and one different card
    @Test
    public void almostSame(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        toTradeIn.add(cavalry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        hand.add(artillery);
        assertEquals(false, Card.isSubset(toTradeIn, hand));
    }

    // toTradeIn is bigger than hand
    @Test
    public void biggerToTradeIn(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        assertEquals(false, Card.isSubset(toTradeIn, hand));
    }

    // toTradeIn has multiple copies of a card, but hand only has one
    @Test
    public void duplicatesInToTradeIn(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        hand.add(cavalry);
        hand.add(artillery);
        assertEquals(false, Card.isSubset(toTradeIn, hand));
    }

    // both lists have duplicates of a card
    @Test
    public void bothDuplicates(){
        List<Card> toTradeIn = new ArrayList<Card>();
        toTradeIn.add(infantry);
        toTradeIn.add(infantry);
        List<Card> hand = new ArrayList<Card>();
        hand.add(infantry);
        hand.add(infantry);
        hand.add(cavalry);
        assertEquals(true, Card.isSubset(toTradeIn, hand));
    }
}
