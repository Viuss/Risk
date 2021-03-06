package lobby;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import networking.message.payload.*;
import player.IPlayer;
import settings.Settings;
import lobby.handler.JoinLobbyEventHandler;
import lobby.handler.LobbyEventHandler;
import logic.state.Board;
import logic.state.Deck;
import networking.*;
import networking.message.*;
import networking.parser.Parser;
import networking.parser.ParserException;

/**
 * IRemoteGameLobby: A remote network game lobby which could be joined.
 * @author James
 *
 */
public class RemoteGameLobby extends Thread {

    JoinLobbyEventHandler handler;
    final InetAddress address;
    final int port;
    final String name;

    int playerid = -1;

    boolean nonPlayingHost;

    private Deck deck;

    // TODO Implement timeouts in networking

    public RemoteGameLobby(InetAddress address, int port, JoinLobbyEventHandler handler, String name) {
        this.address = address;
        this.port = port;
        this.handler = handler;
        this.name = name;
    }

    public void run() {
        try {
            joinLobby();
        }
        catch(Throwable e) {
            handler.onFailure(e);
        }
    }

    private void joinLobby() throws IOException {

        IConnection conn = tcpConnect(address, port);
        handler.onTCPConnect();

        sendJoinGame(conn);

        if(!handleJoinGameResponse(conn)) { // callbacks: onJoinAccepted or onJoinRejected
            return;
        }
        // Now we have a playerid

        // Signifies we have received a PING message from the host.
        List<NetworkClient> otherPlayers = new LinkedList<>();
        int numPlayers = -1;
        
        GameRouter router = new GameRouter();

        while(numPlayers == -1) {
            numPlayers = handleHostMessage(router, conn, otherPlayers);
        }

        int hostPlayerID = LocalGameLobby.HOST_PLAYERID;
        if(nonPlayingHost) {
        	hostPlayerID = -1;
        }

        NetworkClient host = getHost(hostPlayerID, otherPlayers);
        if(host == null) {
        	host = new NetworkClient(router, hostPlayerID, "Host");
        }
        host.setHost();
        router.addRoute(host, conn);
        
        router.startRouting(); // beyond this point messages from the socket will be piped to the correct NetworkClient

        int firstPlayer;
        try {
            handlePings(router, host, otherPlayers); // callbacks: onPingReceive

            if(!handleReady(router, host, otherPlayers)) {
                return; // callbacks: onReady + onReadyAcknowledge
            }

            if (!receiveInitialiseGame(router, host)) {
                return;
            }

            firstPlayer = LobbyUtil.decidePlayerOrder(router, playerid, otherPlayers, handler); // callbacks: onDicePlayerOrder + onDiceHash + onDiceNumber

            if(firstPlayer < 0) {
                return;
            }

            Board board = new Board();
            this.deck = board.getDeck();
            LobbyUtil.shuffleCards(router, playerid, otherPlayers, deck, handler); // ??? // callbacks: onDiceCardShuffle + onDiceHash + onDiceNumber

        } catch(InterruptedException e) {
            // TODO Tidy up logging: Log exception?
            handler.onFailure(e);
            return;
        }

        LinkedList<IPlayer> playersBefore = new LinkedList<>();
        LinkedList<IPlayer> playersAfter = new LinkedList<>();
        LobbyUtil.createIPlayersInOrder(otherPlayers, firstPlayer, playerid, playersBefore, playersAfter);

        handler.onLobbyComplete(playersBefore, playersAfter, deck, new NetworkLocalPlayerHandler(router));
    }

    private NetworkClient getHost(int hostPlayerID, List<NetworkClient> otherPlayers) {
		for(NetworkClient client : otherPlayers) {
			if(client.playerid == hostPlayerID) {
				 return client;
			}
		}
		
		return null;
	}

