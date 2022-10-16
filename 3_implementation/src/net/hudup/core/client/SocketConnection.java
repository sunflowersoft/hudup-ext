/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.hudup.core.Constants;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.logistic.LogUtil;

/**
 * This class extends directly {@link SocketWrapper} class and so it is also a service that sends request to server and then receives response (result) from server, according to socket interaction.
 * However, in the same session, it can send many requests and receive many responses (from server) whereas {@link SocketWrapper} can send/receive only one request/response to/from server at one time.
 * In other words, {@link SocketConnection} is an advanced version of {@link SocketWrapper}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketConnection extends SocketWrapper implements AutoCloseable {


	/**
	 * Internal network socket.
	 */
	protected Socket socket = null;
	
	
	/**
	 * Writer for writing data to network socket.
	 */
	protected PrintWriter out = null;
	
	
	/**
	 * Input stream for reading data from network socket.
	 */
	protected InputStream in = null;
	
	
	/**
	 * Constructor with remote host and remote port.
	 * @param host remote host.
	 * @param port remote port.
	 */
	public SocketConnection(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Connecting to remote server at remote port with specified account, password, and privileges.
	 * @param account specified account.
	 * @param password specified password.
	 * @param priv specified privileges.
	 * @return whether connect successfully.
	 */
	public boolean connect(String account, String password, int priv) {
		try {
			close();
			
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = socket.getInputStream();
			
			boolean validated = validateAccount(account, password, priv);
			if (!validated)
				close();
			return validated;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			close();
			LogUtil.error("Socket connects to server fail caused by error " + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * Testing whether the socket is connected.
	 * @return whether the socket is connected.
	 */
	public boolean isConnected() {
		return socket != null && out != null && in != null;
	}
	
	
	@Override
	protected Response sendRequest(Request request) {
		if (!isConnected()) {
			LogUtil.error("Socket not connected");
			return null;
		}
		
		//String responseText = null;
		try {
			out.println(request.toJson());
			
			if (request.notJsonParsing) {
				return Response.parse(in);
			}
			else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String responseText = reader.readLine();
				if (responseText == null)
					return null;
				else
					return Response.parse(responseText);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
			LogUtil.error("Socket sends request to server, causes error " + e.getMessage());
		}
		
		return null;
	}
	
	
	/**
	 * Closing this socket connection together its session.
	 */
	@Override
	public void close() {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket connection fail to close input stream, causes error " + e.getMessage());
		}
		
		try {
			if (out != null) {
				out.close();
				out = null;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket connection fail to close output stream, causes error " + e.getMessage());
		}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket connection fail to close socket, causes error " + e.getMessage());
		}
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	
	
}
