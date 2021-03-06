package networking;

import networking.message.Message;
import networking.parser.ParserException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Adam on 08/02/2015.
 */
public class NetworkClient {

    Logger logger = Logger.getLogger("NetworkClient");

    public final int playerid;
    private String name;
    private boolean hostPlayer;

    public final GameRouter router;
    private BlockingQueue<Object> messageQueue;

    public NetworkClient(GameRouter router, int playerid, String name) {
        this.hostPlayer = false;
        this.router = router;
        this.playerid = playerid;
        this.name = name;

        this.messageQueue = new LinkedBlockingQueue<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isHost() {
    	return hostPlayer;
    }
    
    public void setHost() {
    	hostPlayer = true;
    }

    public Message readMessage() throws TimeoutException, ConnectionLostException, ParserException {
    	System.out.println("waiting for message from playerid " + this.playerid);
    	
        Object obj;
        try {
            // TODO: Timeout?
            obj = messageQueue.take();
        } catch (InterruptedException e) {
            TimeoutException ex = new TimeoutException();
            ex.addSuppressed(e);

            throw ex;
        }

        return processQueuedObject(obj);
    }

    private Message processQueuedObject(Object obj) throws TimeoutException, ConnectionLostException, ParserException {

        logger.log(Level.FINE, "readMessage NetworkClient playerid: " + playerid);

        if(obj instanceof Message) {
            return (Message)obj;
        }

        if(obj instanceof TimeoutException) {
            throw (TimeoutException)obj;
        }

        if(obj instanceof ConnectionLostException) {
            throw (ConnectionLostException)obj;
        }

        if(obj instanceof ParserException) {
            throw (ParserException)obj;
        }

        throw new RuntimeException("Received invalid object in messageQueue: " + obj.getClass().toString() + " : " + obj.toString());
    }

    protected void addMessageToQueue(Message msg) {

        // TODO Handle leave_game messages here?
        // leave_game would be sent to show that this player has left the game.
        // For leave game, we could put a ConnectionLostException in the message queue.

        addToQueue(msg);
    }

    protected void addExceptionToQueue(Exception ex) {
        addToQueue(ex);
    }

    private void addToQueue(Object obj) {
        messageQueue.add(obj);
        logger.log(Level.FINE, "readMessage NetworkClient playerid: " + playerid);
    }
}
