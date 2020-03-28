/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.EventListenerList;

import net.hudup.core.Constants;
import net.hudup.core.ExtraStorage;
import net.hudup.core.PluginStorage;
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
import net.hudup.core.logistic.LogUtil;
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
	public final static String  SERVER_POLICY = Constants.RESOURCES_PACKAGE + "hudup_server.policy";
	
	
	/**
	 * Starting flag.
	 */
	protected volatile boolean started = false;
	
	
	/**
	 * Pausing flag.
	 */
	protected volatile boolean paused = false;
	
	
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
	protected volatile boolean shutdownHookStatus = false;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public PowerServerImpl(PowerServerConfig config) {
		super();
		
		try {
			this.config = config;
			this.config.addReadOnly(DataConfig.MAX_RATING_FIELD);
			this.config.addReadOnly(DataConfig.MIN_RATING_FIELD);
			this.trans = createTransaction();
			this.gateway = new GatewayImpl(this);		
			
			SystemUtil.setSecurityPolicy(getClass().getResource(SERVER_POLICY));
			
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
			LogUtil.trace(e);
			LogUtil.error("Power server constructor caused error " + e.getMessage());
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
					LogUtil.trace(e);
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
	public synchronized boolean start() throws RemoteException {
		// TODO Auto-generated method stub
		if (isStarted()) return false;
    	
    	trans.lockWrite();
    	try {
    		LogUtil.info("Power server is initializing to start, please wait...");
			doWhenStart();
			activeMeasure.reset();

			UnicastRemoteObject.exportObject(getService(), config.getServerPort());

			createTimer();
			
			started = true;
			fireStatusEvent(new ServerStatusEvent(this, Status.started));
			LogUtil.info("POWER SERVER IS SERVING AT PORT " + config.getServerPort());
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			
			try {
				doWhenStop();
			}
			catch (Throwable e2) {
				e2.printStackTrace();
			}
			
			LogUtil.error("Power server failed to start, caused by " + e.getMessage());
		}
    	finally {
        	trans.unlockWrite();
    	}
    	
		SystemUtil.enhanceAuto();
		
		return true;
	}

    
    /**
     * Some tasks right after started.
     */
    protected abstract void doWhenStart();
    
    
	@Override
	public synchronized boolean pause() throws RemoteException {
		// TODO Auto-generated method stub
		if (!isRunning()) return false;

    	trans.lockWrite();
    	
		destroyTimer();
		
		paused = true;
		fireStatusEvent(new ServerStatusEvent(this, Status.paused));
		LogUtil.info("Power server paused");
		
		return true;
	}

	
	@Override
	public synchronized boolean resume() throws RemoteException {
		// TODO Auto-generated method stub
		//Which thread locked server can unlock server. This feature is used for security of service.
		//However, this feature causes trouble in remote control.
		if (!trans.isWriteLockedByCurrentThread())
			return false;
			
		if (!isPaused()) return false;
        	
		createTimer();
    	
		paused = false;
		fireStatusEvent(new ServerStatusEvent(this, Status.resumed));
		LogUtil.info("Power server resumed");
		
    	trans.unlockWrite();
    	
    	return true;
	}

	
	@Override
	public synchronized boolean stop()  throws RemoteException {
		// TODO Auto-generated method stub
		if (!isStarted()) return false;
		
		if (isPaused()) {
			//Which thread locked server can unlock server. This feature is used for security of service.
			//However, this feature causes trouble in remote control.
			if (!trans.isWriteLockedByCurrentThread())
				return false;
			
			paused = false;
        	trans.unlockWrite();
		}
		
    	trans.lockWrite();
    	try {
    		LogUtil.info("Power server is initializing to stop, please waiting...");
			destroyTimer();
			
			UnicastRemoteObject.unexportObject(getService(), true);
				
        	doWhenStop();
			activeMeasure.reset();
			ExtraStorage.clearUnmanagedExportedObjects(); //Added by Loc Nguyen: 2020.03.15
			started = false;
    		fireStatusEvent(new ServerStatusEvent(this, Status.stopped));
    		LogUtil.info("Power server stopped");
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block

			LogUtil.trace(e);
			LogUtil.error("Power server failed to stop, caused by " + e.getMessage());
		}
    	finally {
        	trans.unlockWrite();
    	}
    	
    	return true;
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
	 * Shutdown server, after server shutdown, program exits. Called by {@link #exit()}.
	 * @throws RemoteException if any error raises.
	 */
	protected synchronized void shutdown() throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null) return;
		
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
        		LogUtil.trace(e);
        	}
        	
        	destroyStorageSystem();
			
    		registry = null;
    		config.save();
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			LogUtil.error("Power server got error when shutdowning, error is " + e.getMessage());
		} 
    	finally {
        	trans.unlockWrite();
    	}
		config = null;
		LogUtil.info("Power server shutdown");
    	
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
		
		//Added date: 2019.09.15 by Loc Nguyen.
		PluginStorage.clear();
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
						LogUtil.trace(e);
						LogUtil.error("Calling power server tasks causes error " + e.getMessage());
					}
				}
			}, 
			milisec, 
			milisec);
		
		LogUtil.info("Power server created internal timer, executing periodly " + milisec + " miliseconds");
	}
	
	
	/**
	 * Creating server task scheduler (timer), called by
	 */
	private void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			
			LogUtil.info("Power server destroyed internal timer");
		}
	}

	
	/**
	 * Server can have some internal tasks when it is running. These tasks are specified by method {@link #serverTasks()} which in turn is called by this method.
	 */
	private synchronized void callServerTasks() throws RemoteException {
		if (!isRunning()) return;

		try {
			serverTasks();
			LogUtil.info("Power server has done timer internal tasks");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Power server got error to do timer internal tasks, error is " + e.getMessage());
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
		        
		        LogUtil.info("Power server added successfully status listener " + 
		        		(listenerList.getListenerCount() - 1) + ": " + listener.getClass());
		        
		        return true;
    		}
    		catch (Throwable e) {
    			LogUtil.trace(e);
    			LogUtil.error("Power server failed to add status listener " + listener.getClass() + 
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
    			LogUtil.info("Power server removed successfully status listener " + listener.getClass());
    	        return true;
    		}
    		catch (Throwable e) {
    			LogUtil.trace(e);
    			LogUtil.error("Power server failed to remove status listener " + listener.getClass() + 
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
    		
    		LogUtil.info("Power server set configuration successfully");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			LogUtil.error("Power server failed to set configuration, caused by " + e.getMessage());
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
				LogUtil.info("Power server fired successfully status event " + evt + " to listener " + listener.getClass());
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Power server failed to fire status event " + evt + " to listener " + listener.getClass() + 
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
			LogUtil.trace(e);
			server = null;
			LogUtil.error("Remote client failed to connect to this power server as control panel, caused by " + e.getMessage());
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
			LogUtil.trace(e);
			service = null;
			LogUtil.error("Remote client failed to connect to this power server as service, caused by " + e.getMessage());
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


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			shutdown();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
}



