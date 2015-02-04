package networking;

import networking.message.JoinGamePayload;
import networking.message.Message;
import networking.parser.Parser;
import networking.parser.ParserException;

// Interface used by GameManager / NetworkPlayer?
public class Network {
	/**
	 * Gets a risk player connection from the socket
	 * 
	 * @param socket
	 * @return A LobbyClient object, which may be used to accept or reject the
	 *         join request.
	 * @return null if an error occurs during the initial connection
	 */
	public static LobbyClient getLobbyClient(IConnection socket) {

		Message message;
		try {
			message = Network.readMessage(socket);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		if (message == null || message.command != Command.JOIN_GAME) {
			return null;
		}

		JoinGamePayload payload = (JoinGamePayload) message.payload;

		return new LobbyClient(socket, payload.supported_versions,
				payload.supported_features);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public static LobbyServer joinLobby(String ipAddress, int port) {
		// Connect to IP address:port (TCP)

		// Send Join Message

		// Receive Accept (or reject) message
		return null;
	}

	/**
	 * Reads a line from conn, and parses it as a JSON Object in the protocol
	 * format.
	 * 
	 * @param conn
	 *            - Connection to read from
	 * @return Message on success
	 * @return null on socket failure TODO: Decide if this is enough detail.
	 *         Could be useful to provide different exceptions for IOError vs
	 *         Bad Packet
	 */
	public static Message readMessage(IConnection conn) throws ParserException,
			ConnectionLostException, TimeoutException {
		// Assumes newline is equivalent to JSON Object boundary. Waiting on
		// representatives to formally agree on this
		String msgString = conn.receiveLine();
		return Parser.parseMessage(msgString);
	}
}
