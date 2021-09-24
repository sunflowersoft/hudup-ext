/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.EventListenerList;

import net.hudup.core.Constants;
import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.SocketWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.RemoteRunner;
import net.hudup.core.logistic.Runner;
import net.hudup.core.logistic.RunnerThread;

/**
 * This abstract class implement partly the interface {@link Server} in order to setting up partially the server that support the popular socket connection.
 * In current implementation, the {@link Listener}, which inherits directly this {@link SocketServer}, is the complete server with socket connection.
 *   
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class SocketServer extends AbstractRunner implements Server, AccountValidater {

	
	/**
	 * Server configuration.
	 */
	protected ServerConfig config = null;
	
	
	/**
	 * The Java {@link ServerSocket} of socket connection. This socket is main object of {@link SocketServer}.
	 */
	protected ServerSocket serverSocket = null;

	
	/**
	 * Control socket for control commands such as start, stop, pause and resume.
	 */
	protected volatile ServerSocket controlSocket = null;
	
	
//	/**
//	 * User control session for storing user information such as account name, password, privileges.
//	 */
//	protected volatile UserSessions userControlSessions = new UserSessions();

	
	/**
	 * List of event listeners of this socket server. Popular listeners are server status listeners represented by {@link ServerStatusListener} interface.
	 * Every time server change its current status, it notifies an event {@link ServerStatusEvent} to {@link ServerStatusListener} (s).
	 * However this list can contains other registered listeners.
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
	/**
	 * The timer for making scheduler of server tasks.
	 */
	protected Timer timer = null;

	
	/**
	 * Indicator of hook status. Before socket server is shut down and discarded, a hook operator is performed.
	 * If this indicator is true, the hook operator is being done. When this indicator is false, the hook operator was done.
	 * Following is the code snippet of hook operator:
	 * <br><br>
	 * <code>
	 * shutdownHookStatus = true;<br>
	 * shutdown();<br>
	 * shutdownHookStatus = false;<br>
	 * </code> 
	 */
	protected volatile boolean shutdownHookStatus = false;
	
	
	/**
	 * List of internal runners.
	 */
	protected List<Object> internalRunners = Util.newList(); //Added date 2019.09.11 by Loc Nguyen
	
	
	/**
	 * Flag for some uses.
	 */
	protected volatile Boolean flag = false;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified server configuration.
	 */
	public SocketServer(ServerConfig config) {
		this.config = config;
		
		setupControlSocket();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					shutdownHookStatus = true;

					shutdown();
					
					shutdownHookStatus = false;
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
					LogUtil.error("Socket server failed to shutdown, caused by " + e.getMessage());
				}
			}
			
		});
	}
	
	
	@Override
	protected void task() {
		if (serverSocket == null || paused)
			return;
		
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			if (paused) {
				socket.close();
				socket = null;
			}
				
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			socket = null;
			LogUtil.error("Socket server failed to connect to client, caused by " + e.getMessage());
		}
		if (socket == null) return;

		try {
			socket.setSoTimeout(config.getServerTimeout()*1000);
			
			AbstractDelegator delegator = delegate(socket);
			if (delegator != null) {
				delegator.start();
			}
			else {
				try {
					socket.close();
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
					LogUtil.error("Socket server failed to close socket, caused by " + e.getMessage());
				}
				LogUtil.error("Socket server failed to create delegator (null delegator)");
			}
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			
			try {
				socket.close();
			} 
			catch (Throwable e1) {
				LogUtil.trace(e1);
				LogUtil.error("Socket server failed to close socket, caused by " + e.getMessage());
			}
			LogUtil.error("Socket server failed to connect to client, caused by " + e.getMessage());
		}
			
	}

	
	/**
	 * Processing socket control commands such as start, stop, pause, resume.
	 * @param socket socket of control task.
	 */
	protected void controlSocketTask(Socket socket) {
		if (socket == null) return;
		
		DataOutputStream out = null;
		BufferedReader in = null;
		UserSession userSession = new UserSession();
		try {
			socket.setSoTimeout(config.getServerTimeout()*1000);
			
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			String requestText = null;
			while ( (socket != null) && (!socket.isClosed()) 
					&& ((requestText = in.readLine()) != null) ) {
				
				Request request = AbstractDelegator.parseRequest0(requestText);
				
				if (request == null || request.isQuitRequest()) {
					Response empty = request == null? Response.createEmpty(Protocol.HDP_PROTOCOL) : Response.createEmpty(request.protocol);
					out.write((empty.toJson() + "\n").getBytes());
				}
				else if (request.action.equals(Protocol.VALIDATE_ACCOUNT)) { //Not store user name and password in session.
					String account = userSession.getAccount();
					if (account == null || !account.equals(request.account_name)) {
						userSession.clear();
						
						boolean validated = validateAdminAccount(request.account_name, request.account_password);
						Response response = Response.create(validated);
						out.write((response.toJson() + "\n").getBytes());
						
						if (validated) {
							userSession.putAccount(request.account_name);
							//userSession.putPassword(request.account_password); //Do not store password.
							userSession.putPriv(DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
						}
					}
					else if ( (userSession.getPriv() & DataConfig.ACCOUNT_ACCESS_PRIVILEGE) == DataConfig.ACCOUNT_ACCESS_PRIVILEGE)
						out.write((Response.create(true).toJson() + "\n").getBytes());
					else
						out.write((Response.create(false).toJson() + "\n").getBytes());
				}
				else if (request.action.equals(Protocol.CONTROL)) {
					if (userSession.size() > 0) {
						if (request.control_command.equals(Request.START_CONTROL_COMMAND))
							this.start();
						else if (request.control_command.equals(Request.STOP_CONTROL_COMMAND))
							this.stop();
						else if (request.control_command.equals(Request.PAUSE_CONTROL_COMMAND))
							this.pause();
						else if (request.control_command.equals(Request.RESUME_CONTROL_COMMAND))
							this.resume();
						else if (request.control_command.equals(Request.EXIT_CONTROL_COMMAND))
							this.exit();
	
						out.write((Response.create(true).toJson() + "\n").getBytes());
					}
					else
						out.write((Response.create(false).toJson() + "\n").getBytes());
				}
				else {
					out.write((Response.createEmpty(request.protocol) + "\n").getBytes());
				}
				
			} //End while
			
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			
			try {
				if (in != null) in.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			try {
				if (out != null) out.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}

			try {
				if (socket != null && !socket.isClosed())
					socket.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			userSession.clear();
		}
			
	}
	
	
	@Override
	public synchronized boolean start() {
		if (isStarted()) return false;
		
		LogUtil.info("Socket server is initializing to start, please wait...");
		setupServerSocket();
		if (!testServerSocket()) {
			fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
			LogUtil.error("Socket server failed to start");
			return false;
		}
		
		super.start();
		createTimer();

		fireStatusEvent(new ServerStatusEvent(this, Status.started));
		LogUtil.info("SOCKET SERVER IS SERVING AT PORT " + config.getServerPort());
		LogUtil.info("Socket server has socket control port " + config.getSocketControlPort());
		
		return true;
	}
	
	
	@Override
	public synchronized boolean pause() {
		if (!isRunning()) return false;
		
		pauseInternalRunners(); //Added date: 2019.08.11 by Loc Nguyen.
		pauseDelegators();

		destroyTimer();
		new SocketWrapper("localhost", 
				config.getServerPort()).sendQuitRequest();
		super.pause();
		
		fireStatusEvent(new ServerStatusEvent(this, Status.paused));
		LogUtil.info("Socket server paused");
		
		return true;
	}


	@Override
	public synchronized boolean resume() {
		if (!isPaused()) return false;
		
		resumeInternalRunners(); //Added date: 2019.08.11 by Loc Nguyen.
		resumeDelegators(); //Added date: 2019.08.11 by Loc Nguyen. Resume all delegators first.
		
		super.resume();
		createTimer();
		
		fireStatusEvent(new ServerStatusEvent(this, Status.resumed));
		LogUtil.info("Socket server resumed");
		
		return true;
	}


	@Override
	public synchronized boolean stop() {
		if (!isStarted()) return false;
		
		LogUtil.info("Socket server prepares to stop, please waiting...");
		
		stopInternalRunners(); //Added date 2019.09.11 by Loc Nguyen
		stopDelegators();

		destroyTimer();
		if (!paused)
			new SocketWrapper("localhost", config.getServerPort()).sendQuitRequest(); //This code line is redundant.
		super.stop();
		
		fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
		LogUtil.info("Socket server stopped");
		
		return true;
	}


	@Override
	public void exit() throws RemoteException {
		shutdown();
		System.exit(0);
	}
	
	
	/**
	 * Shutting down this socket server. After shut down, the server is discarded and cannot be re-started.
	 * @throws RemoteException if any error raises.
	 */
	protected synchronized void shutdown() throws RemoteException {
		if (config == null) //The configuration is like a flag.
			return;
		
		stop();
		
		destroyControlSocket();
		
		config.save();
		config = null;
		
		LogUtil.info("Socket server shutdown");
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
		
		//Added date: 2019.09.15 by Loc Nguyen.
		PluginStorage.clear();
	}

	
	@Override
	protected void clear() {
		destroyServerSocket();
		
		synchronized(internalRunners) {
			internalRunners.clear();
		}
	}

	
	/**
	 * When socket server receive incoming request, it dispatches such request to a delegator represented by {@link AbstractDelegator} so that such delegator handles and processes the request.
	 * This method is responsible for creating the delegator with specified network socket at first.
	 * Later on method {@link #task()} will dispatch incomming request to the returned delegator. 
	 * @param socket specified network socket
	 * @return returned delegator which handles and processes incoming request later.
	 */
	protected abstract AbstractDelegator delegate(Socket socket);
	
	
	/**
	 * Creating the internal timer {@link #timer} for making scheduler of server tasks.
	 * In every interval in mili-seconds, the {@link #timer} calls the method {@link #callServerTasks()} which in turn calls the method {@link #serverTasks()}.
	 * Specified tasks of server are coded in the method {@link #serverTasks()}.
	 */
	private void createTimer() {
		destroyTimer();
		
		try {
			int milisec = config.getServerTasksPeriod()*1000;
			if (milisec == 0) return;
			
			timer = new Timer();
			timer.schedule(
				new TimerTask() {
				
					@Override
					public void run() {
						callServerTasks();
					}
				}, 
				milisec, 
				milisec);
			
			LogUtil.info("Socket server created internal timer, executing periodly " + (milisec/1000) + " seconds");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket server failed to create internal timer, caused by " + e.getMessage());
		}
	}

	
	/**
	 * The internal {@link #timer} is responsible for making scheduler of server tasks.
	 * In every interval in mili-seconds, the {@link #timer} calls the method {@link #callServerTasks()} which in turn calls the method {@link #serverTasks()}.
	 * Specified tasks of server are coded in the method {@link #serverTasks()}.
	 */
	private synchronized void callServerTasks() {
		if (!isRunning())
			return;
		
		try {
			serverTasks();
			LogUtil.info("Socket server finished server tasks");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket server failed to call server tasks, caused by " + e.getMessage());
			
		}
		
	}

	
	/**
	 * The internal {@link #timer} is responsible for making scheduler of server tasks.
	 * In every interval in mili-seconds, the {@link #timer} calls the method {@link #callServerTasks()} which in turn calls the method {@link #serverTasks()}.
	 * Specified tasks of server are coded in the method {@link #serverTasks()}.
	 */
	protected abstract void serverTasks();

	
	/**
	 * Destroying the internal {@link #timer}. Note, such timer is responsible for making scheduler of server tasks.
	 * After this method is called, no server task is performed.
	 */
	private void destroyTimer() {
		if (timer != null) {
			try {
				timer.cancel();
				timer = null;
				
				LogUtil.info("Socket server destroyed internal timer");
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to destroy internal timer, caused by " + e.getMessage());
			}
		}
	}

	
	/**
	 * Destroying the internal socket {@link #serverSocket}.
	 */
	private void destroyServerSocket() {
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to close server socket, caused by " + e.getMessage());
			}
		}
		
		serverSocket = null;
	}
	
	
	/**
	 * Destroying the control socket.
	 */
	private void destroyControlSocket() {
		if (controlSocket == null || controlSocket.isClosed())
			return;
		
		synchronized (controlSocket) {
			try {
				controlSocket.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			controlSocket = null;
		}
	}

	
	/**
	 * Setting up (initializing) the internal socket {@link #serverSocket}.
	 */
	private void setupServerSocket() {
		if (serverSocket == null || serverSocket.isClosed()) {
		
			try {
				int port = NetUtil.getPort(config.getServerPort(), Constants.TRY_RANDOM_PORT);
				if (port < 0)
					throw new Exception("Invalid port number");
				config.setServerPort(port);

				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(config.getServerTimeout()*1000);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to create server socket, caused by " + e.getMessage());
				destroyServerSocket();
			}
		}
	}

	
	/**
	 * Setting up (initializing) the control socket.
	 */
	private void setupControlSocket() {
		if (controlSocket != null && !controlSocket.isClosed())
			return;
			
		try {
			int port = NetUtil.getPort(config.getSocketControlPort(), Constants.TRY_RANDOM_PORT);
			if (port < 0)
				throw new Exception("Invalid port number");
			config.setSocketControlPort(port);

			controlSocket = new ServerSocket(port);
			controlSocket.setSoTimeout(config.getServerTimeout()*1000);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Socket server failed to create server socket, caused by " + e.getMessage());
			destroyControlSocket();
			return;
		}
		
		
		new Thread() {

			@Override
			public void run() {
				super.run();
				
				while (controlSocket != null && !controlSocket.isClosed()) {
					Socket socket = null;
					try {
						socket = controlSocket.accept();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
						socket = null;
					}
					
					if (socket != null)
						controlSocketTask(socket);
				}
				
				destroyControlSocket();
			}
			
		}.start();
	}

	
	/**
	 * Testing the internal socket {@link #serverSocket}.
	 * @return whether test socket successfully
	 */
	private boolean testServerSocket() {
		return serverSocket != null && !serverSocket.isClosed();
	}


	@Override
	public boolean addStatusListener(ServerStatusListener listener) 
			throws RemoteException {
		
		synchronized(listenerList) {
			try {
		        listenerList.add(ServerStatusListener.class, listener);
		        LogUtil.info("Socket server added successfully status listener " + listener.getClass());
		        return true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to add status listener, caused by " + e.getMessage());
			}
			
			return false;
		}
    }
	
	
	@Override
	public boolean ping()
			throws RemoteException {
		
		return config != null;
	}


	@Override
    public boolean removeStatusListener(ServerStatusListener listener) 
    		throws RemoteException {
		synchronized(listenerList) {
			try {
				listenerList.remove(ServerStatusListener.class, listener);
				LogUtil.info("Socket server removed successfully status listener " + listener.getClass());
				return true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to remove status listener, caused by " + e.getMessage());
			}
			
			return false;
		}
    }
	

    /**
     * Getting the array of server status listeners of this server.
     * Such listeners were registered and stored in {@link #listenerList}.
     * Note, every time server change its current status, it notifies an event {@link ServerStatusEvent} to listeners that implement the {@link ServerStatusListener} interface.
     * @return array of {@link ServerStatusListener} (s).
     */
    public ServerStatusListener[] getStatusListeners() {
		synchronized(listenerList) {
	        return listenerList.getListeners(ServerStatusListener.class);
		}
    }

    
    /**
     * Firing a status event to server status listeners of this server.
     * Such listeners were registered and stored in {@link #listenerList}.
     * Note, every time server change its current status, it notifies an event {@link ServerStatusEvent} to listeners that implement the {@link ServerStatusListener} interface.
     * @param evt event indicating status of server.
     */
	protected void fireStatusEvent(ServerStatusEvent evt) {
		ServerStatusListener[] listeners = getStatusListeners();
		
		for (ServerStatusListener listener : listeners) {
			try {
				evt.setShutdownHookStatus(shutdownHookStatus);
				listener.statusChanged(evt);
				LogUtil.info("Socket server fired successfully status event to listener " + listener.getClass());
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to fire status event to listener " + listener.getClass());
			}
		}
		
	}


	@Override
	public DataConfig getConfig() 
			throws RemoteException {
		return config;
	}


	@Override
	public synchronized void setConfig(DataConfig config) 
			throws RemoteException {
		if (isStarted())
			return;

		if (this.config != config)
			this.config.putAll(config);
		fireStatusEvent(new ServerStatusEvent(this, Status.setconfig));
		LogUtil.info("Socket server set configuration successfully");
	}

	
	@Override
	public boolean validateAdminAccount(String account, String password) {
		if (!account.equals(DataConfig.ADMIN_ACCOUNT))
			return false;
		
		//Checking Hudup properties.
		String pwd = Util.getHudupProperty(account);
		if (pwd == null)
			return false;
		else
			return pwd.equals(password);
	}


	/**
	 * Getting running delegators of this server.
	 * Note, a delegator when is responsible for handling and processing incoming request that server dispatches to it.
	 * @return list of running {@link AbstractDelegator}.
	 */
	private List<AbstractDelegator> getDelegators() {
		List<AbstractDelegator> delegators = Util.newList();
		
		try {
			int n = Thread.activeCount(); //Not totally precise in different thread group
			Thread[] tarray = new Thread[n];
			
			Thread.enumerate(tarray); //Not totally precise in different thread group
			for (Thread thread : tarray) {
				if (thread == null || !(thread instanceof RunnerThread))
					continue;
				
				Runner runner = ((RunnerThread)thread).getRunner();
				if ( (runner != null) && (runner instanceof AbstractDelegator) )
					delegators.add((AbstractDelegator)runner);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return delegators;
	}
	
	
	/**
	 * This method stops running delegators of this server.
	 * Note, a delegator when is responsible for handling and processing incoming request that server dispatches to it.
	 */
	protected void stopDelegators() {
		List<AbstractDelegator> delegators = getDelegators();
		for (AbstractDelegator delegator : delegators) {
			try {
				delegator.stop();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to stop dlegator, caused by " + e.getMessage());
			}
		}
		
	}

	
	/**
	 * This method pauses running delegators of this server.
	 * Note, a delegator when is responsible for handling and processing incoming request that server dispatches to it.
	 */
	protected void pauseDelegators() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				
				List<AbstractDelegator> delegators = getDelegators();
				for (AbstractDelegator delegator : delegators) {
					try {
						delegator.pause();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
						LogUtil.error("Socket server failed to pause delegator, caused by " + e.getMessage());
					}
				}
			}
		}.start();
		
	}
	
	
	/**
	 * This method resumes paused delegators of this server.
	 * Note, a delegator when is responsible for handling and processing incoming request that server dispatches to it.
	 */
	protected void resumeDelegators() {
		List<AbstractDelegator> delegators = getDelegators();
		for (AbstractDelegator delegator : delegators) {
			try {
				delegator.resume();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to resume delegator, caused by " + e.getMessage());
			}
		}
		
	}
	
	
	//Added date 2019.09.11 by Loc Nguyen
	/**
	 * Add runner.
	 * @param runner runnable object.
	 */
	protected void addRunner(Object runner) {
		if (!(runner instanceof Runner) && !(runner instanceof RemoteRunner))
			return;
		
		synchronized (internalRunners) {
			internalRunners.add(runner);
		}
	}
	
	
	//Added date 2019.09.11 by Loc Nguyen
	/**
	 * Remove runner.
	 * @param runner runnable object.
	 */
	protected void removeRunner(Object runner) {
		synchronized (internalRunners) {
			internalRunners.remove(runner);
		}
	}
	
	
	//Added date 2019.09.11 by Loc Nguyen
	/**
	 * Pause internal runners.
	 */
	protected void pauseInternalRunners() {
		synchronized (internalRunners) {
			synchronized (flag) {
				flag = true;
				for (Object runner : internalRunners) {
					try {
						if (runner instanceof Runner)
							((Runner)runner).pause();
						else if (runner instanceof RemoteRunner)
							((RemoteRunner)runner).remotePause();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
					}
				}
				flag = false;
			}
		}
	}
	
	
	//Added date 2019.09.11 by Loc Nguyen
	/**
	 * Resume internal runners.
	 */
	protected void resumeInternalRunners() {
		synchronized (internalRunners) {
			synchronized (flag) {
				flag = true;
				for (Object runner : internalRunners) {
					try {
						if (runner instanceof Runner)
							((Runner)runner).resume();
						else if (runner instanceof RemoteRunner)
							((RemoteRunner)runner).remoteResume();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
					}
				}
				flag = false;
			}
		}
	}

	
	//Added date 2019.09.11 by Loc Nguyen
	/**
	 * Stop internal runners.
	 */
	protected void stopInternalRunners() {
		synchronized (internalRunners) {
			synchronized (flag) {
				flag = true;
				for (Object runner : internalRunners) {
					try {
						if (runner instanceof Runner)
							((Runner)runner).stop();
						else if (runner instanceof RemoteRunner)
							((RemoteRunner)runner).remoteStop();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
					}
				}
				flag = false;
			}
		}
	}


	/**
	 * Getting flag.
	 * @return flag for some uses.
	 */
	protected boolean getFlag() {
		synchronized (flag) {
			return flag;
		}
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			shutdown();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
}
