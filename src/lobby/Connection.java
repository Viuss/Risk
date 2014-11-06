package lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Concrete implementation of IConnection
 * 
 * @see IConnection
 * @author James
 *
 */
public class Connection implements IConnection {

	// ================================================================================
	// Properties
	// ================================================================================

	private int timeout = DEFAULT_TIMEOUT;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	// ================================================================================
	// Constructors
	// ================================================================================

	public Connection(Socket socket) {
		this.socket = socket;
	}

	// ================================================================================
	// Functions
	// ================================================================================
	@Override
	public void send(String message) throws ConnectionLostException {
		out.write(message);

		if (out.checkError())
			throw new ConnectionLostException();
	}

	@Override
	public String receive() throws ConnectionLostException, TimeoutException {
		try {
			in.readLine();
		} catch (IOException e) {
			throw new ConnectionLostException();
		}
		return null;
	}

	@Override
	public void kill() {
		out.close();

		try {
			in.close();
		} catch (IOException e) {
		}

		try {
			socket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Establishes the connection through the socket, setting up input and
	 * output streams.
	 * 
	 * @return True if successful connection established.
	 */
	protected boolean establishConnection() {
		boolean success = false;

		try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			success = true;
		} catch (IOException e) {
			// TODO: Log exception ?
			e.printStackTrace();
		}

		return success;
	}

	// ================================================================================
	// Accessors
	// ================================================================================

	@Override
	public void setTimeout(int milliseconds) {
		this.timeout = milliseconds;
		try {
			socket.setSoTimeout(milliseconds);
		} catch (SocketException e) {
			// TODO: Log exception ?
		}
	}

	@Override
	public int getTimeout() {
		int t;

		try {
			t = socket.getSoTimeout();
		} catch (SocketException e) {
			t = this.timeout;
		}

		return t;
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}
}