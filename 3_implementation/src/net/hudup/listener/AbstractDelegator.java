package net.hudup.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

import net.hudup.core.client.Protocol;
import net.hudup.core.client.ProtocolImpl;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.data.DataConfig;
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
	protected Socket socket = null;
	
	
	/**
	 * Flag variable indicates whether or not this delegator started.
	 */
	protected boolean started = false;
	
	
	/**
	 * Flag variable indicates whether or not this delegator paused.
	 */
	protected boolean paused = false;
	
	
	/**
	 * User session for storing user information such as account name, password, privileges.
	 */
	protected UserSession userSession = new UserSession();
	
	
	/**
	 * Socket server. It is often listener. This variable is not important.
	 */
	protected SocketServer socketServer = null;
	
	
	/**
	 * Default constructor with specified socket.
	 * The specified socket is assigned to the internal variable {@link #socket}.
	 * @param socket specified Java socket for receiving (reading) requests from client and sending back (writing) responses to client via input / output streams.
	 * @param socketServer socket server is often listener.
	 * @param socketConfig socket configuration.
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
				logger.error("Delegator fail to listen from client " + socket + ", caused by " + e.getMessage());
			}
			
			notifyAll();
		}
		
		
		try {
			
			userSession.clear();
			String requestText = null;
//			while ( (!socket.isClosed()) && (requestText = in.readLine()) != null ) {
			while (!socket.isClosed()) {
				try {
					requestText = in.readLine(); //Wait here.
				}
				catch (IOException e) {
					if (!socket.isClosed())
						e.printStackTrace();
					else
						logger.info("Error by socket interupted.");
					
					requestText = null;
					break;
				}
				if (requestText == null)
					break;
				
				synchronized (this) {
					final Request request = parseRequest(requestText);
					if (request == null || request.isQuitRequest()) {
						Response empty = request == null? Response.createEmpty(Protocol.HDP_PROTOCOL) : Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
						break;
					}

					
					if (!initUserSession(this, userSession, request)) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
						break;
					}
					

					boolean handled = handleRequest(request, out);
					if (request.protocol == HTTP_PROTOCOL)
						break;
					else if (!handled) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
					}
					
					while (paused) {
						notifyAll();
						wait();
					}
					
				} // synchronized (this)
				
			} // End while
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Delegator interrupted by error: " + e.getMessage());
		}
		finally {
			userSession.clear();
		}
		

		synchronized (this) {
			try {
				if (in != null)
					in.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close input stream, causes error " + e.getMessage());
			}
			
			try {
				if (out != null)
					out.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close output stream, causes error " + e.getMessage());
			}

			try {
				if (socket != null && !socket.isClosed())
					socket.close();
				socket = null;
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close socket, causes error " + e.getMessage());
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
			userSession.putPassword(password);
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
	public synchronized void start() {
		if (isStarted())
			return;
		
		new RunnerThread(this).start();
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to start, causes error " + e.getMessage());
		}
	}

	
	@Override
	public synchronized void pause() {
		if (!isStarted() || isPaused())
			return;
		
		paused  = true;
		
		try {
			wait();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to pause, causes error " + e.getMessage());
		}
			
	}
	
	
	@Override
	public synchronized void resume() {
		if (!isStarted() || !isPaused())
			return;
		
		paused = false;
		notifyAll();
	}
	
	
	@Override
	public synchronized void stop() {
		if (!isStarted())
			return;
		
		try {
			socket.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to close socket when stop, causes error " + e.getMessage());
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
			logger.error("Delegator fail to stop, causes error " + e.getMessage());
		}
		
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
		return isStarted() && !isPaused();
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
			
			logger.error("Delegator fail to parse request, causes error " + e.getMessage());
		}
		
		return request;
	}

	
}
