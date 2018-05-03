package net.hudup.listener;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.EventListenerList;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.SocketWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.NetUtil;
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
public abstract class SocketServer extends AbstractRunner implements Server {

	
	/**
	 * Server configuration.
	 */
	protected ServerConfig config = null;
	
	
	/**
	 * The Java {@link ServerSocket} of socket connection. This socket is main object of {@link SocketServer}.
	 */
	protected ServerSocket serverSocket = null;

	
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
	protected boolean shutdownHookStatus = false;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified server configuration.
	 */
	public SocketServer(ServerConfig config) {
		this.config = config;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					shutdownHookStatus = true;

					shutdown();
					
					shutdownHookStatus = false;
				} 
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			        logger.error("Socket server fail to shutdown, caused by " + e.getMessage());
				}
			}
			
		});
	}
	
	
	@Override
	public void task() {
		// TODO Auto-generated method stub
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
			e.printStackTrace();
			socket = null;
			logger.error("Socket server fail to connect to client, caused by " + e.getMessage());
		}

		if (socket != null) {
			try {
				socket.setSoTimeout(config.getServerTimeout());
				
				AbstractDelegator delegator = delegate(socket);
				if (delegator != null) {
					delegator.start();
				}
				else {
					try {
						socket.close();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error("Socket server fail to close socket, caused by " + e.getMessage());
					}
					logger.error("Socket server fail to create delegator (null delegator)");
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try {
					socket.close();
				} 
				catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					logger.error("Socket server fail to close socket, caused by " + e.getMessage());
				}
				logger.error("Socket server fail to connect to client, caused by " + e.getMessage());
			}
			
				
		}
		
	}

	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		if (isStarted())
			return;
		
		logger.info("Socket server is initializing to start, please waiting...");
		setupServerSocket();
		if (!testServerSocket()) {
			fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
			logger.error("Socket server fail to start");
			return;
		}
		
		super.start();
		createTimer();

		fireStatusEvent(new ServerStatusEvent(this, Status.started));
		logger.info("Socket server started at port " + config.getServerPort());
		
	}
	
	
	@Override
	public synchronized void pause() {
		// TODO Auto-generated method stub
		if (isRunning()) {
		
			destroyTimer();
			new SocketWrapper("localhost", 
					config.getServerPort()).sendQuitRequest();
			super.pause();
			pauseDelegators();
			
			fireStatusEvent(new ServerStatusEvent(this, Status.paused));
			logger.info("Socket server paused");
		}
	}


	@Override
	public synchronized void resume() {
		// TODO Auto-generated method stub
		if (isPaused()) {
		
			super.resume();
			resumeDelegators();
			createTimer();
			
			fireStatusEvent(new ServerStatusEvent(this, Status.resumed));
			logger.info("Socket server resumed");
		}
	}


	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		if (!isStarted())
			return;
		
		logger.info("Socket server prepare to stop, please waiting...");
		
		destroyTimer();
		if (!paused)
			new SocketWrapper("localhost", config.getServerPort()).sendQuitRequest();
		super.stop();
		stopDelegators();
		
		fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
		logger.info("Socket server stopped");
	}


	@Override
	public void exit() throws RemoteException {
		shutdown();
		System.exit(0);
	}
	
	
	/**
	 * Shutting down this socket server. After shut down, the server is discarded and cannot be re-started.
	 */
	protected synchronized void shutdown() throws RemoteException {
		if (config == null)
			return;
		
		stop();
		config.save();
		config = null;
		logger.info("Socket server shutdown");
		
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
	}

	
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		destroyServerSocket();
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
			int milisec = config.getServerTaskPeriod();
			if (milisec == 0)
				return;
			
			timer = new Timer();
			timer.schedule(
				new TimerTask() {
				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						callServerTasks();
					}
				}, 
				milisec, 
				milisec);
			
			logger.info("Socket server create internal timer, executing periodly " + milisec + " miliseconds");
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Socket server fail to create internal timer, caused by " + e.getMessage());
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
			logger.info("Socket server finished server tasks");
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Socket server fail to call server tasks, caused by " + e.getMessage());
			
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
				
				logger.info("Socket server destroyed internal timer");
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Socket server fail to destroy internal timer, caused by " + e.getMessage());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Socket server fail to close server socket, caused by " + e.getMessage());
			}
		}
		
		serverSocket = null;
	}
	
	
	/**
	 * Setting up (initializing) the internal socket {@link #serverSocket}.
	 */
	private void setupServerSocket() {
		if (serverSocket == null || serverSocket.isClosed()) {
		
			try {
				int port = NetUtil.getPort(config.getServerPort(), Constants.TRY_RANDOM_PORT);
				if (port < 0)
					throw new RuntimeException("Invalid port number");
				config.setServerPort(port);

				serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(config.getServerTimeout());
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Socket server fail to create server socket, caused by " + e.getMessage());
				destroyServerSocket();
			}
		}
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
		        logger.info("Socket server add successfully status listener " + listener.getClass());
		        return true;
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Socket server fail to add status listener, caused by " + e.getMessage());
			}
			
			return false;
		}
    }
	
	
	@Override
	public boolean ping()
			throws RemoteException {
		
		// TODO Auto-generated method stub
		
		return config != null;
	}


	@Override
    public boolean removeStatusListener(ServerStatusListener listener) 
    		throws RemoteException {
		synchronized(listenerList) {
			try {
				listenerList.remove(ServerStatusListener.class, listener);
				logger.info("Socket server remove successfully status listener " + listener.getClass());
				return true;
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Socket server fail to remove status listener, caused by " + e.getMessage());
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
		        logger.info("Socket server fire successfully status event " + evt + " to listener " + listener.getClass());
			}
			catch (Throwable e) {
				e.printStackTrace();
		        logger.error("Socket server fail to fire status event " + evt + " to listener " + listener.getClass());
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
		logger.info("Socket server set configuration successfully");
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
			e.printStackTrace();
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
				e.printStackTrace();
		        logger.error("Socket server fail to stop dlegator, caused by " + e.getMessage());
			}
		}
		
	}

	
	/**
	 * This method pauses running delegators of this server.
	 * Note, a delegator when is responsible for handling and processing incoming request that server dispatches to it.
	 */
	protected void pauseDelegators() {
		List<AbstractDelegator> delegators = getDelegators();
		for (AbstractDelegator delegator : delegators) {
			try {
				delegator.pause();
			}
			catch (Throwable e) {
				e.printStackTrace();
		        logger.error("Socket server fail to pause delegator, caused by " + e.getMessage());
			}
		}
		
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
				e.printStackTrace();
		        logger.error("Socket server fail to resume delegator, caused by " + e.getMessage());
			}
		}
		
	}
	
	
	/**
	 * Getting the message (value) associated with the specified key from the server configuration.
	 * @param key specified key.
	 * @return message (value) associated with the specified key from the server configuration.
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(config, key);
	}

	
}
