package networking;

import networking.message.Message;
import networking.parser.Parser;

import java.util.*;

/**
 * Maps between Game Players and Network Sockets.
 *
 * And handles potential need to re-broadcast messages once they are received
 *
 * Fairly flexible implementation, should work with current protocol and future protocols.
 */
public class GameRouter {
    private Map<IConnection, Set<NetworkClient>> connections;
    private Map<IConnection, ReadThread> readThreads;

    private Map<IConnection, Set<IConnection>> connectionBridges;

    public GameRouter() {
        connections = new HashMap<>();
        readThreads = new HashMap<>();
        connectionBridges = new HashMap<>();
    }

    public void addRoute(NetworkClient player, IConnection conn) {
        // Each playerid should only have one route
        removeAllRoutes(player);

        Set<NetworkClient> players = connections.get(conn);

        if(players == null) {
            players = new HashSet<>();

            startNewListenThread(conn);
        }

        players.add(player);

        connections.put(conn, players);

        System.out.println("Router addRoute(" + player.playerid + ", " + conn.getPort() + ")");
    }

    public void removeRoute(NetworkClient player, IConnection conn) {

        Set<NetworkClient> players = connections.get(conn);

        if(players != null && players.contains(player)) {

            players.remove(player);

            if(players.size() == 0) {
                players = null;
                stopReadThread(conn);
            }
        }

        connections.put(conn, players);

        System.out.println("Router removeRoute(" + player.playerid + ", " + conn.getPort() + ")");
    }

    public void removeAllRoutes(NetworkClient player) {
        for(IConnection conn : connections.keySet()) {
            removeRoute(player, conn);
        }
    }

    public void addBridge(IConnection msgSource, IConnection resendDest) {
        Set<IConnection> destinations = connectionBridges.get(msgSource);

        if(destinations == null) {
            destinations = new HashSet<>();
        }

        destinations.add(resendDest);

        connectionBridges.put(msgSource, destinations);
        // TODO Verbose log level
        //System.out.println("Router addBridge(" + msgSource.getPort() + ", " + resendDest.getPort() + ")");
    }

    public int getNumPlayers() {

        return getAllPlayers().size();
    }

    public Set<NetworkClient> getAllPlayers() {

        HashSet<NetworkClient> players = new HashSet<>();

        connections.values().forEach(players::addAll);

        return players;
    }

    // TODO: Send should work the same way as receive (Performed on different thread with result).
    //      TCP sends can block and would give a nice way to feedback errors to sender code.
    public void sendToAllPlayers(Message message) {
        // Send once on each socket
        for(IConnection conn : connections.keySet()) {
            sendToConnection(message, conn);
        }
    }

    private void sendToConnection(Message message, IConnection conn) {
        try {
            conn.sendBlocking(Parser.stringifyMessage(message));
        } catch (ConnectionLostException e) {
            handleException(conn, e);
        }
    }

    protected void handleMessage(IConnection conn, Message msg) {
        resendMessage(conn, msg);

        // TODO Handle Asynchronous timeout message here?
        // TODO Oh god timeout has an acknowledgement... Kill me now.

        dispatchMessageToNetworkClient(msg);
    }

    // TODO This doesn't seem to put the exception in the message queue of every client on the connection
    protected void handleException(IConnection conn, Exception ex) {
        Set<NetworkClient> clients = connections.get(conn);

        if(clients == null) {
            return;
        }

        for(NetworkClient client : clients) {
            client.addExceptionToQueue(ex);
        }
    }

    private void resendMessage(IConnection conn, Message msg) {
        // Do messages on this connection need to be resent to other connections?
        Collection<IConnection> destinations = connectionBridges.get(conn);

        if(destinations == null) {
            return;
        }

        for(IConnection dest : destinations) {

            System.out.println("Forwarded message from " + conn.getPort() + " to " + dest.getPort());
            sendToConnection(msg, dest);
        }
    }

    private void dispatchMessageToNetworkClient(Message msg) {

        int playerid;

        if(msg.playerid == null || msg.playerid < 0) {
            // findNetworkClient HOST PLAYER
            playerid = -1;
        } else {
            playerid = msg.playerid.intValue();
        }


        // Find the appropriate NetworkClient
        NetworkClient client = findNetworkClient(playerid);

        if(client == null) {
            //Log received message with playerid for which we have no client registered in the router
            System.out.println("Cannot route message to NetworkClient: Received msg.playerid=" + msg.playerid);
            return;
        }

        client.addMessageToQueue(msg);
    }

    private NetworkClient findNetworkClient(int playerid) {
        for(Set<NetworkClient> clients : connections.values()) {
            for(NetworkClient client : clients) {
                if(client.playerid == playerid) {
                    return client;
                }

                if(playerid == -1 && client.hostPlayer) {
                    return client;
                }
            }
        }

        return null;
    }

    private void startNewListenThread(IConnection conn) {
        ReadThread rThread = new ReadThread(conn, this);

        rThread.start();

        readThreads.put(conn, rThread);
    }

    private void stopReadThread(IConnection conn) {
        ReadThread rThread = readThreads.get(conn);

        rThread.stop();

        readThreads.remove(conn);
    }
}