	/**
     * Receives a message and processes it for the (PLAYERS_JOINED or PING) stage of the lobby

     * @return numPlayers in the game if a PING message was received. Otherwise -1
     */
    private int handleHostMessage(GameRouter router, IConnection conn, List<NetworkClient> otherPlayers) {

        Message msg;
        try {
            msg = Networking.readMessage(conn);
        } catch (TimeoutException | ConnectionLostException | ParserException e) {
            e.printStackTrace();
            handler.onFailure(e);
            throw new RuntimeException("Invalid message received from host");
        }

        if(msg.command == Command.PLAYERS_JOINED) {
            PlayersJoinedPayload payload = (PlayersJoinedPayload) msg.payload;

            for(PlayersJoinedPayload.PlayerInfo info : payload.info) {
                if(info.playerid == this.playerid) {
                    continue; // Skip over our playerid.
                }

                handler.onPlayerJoin(info.playerid, info.name);

                updateNetworkClientInfo(router, conn, info.playerid, info.name, otherPlayers);
            }

            return -1;
        }
        else if (msg.command == Command.PING) {

            handler.onPingStart();
            int numplayers = processHostPingMessage(msg);

            if(numplayers < 2) {
                throw new RuntimeException("Invalid number of players received from host. " + numplayers);
            }

            return numplayers;
        }

        // Unknown message. Ignore
        return -1;
    }

    private void updateNetworkClientInfo(GameRouter router, IConnection conn, int playerid, String name, List<NetworkClient> allPlayers) {

        for(NetworkClient client : allPlayers) {
            if(client.playerid == playerid) {
                client.setName(name);
                return;
            }
        }

        // Otherwise, we didn't find the network client in the list.. Time to make it!

        NetworkClient client = new NetworkClient(router, playerid, name);
        allPlayers.add(client);
        router.addRoute(client, conn);
    }

    private IConnection tcpConnect(InetAddress address, int port) throws IOException {
    	Socket soc = new Socket();
    	soc.connect(new InetSocketAddress(address, port), Settings.socketTimeout);
        soc.setSoTimeout(0);
        return new Connection(soc);
    }

    private void sendJoinGame(IConnection conn) throws ConnectionLostException {

        JoinGamePayload payload = new JoinGamePayload(new double[] { 1.0 }, new String[] {}, name);

        Message msg = new Message(Command.JOIN_GAME, payload);

        conn.sendBlocking(Parser.stringifyMessage(msg));
    }

    private boolean handleJoinGameResponse(IConnection conn) throws IOException {
        Message msg;
        try {
            msg = Networking.readMessage(conn);
        } catch(ParserException e) {
            e.printStackTrace();
            handler.onFailure(e);
            System.err.println(e.getMessage() + " cannot handle response");
            return false;
        } catch (TimeoutException e) {
			e.printStackTrace();
			return false;
		}

        if(msg == null) {
            handler.onFailure(new IOException("Networking error: Invalid or no message received from host."));
            return false;
        }

        if(msg.command == Command.JOIN_ACCEPT) {
            AcceptJoinGamePayload payload = (AcceptJoinGamePayload)msg.payload;

            this.playerid = payload.playerid;

            handler.onJoinAccepted(this.playerid);

            return true;
        }
        else if(msg.command == Command.JOIN_REJECT) {
            RejectJoinGamePayload payload = (RejectJoinGamePayload)msg.payload;
            handler.onJoinRejected(payload.message);
            return false;
        }
        else {
            handler.onFailure(new RuntimeException("Incorrect message received. Expected accept/reject_join_game. Received: " + msg.command.toString()));
            return false;
        }
    }

    /**
     *
     * @return Collection of NetworkClients - Containing all other players in the game (Not host or this local player)
     * @throws InterruptedException
     */
    private void handlePings(GameRouter router, NetworkClient host, Collection<NetworkClient> players) throws InterruptedException {
        LinkedList<NetworkClient> nonHostPlayers = new LinkedList<>(players);
        nonHostPlayers.remove(host);

        // Send ping to all other players
        sendPing(router);

        // Receive ping from all other players
        // TODO Try to receive ping from all other players.
        // TODO But if we get a ready command from the host, we should stop trying.
        receivePingResponseFromConnections(nonHostPlayers);
    }

    private int processHostPingMessage(Message msg) {
 
        if(msg.playerid == null || msg.playerid == -1) {
            nonPlayingHost = true;
        } else {
            if(msg.playerid < 0) {
                throw new RuntimeException("Host did not specify playerid. Playerid must be explicitly null for non playing host");
            }
            nonPlayingHost = false;
        }

        int numplayers = handlePingMessage(msg);

        return numplayers;
    }

