package lobby;

import lobby.handler.LobbyEventHandler;
import logic.state.Deck;
import networking.GameRouter;
import networking.NetworkClient;
import networking.networkplayer.NetworkPlayer;
import player.IPlayer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LobbyUtil {

    static Logger logger = Logger.getLogger("LobbyUtil");

    // TODO Revisit this to simplify?
    public static void createIPlayersInOrder(List<NetworkClient> clients, int firstID, int ourPlayerID, List<IPlayer> playersBefore, List<IPlayer> playersAfter) {

        Collections.sort(clients, new NetworkClientComparator());

        LinkedList<Integer> playeridOrder = new LinkedList<>();

        int rotationAmount = -1;
        int i = 0;

        boolean passedOurPlayerID = false;

        for(NetworkClient client : clients) {

            // Insert our player ID if we haven't already, and the current player's ID is more than ours.
            if(!passedOurPlayerID && client.playerid > ourPlayerID) {
                if(ourPlayerID == firstID) {
                    rotationAmount = i;
                }
                playeridOrder.add(ourPlayerID);
                i++;
                passedOurPlayerID = true;
            }

            if(client.playerid == firstID) {
                rotationAmount = i;
            }

            playeridOrder.add(client.playerid);
            i++;
        }

        if(!passedOurPlayerID) {
            playeridOrder.add(ourPlayerID);
        }

        Collections.rotate(playeridOrder, -rotationAmount);

        boolean after = false;

        for(int playerid : playeridOrder) {
            // If we've skipped over our playerid, we should start inserting into the after list now.
            if(playerid == ourPlayerID) {
                after = true;
                continue;
            }

            for(NetworkClient client : clients) {
                if(client.playerid != playerid) {
                    // skip, this is not the player we want to insert into the list next.
                    continue;
                }

                NetworkPlayer player = new NetworkPlayer(client, client.playerid, ourPlayerID);

                // If we've passed our playerid, add to playersAfter
                if(!after) {
                    playersBefore.add(player);
                    break;
                }
                else {
                    playersAfter.add(player);
                    break;
                }
            }
        }
    }

    static class NetworkClientComparator implements Comparator<NetworkClient> {

        @Override
        public int compare(NetworkClient c1, NetworkClient c2) {
            return c1.playerid - c2.playerid;
        }
    }

    public static int decidePlayerOrder(GameRouter router, int ourPlayerid, List<NetworkClient> otherPlayers, LobbyEventHandler handler) {
        int result;
        try {
            int numplayers = otherPlayers.size() + 1;
            result = LobbyDiceRoll.rollDice(router, ourPlayerid, otherPlayers, 1, numplayers).get(0);
        } catch (LobbyDiceRoll.DiceRollException e) {
            handler.onFailure(e);
            return -1;
        }

        result -= 1; // We want to index from 0, not 1.

        // Convert from the player number, to the player ID.
        // Required because player IDs might not be continuous at this point.
        // Eg 2nd player goes first. Playerids = (0, 5, 6). Playerid must be 5.

        List<Integer> playerids = new LinkedList<>();
        playerids.add(ourPlayerid);

        for(NetworkClient client : otherPlayers) {
            playerids.add(client.playerid);
        }

        Collections.sort(playerids);

        int playerid = playerids.get(result);

        logger.log(Level.FINE, "Dice roll outcome: " + result + " playerid plays first");

        return playerid;
    }

    public static void shuffleCards(GameRouter router, int ourPlayerid, List<NetworkClient> otherPlayers, Deck deck, LobbyEventHandler handler) {
        List<Integer> randomNumbers;
        try {
            randomNumbers = LobbyDiceRoll.rollDice(router, ourPlayerid, otherPlayers, deck.getNumCards(), deck.getNumCards());
        } catch (LobbyDiceRoll.DiceRollException e) {
            handler.onFailure(e);
            throw new RuntimeException("Cannot roll dice to shuffle cards.");
        }

        int i = 0;
        for (int num : randomNumbers) {
            num -= 1; // Dice roll code produces 1-indexed numbers, we need zero-indexed.

            deck.shuffleSwap(i++, num);
        }
    }
}
