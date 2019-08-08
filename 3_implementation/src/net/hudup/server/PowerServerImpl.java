package net.hudup.server;

import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import net.hudup.core.Constants;
import net.hudup.core.client.ActiveMeasure;
import net.hudup.core.client.Gateway;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.xURI;


/**
 * This is abstract class for power sever which is the base of recommendation server.
 * <br>
 * Note, the sub-architecture of recommendation server (recommender) is inspired from the architecture of Oracle database management system (Oracle DBMS);
 * especially concepts of listener and share memory layer are borrowed from concepts &quot;Listener&quot; and &quot;System Global Area&quot; of Oracle DBMS, respectively,
 * available at <a href="https://docs.oracle.com/database/122/index.htm">https://docs.oracle.com/database/122/index.htm</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class PowerServerImpl implements PowerServer, Gateway {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Server policy file.
	 */
	public final static String  SERVER_POLICY = Constants.ROOT_PACKAGE + "hudup_server.policy";
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(PowerServer.class);

	
	/**
	 * Starting flag.
	 */
	protected boolean started = false;
	
	
	/**
	 * Pausing flag.
	 */
	protected boolean paused = false;
	
	
	/**
	 * Configuration.
	 */
	protected PowerServerConfig config = null;

	
	/**
	 * List of listeners.
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
	/**
	 * Transaction.
	 */
	protected Transaction trans = null;

	
	/**
	 * RMI registry.
	 */
	protected Registry registry = null;
	
	
	/**
	 * Internal timer.
	 */
	protected Timer timer = null;

	
	/**
	 * Gateway
	 */
	protected Gateway gateway = null;
	
	
	/**
	 * Active measures.
	 */
	protected ActiveMeasure activeMeasure = new ActiveMeasureImpl();
	
	
	/**
	 * Shutdown hook status.
	 */
	protected boolean shutdownHookStatus = false;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	@SuppressWarnings("deprecation")
	public PowerServerImpl(PowerServerConfig config) {
		super();
		
		try {
			this.config = config;
			this.config.addReadOnly(DataConfig.MAX_RATING_FIELD);
			this.config.addReadOnly(DataConfig.MIN_RATING_FIELD);
			this.trans = createTransaction();
			this.gateway = new GatewayImpl(this);		
			
			URL policyUrl = getClass().getResource(SERVER_POLICY);
			if (policyUrl != null) {
				System.setProperty("java.security.policy", policyUrl.toString());
				if (System.getSecurityManager() == null) {
					int version = SystemUtil.getJavaVersion();
					if (version <= 8)
						System.setSecurityManager(new java.rmi.RMISecurityManager());
					else
						System.setSecurityManager(new SecurityManager());
				}
			}
			
			int port = NetUtil.getPort(this.config.getServerPort(), Constants.TRY_RANDOM_PORT);
			if (port < 0)
				throw new Exception("Invalid port number");
			this.config.setServerPort(port);
			
			registry = LocateRegistry.createRegistry(port);
			UnicastRemoteObject.exportObject(this, port);
			UnicastRemoteObject.exportObject(gateway, port);
			
			Naming.rebind(getGatewayBindName(), gateway);
			
			initStorageSystem();
			
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Server constructor causes error " + e.getMessage());
			System.exit(0);
		}
		
		
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
				}
			}
			
		});
		
	}

	
	/**
	 * Create transaction.
	 * @return {@link Transaction}.
	 */
	protected abstract Transaction createTransaction();

	
	@Override
	public synchronized void start()  throws RemoteException {
		// TODO Auto-generated method stub
		if (isStarted())
			return;
    	
    	trans.lockWrite();
    	try {
    		logger.info("Server is initializing to start, please waiting...");
			doWhenStart();
			activeMeasure.reset();

			UnicastRemoteObject.exportObject(getService(), config.getServerPort());

			createTimer();
			
			started = true;
			fireStatusEvent(new ServerStatusEvent(this, Status.started));
			logger.info("Server started at port " + config.getServerPort());
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				doWhenStop();
			}
			catch (Throwable e2) {
				e2.printStackTrace();
			}
			
			logger.error("Server fail to start, caused by " + e.getMessage());
		}
    	finally {
        	trans.unlockWrite();
    	}
    	
		SystemUtil.enhanceAuto();
	}

    
    /**
     * Some tasks right after started.
     */
    protected abstract void doWhenStart();
    
    
	@Override
	public synchronized void pause()  throws RemoteException {
		// TODO Auto-generated method stub
		if (isRunning()) {
			
        	trans.lockWrite();
        	
			destroyTimer();
			
    		paused = true;
    		fireStatusEvent(new ServerStatusEvent(this, Status.paused));
    		logger.info("Server paused");
		}
    	
	}

	
	@Override
	public synchronized void resume()  throws RemoteException {
		// TODO Auto-generated method stub
		if (!trans.isWriteLockedByCurrentThread())
			return;
			
		if (isPaused()) {
        	
			createTimer();
        	
			paused = false;
    		fireStatusEvent(new ServerStatusEvent(this, Status.resumed));
    		logger.info("Server resumed");
    		
        	trans.unlockWrite();
		}
	}

	
	@Override
	public synchronized void stop()  throws RemoteException {
		// TODO Auto-generated method stub
		if (!isStarted())
			return;
		
		if (isPaused()) {
			if (!trans.isWriteLockedByCurrentThread())
				return;
			
			paused = false;
        	trans.unlockWrite();
		}
		
    	trans.lockWrite();
    	try {
    		logger.info("Server is initializing to stop, please waiting...");
			destroyTimer();
			
			UnicastRemoteObject.unexportObject(getService(), true);
				
        	doWhenStop();
			activeMeasure.reset();
			started = false;
    		fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
    		logger.info("Server stopped");
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			logger.error("Server fail to stop, caused by " + e.getMessage());
		}
    	finally {
        	trans.unlockWrite();
    	}
    	
	}

	
	/**
	 * Doing some tasks when power server stop, called by {@link #stop()}
	 */
	protected abstract void doWhenStop();
	
	
	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		shutdown();
		
		System.exit(0);
	}

	
	/**
	 * Shutdown server, after server shutdown, program exits. Called by {@link #exit()} 
	 * @throws RemoteException
	 */
	protected synchronized void shutdown() throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null)
			return;
		
		stop();
		
    	trans.lockWrite();
    	try {
        	UnicastRemoteObject.unexportObject(gateway, true);
			Naming.unbind(getGatewayBindName());
			
        	UnicastRemoteObject.unexportObject(this, true);
        	try {
        		UnicastRemoteObject.unexportObject(registry, true);
        	}
        	catch (Throwable e) {
        		e.printStackTrace();
        	}
        	
        	destroyStorageSystem();
			
			registry = null;
    		config.save();
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Server get error when shutdowning, error is " + e.getMessage());
		} 
    	finally {
        	trans.unlockWrite();
    	}
		config = null;
		logger.info("Server shutdown");
    	
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
	}

	
	/**
	 * Creating server task scheduler as timer, called by methods: {@link #start()} and {@link #resume()}
	 */
	private void createTimer() {
		destroyTimer();
		
		int milisec = config.getServerTasksPeriod();
		if (milisec == 0)
			return;
		
		timer = new Timer();
		timer.schedule(
			new TimerTask() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						callServerTasks();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						logger.error("Calling server tasks causes error " + e.getMessage());
					}
				}
			}, 
			milisec, 
			milisec);
		
		logger.info("Server created internal timer, executing periodly " + milisec + " miliseconds");
	}
	
	
	/**
	 * Creating server task scheduler (timer), called by
	 */
	private void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			
			logger.info("Server destroyed internal timer");
		}
	}

	
	/**
	 * Server can have some internal tasks when it is running. These tasks are specified by method {@link #serverTasks()} which in turn is called by this method.
	 */
	private synchronized void callServerTasks() throws RemoteException {
		if (!isRunning()) return;

		try {
			serverTasks();
			logger.info("Server has done timer internal tasks");
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Server got error to do timer internal tasks, error is " + e.getMessage());
		}
		
		SystemUtil.enhanceAuto();
	}

	
	/**
	 * Server can have some internal tasks when it is running. These tasks are specified by this method.
	 */
	protected abstract void serverTasks();

	
	/**
	 * Initialize storage system.
	 */
	protected abstract void initStorageSystem();
	
	
	/**
	 * Destroy storage system.
	 */
	protected abstract void destroyStorageSystem();
	
	
	@Override
	public boolean isStarted()  throws RemoteException {
		// TODO Auto-generated method stub
		return started;
	}

	
	@Override
	public boolean isPaused()  throws RemoteException {
		// TODO Auto-generated method stub
		return started && paused;
	}

	
	@Override
	public boolean isRunning()  throws RemoteException {
		// TODO Auto-generated method stub
		return started && !paused;
	}


	/**
	 * Getting bind name.
	 * @return gateway bind name.
	 */
	private String getGatewayBindName() {
		xURI uri = xURI.create( "rmi://localhost:" + config.getServerPort() + "/" + Protocol.GATEWAY);
		return uri.toString(); 
	}

	
	@Override
	public boolean addStatusListener(ServerStatusListener listener) 
			throws RemoteException {
		
    	synchronized (listenerList) {
    		try {
		        listenerList.add(ServerStatusListener.class, listener);
		        
		        logger.info("Server added successfully status listener " + 
		        		(listenerList.getListenerCount() - 1) + ": " + listener.getClass());
		        
		        return true;
    		}
    		catch (Throwable e) {
    			e.printStackTrace();
		        logger.error("Server fail to add status listener " + listener.getClass() + 
		        		", error is " + e.getMessage());
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
		
    	synchronized (listenerList) {
    		try {
    			listenerList.remove(ServerStatusListener.class, listener);
    	        logger.info("Server remove successfully status listener " + listener.getClass());
    	        return true;
    		}
    		catch (Throwable e) {
    			e.printStackTrace();
		        logger.error("Server fail to remove status listener " + listener.getClass() + 
		        		", error is " + e.getMessage());
    		}
    		
    		return false;
		}
    	
    }
	
	
	/**
     * Getting server status listener.
     * @return array of {@link ServerStatusListener}
     */
    public ServerStatusListener[] getStatusListeners() {
    	synchronized (listenerList) {
	        return listenerList.getListeners(ServerStatusListener.class);
    	}
    }

    
	@Override
	public DataConfig getConfig()  throws RemoteException {
		return config;
	}

	
	@Override
	public synchronized void setConfig(DataConfig config) throws RemoteException {
		if (isStarted())
			return;

		trans.lockWrite();
		try {
			if (this.config != config)
				this.config.putAll(config);
    		fireStatusEvent(new ServerStatusEvent(this, Status.setconfig));
    		
			logger.info("Server set configuration successfully");
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			logger.error("Server fail to set configuration, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
	}
	
	
	/**
     * Fire status event.
     * @param evt status event.
     */
	private void fireStatusEvent(ServerStatusEvent evt) {
		ServerStatusListener[] listeners = getStatusListeners();
		
		for (ServerStatusListener listener : listeners) {
			try {
				evt.setShutdownHookStatus(shutdownHookStatus);
				listener.statusChanged(evt);
		        logger.info("Server fire successfully status event " + evt + " to listener " + listener.getClass());
			}
			catch (Throwable e) {
				e.printStackTrace();
		        logger.error("Server fail to fire status event " + evt + " to listener " + listener.getClass() + 
		        		", caused by " + e.getMessage());
		        
				try {
					
					removeStatusListener(listener);
					
				}
				catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			} // end try
			
		} // end for
		
	}

	
	@Override
	public Server getRemoteServer(String account, String password) 
			throws RemoteException {
		if (config == null)
			return null;
		
		Server server = null;
		
		try {
			if (validateAccount(account, password, DataConfig.ACCOUNT_ADMIN_PRIVILEGE))
				server = this;
			else
				server = null;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			server = null;
			logger.error("Server: remote client fail to connect to server as control panel, caused by " + e.getMessage());
		}
		
		return server;
		
	}
	

	@Override
	public Service getRemoteService(String account, String password) 
			throws RemoteException {
		if (!isRunning())
			return null;
		
		Service service = null;
		try {
			if (validateAccount(account, password, DataConfig.ACCOUNT_ACCESS_PRIVILEGE)) {
				service = getService();
			}
			else
				service = null;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			service = null;
			logger.error("Server: remote client fail to connect to server as service, caused by " + e.getMessage());
		}
		
		return service;
	}

	
	@Override
	public void incRequest() throws RemoteException {
		activeMeasure.incRequestCount();
	}
	
	
	@Override
	public void decRequest() throws RemoteException {
		activeMeasure.decRequestCount();
	}

	
	@Override
	public ActiveMeasure getActiveMeasure() throws RemoteException {
		return activeMeasure;
	}
	
	
	/**
	 * (1) Interface "Gateway" has no method to retrieve PowerServer instance so that the client stub can't access PowerServer instance
	 * (2) Class "GatewayImpl" has "final" modifier. Modifier "final" is also important, it prevents from overriding injection class
	 * 
	 * So using Gateway is the solution of avoiding exporting PowerServer instance
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	final static private class GatewayImpl implements Gateway {

		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Internal gateway.
		 */
		private Gateway gateway = null;
		
		/**
		 * Constructor with specified gateway.
		 * @param gateway specified gateway.
		 */
		public GatewayImpl(Gateway gateway) {
			this.gateway = gateway;
		}
		
		@Override
		public Server getRemoteServer(String account, String password)
				throws RemoteException {
			// TODO Auto-generated method stub
			
			return gateway.getRemoteServer(account, password);
		}

		@Override
		public Service getRemoteService(String account, String password)
				throws RemoteException {
			// TODO Auto-generated method stub
			
			return gateway.getRemoteService(account, password);
		}
		
	}

	
}