    private void receivePingResponseFromConnections(Collection<NetworkClient> connections) throws InterruptedException {
        if(connections.size() == 0) {
            return;
        }
        
        System.out.println("Receiving pings from: " + connections.size() + "players");

        ExecutorCompletionService<Message> ecs = Networking.readMessageFromConnections(connections);

        for(@SuppressWarnings("unused") NetworkClient client : connections) {
            try {
                Future<Message> msg = ecs.take();

                processPingMessage(msg);
            } catch (InterruptedException e) {
                throw new RuntimeException("Timeout?");
            }
        }
    }

    private void processPingMessage(Future<Message> futureMsg) throws InterruptedException {

        Message msg;
        try {
             msg = futureMsg.get();
        } catch (ExecutionException e) {
            Throwable e2 = e.getCause();

            if(e2 instanceof ConnectionLostException
                    || e2 instanceof TimeoutException
                    || e2 instanceof ParserException) {
                throw new RuntimeException("Unable to receive ping message: " + e2.getClass().toString() + e2.getMessage());
            }
            // TODO tidy up logging
            e.printStackTrace();
            e2.printStackTrace();
            handler.onFailure(e2);
            return;
        }
        handlePingMessage(msg);
    }

    private int handlePingMessage(Message msg) {
        if(msg.command != Command.PING) {
            throw new RuntimeException("Invalid message " + msg.command);
        }
        
        int playerid = -1;
        		
        if(msg.playerid != null) {
        	playerid = msg.playerid;
        }
        
        handler.onPingReceive(playerid);

        if(msg.payload instanceof IntegerPayload) {
            return ((IntegerPayload)msg.payload).value;
        }

        return -1;
    }

    private void sendPing(GameRouter router) {
        Message ping = new Message(Command.PING, playerid, null);

        router.sendToAllPlayers(ping);
    }

    private boolean handleReady(GameRouter router, NetworkClient host, Collection<NetworkClient> players) {
        // receive Ready Message
        Message msg = receiveReadyFromHost(host);

        // acknowledge
        acknowledgeMessage(msg, router);

        // receive all other acknowledgements
        return readyAcknowledgements(router, host, players, msg, handler);
    }

    protected static boolean readyAcknowledgements(GameRouter router, NetworkClient host, Collection<NetworkClient> players, Message msg, LobbyEventHandler handler) {

        List<NetworkClient> nonHostPlayers = new LinkedList<>(players);
        nonHostPlayers.remove(host);

        List<Integer> responses = Networking.readAcknowledgementsForMessageFromPlayers(msg, nonHostPlayers);

        for(int playerid : responses) {
            handler.onReadyAcknowledge(playerid);
        }

        if(responses.size() != nonHostPlayers.size()) {
            handler.onFailure(new IOException("Did not receive acknowledgement from all players"));
            return false;
        }

        return true;
    }

    private void acknowledgeMessage(Message msg, GameRouter router) {
        Message response = Acknowledgement.acknowledgeMessage(msg, this.playerid);

        router.sendToAllPlayers(response);
    }

    private Message receiveReadyFromHost(NetworkClient host) {
        Message msg;
        try {
            msg = host.readMessage();
        } catch (TimeoutException | ConnectionLostException | ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to receive ping message: " + e.getClass().toString() + " " + e.getMessage());
        }

        if(msg.command != Command.READY) {
            throw new RuntimeException("Invalid message type");
        }

        handler.onReady();

        return msg;
    }

    private boolean receiveInitialiseGame(GameRouter router, NetworkClient host) {
        Message msg;
        try {
            msg = host.readMessage();
        } catch (TimeoutException | ConnectionLostException | ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to receive initialise_game message: " + e.getClass().toString() + " " + e.getMessage());
        }
        
        if(msg.command != Command.INITIALISE_GAME) {
        	throw new RuntimeException("Expecting initialise_game. Received: " + msg.command);
        }

        InitialiseGamePayload payload = (InitialiseGamePayload)msg.payload;

        handler.onInitialiseGame(payload.version, payload.features);

        return true;
    }
}
