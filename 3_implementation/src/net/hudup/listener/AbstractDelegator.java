/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

import net.hudup.core.client.Protocol;
import net.hudup.core.client.ProtocolImpl;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Runner;
import net.hudup.core.logistic.RunnerThread;
import net.hudup.core.logistic.UriAdapter;

/**
 * This is abstract class of delegator.
 * Delegator is responsible for handling and processing user request represented by {@link Request}. Each time {@link Listener} receives a user request, it creates a respective delegator and passes such request to delegator.
 * After that delegator processes and dispatches the request to the proper binding server. The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
 * In fact, delegator interacts directly with server. However, delegator is a part of {@link Listener} and the client-server interaction is always ensured.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AbstractDelegator extends ProtocolImpl implements Runner, AccountValidater {

	
	/**
	 * The Java network socket for receiving (reading) requests from client and sending back (writing) responses to client via input / output streams. 
	 */
	protected volatile Socket socket = null;
	
	
	/**
	 * Flag variable indicates whether or not this delegator started.
	 */
	protected volatile boolean started = false;
	
	
	/**
	 * Flag variable indicates whether or not this delegator paused.
	 */
	protected volatile boolean paused = false;
	
	
	/**
	 * User session for storing user information such as account name, password, privileges.
	 */
	protected volatile UserSession userSession = new UserSession();
	
	
	/**
	 * Socket server. It is often listener. This variable is not important.
	 */
	protected SocketServer socketServer = null;
	
	
	/**
	 * Default constructor with specified socket.
	 * The specified socket is assigned to the internal variable {@link #socket}.
	 * @param socket specified Java socket for receiving (reading) requests from client and sending back (writing) responses to client via input / output streams.
	 * @param socketServer socket server is often listener.
	 */
	public AbstractDelegator(Socket socket, SocketServer socketServer) {
		this.socket = socket;
		this.socketServer = socketServer;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		DataOutputStream out = null;
		BufferedReader in = null;
		synchronized (this) {
			try {
				started = true;
				out = new DataOutputStream(socket.getOutputStream());
				
				in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
			}
			catch (Throwable e) {
				e.printStackTrace();
				LogUtil.error("Delegator fail to listen from client " + socket + ", caused by " + e.getMessage());
			}
			
			notifyAll();
		}
		
		
		userSession.clear();
		String requestText = null;
		while (socket != null && !socket.isClosed()) {
			try {
				requestText = in.readLine(); //Wait here.
				if (requestText == null) break;

				Request request = parseRequest(requestText);
				if (request == null || request.isQuitRequest()) {
					Response empty = request == null? Response.createEmpty(Protocol.HDP_PROTOCOL) : Response.createEmpty(request.protocol);
					out.write( (empty.toJson() + "\n").getBytes());
				}
				else if (!initUserSession(this, userSession, request)) { //Also handle validate account action.
					Response empty = Response.createEmpty(request.protocol);
					out.write( (empty.toJson() + "\n").getBytes());
				}
				else {
					boolean handled = handleRequest(request, out);
					if (!handled) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				LogUtil.info("Error by writing data to client.");
				
				try {
					socket.close();
				}
				catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				socket = null;
			}
			
			synchronized (this) {
				while (paused) {
					notifyAll();
					try {
						wait();
					}
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		} // End while
		

		synchronized (this) {
			userSession.clear();

			try {
				if (in != null)
					in.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				LogUtil.error("Delegator fail to close input stream, causes error " + e.getMessage());
			}
			
			try {
				if (out != null)
					out.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				LogUtil.error("Delegator fail to close output stream, causes error " + e.getMessage());
			}

			try {
				if (socket != null && !socket.isClosed())
					socket.close();
				socket = null;
			}
			catch (Throwable e) {
				e.printStackTrace();
				LogUtil.error("Delegator fail to close socket, causes error " + e.getMessage());
			}

			started = false;
			paused = false;
			
			//Clearing extra data here. It is possible to write a clearing method to clear such data.
			socketServer = null;
			
			notifyAll();
		}
		
	}
	
	
	/**
	 * Initializing user session by specified request.
	 * User session is used for storing user information such as account name, password, privileges.
	 * @param accountValidater account validater.
	 * @param userSession user session.
	 * @param request specified request.
	 * @return whether user session initialized successfully.
	 */
	public static boolean initUserSession(AccountValidater accountValidater, UserSession userSession, Request request) {
		if (request.protocol != Protocol.HDP_PROTOCOL)
			return true;
		
		if (userSession.size() == 0) {
			
			String account = request.account_name;
			String password = request.account_password;
			int privileges = request.account_privileges;
			
			if (account == null || password ==  null || privileges <= 0)
				return false;
			
			if (  ((privileges & DataConfig.ACCOUNT_ACCESS_PRIVILEGE) != DataConfig.ACCOUNT_ACCESS_PRIVILEGE) ||
				  (!accountValidater.validateAccount(account, password, privileges))  ) {
				
				return false;
			}
			
			userSession.putAccount(account);
			//userSession.putPassword(password); //Do not store password
			userSession.putPriv(privileges);
			
			return true;
		}
		else {
			int priv = userSession.getPriv();
			if ( (priv & DataConfig.ACCOUNT_ACCESS_PRIVILEGE) != DataConfig.ACCOUNT_ACCESS_PRIVILEGE) {
				return false;
			}
			
			return true;
		}
	}

	
	@Override
	public synchronized boolean start() {
		if (isStarted())
			return false;
		
		new RunnerThread(this).start();
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Delegator fail to start, causes error " + e.getMessage());
		}
		
		return true;
	}

	
	@Override
	public synchronized boolean pause() {
		if (!isRunning()) return false;

		paused  = true;
		
		try {
			wait();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Delegator fail to pause, causes error " + e.getMessage());
		}
		
		return true;
	}
	
	
	@Override
	public synchronized boolean resume() {
		if (!isPaused()) return false;
		
		paused = false;
		notifyAll();
		
		return true;
	}
	
	
	@Override
	public synchronized boolean stop() {
		if (!isStarted()) return false;
		
		try {
			socket.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Delegator fail to close socket when stop, causes error " + e.getMessage());
		}
		
		if (paused) {
			paused = false;
			notifyAll();
		}

		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Delegator fail to stop, causes error " + e.getMessage());
		}
		
		return true;
	}
	
	
	@Override
	public boolean isStarted() {
		return started;
	}
	
	
	@Override
	public boolean isPaused() {
		return started && paused;
	}
	
	
	@Override
	public boolean isRunning() {
		return started && !paused;
	}


	/**
	 * Parsing request in text format into {@link Request} object.
	 * It is called by {@link #run()} method.
	 * 
	 * @param requestText request in text format.
	 * @return {@link Request} parsed
	 */
	protected abstract Request parseRequest(String requestText);
	
	
	/**
	 * Processing specified {@link Request} resulted from {@link #parseRequest(String)}.
	 * It is called by {@link #run()} method.
	 * 
	 * @param request specified request.
	 * @param out Output stream for writing the result from processing the specified request to client.
	 * Such result is wrapped by {@link Response} class.
	 * @return whether handle request successfully.
	 */
	protected abstract boolean handleRequest(Request request, DataOutputStream out);
	
	
	/**
	 * Parsing request from specified text.
	 * @param requestText specified request text.
	 * @return request parsed from specified text.
	 */
	protected static Request parseRequest0(String requestText) {
		Request request = null;
		try {
			String triple = requestText.substring(0, Math.min(3, requestText.length()));
			triple = triple.toUpperCase();
			
			if (triple.equals("GET")) {
				int fileType = HttpUtil.getFileType(requestText);
				
				if (fileType == UNKNOWN_FILE_TYPE) {
					Map<String, String> params = HttpUtil.getParameters(requestText);
					String action = HttpUtil.getAction(requestText);
					if (action != null)
						params.put("action", action);
					else
						params.put("action", Protocol.READ_FILE);
					
					request = Request.parse(params);
					request.protocol = HTTP_PROTOCOL;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
				else {
					request = new Request();
					request.protocol = HTTP_PROTOCOL;
					request.action = Protocol.READ_FILE;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
			}
			else {
				request = Request.parse(requestText);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			request = null;
			
			LogUtil.error("Delegator fail to parse request, causes error " + e.getMessage());
		}
		
		return request;
	}

	
}
