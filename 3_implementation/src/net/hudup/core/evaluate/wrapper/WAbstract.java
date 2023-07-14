/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.hudup.core.evaluate.wrapper.WDoEvent.Type;

/**
 * This class implements partially the algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class WAbstract implements W {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maximum iteration.
	 */
	public final static String MAX_ITERATION_FIELD = "terminate_max_iteration";
	
	
	/**
	 * Default value for maximum iteration.
	 */
	public final static int MAX_ITERATION_DEFAULT = 1000;

	
	/**
	 * Holding a list of listeners.
	 */
    protected transient WListenerList listenerList = new WListenerList();

    
    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean doStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean doPaused = false;

    
	/**
	 * Internal configuration.
	 */
	protected WConfig config = new WConfig();
	
	
	/**
	 * Flag to indicate whether this PSO was exported.
	 */
	protected boolean exported = false;

	
	/**
	 * Default constructor.
	 */
	public WAbstract() {
		config.put(MAX_ITERATION_FIELD, MAX_ITERATION_DEFAULT);
	}

	
	@Override
	public Object learn(WSetting setting, Object...params) throws RemoteException {
		if (isDoStarted()) return null;
		
		if (setting == null) setting = getSetting();


		int maxIteration = config.getAsInt(MAX_ITERATION_FIELD);
		int iteration = 0;
		doStarted = true;
		while (doStarted && (maxIteration <= 0 || iteration < maxIteration)) {

			//Do main learning tasks.
			
			iteration ++;
			
			fireDoEvent(new WDoEventImpl(this, Type.doing, "w",
					"At iteration " + iteration + ": doing result",
					iteration, maxIteration));
			
			if (terminatedCondition()) //Fixing
				doStarted = false;
			
			synchronized (this) {
				while (doPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {Util.trace(e);}
				}
			}

		}
		
		synchronized (this) {
			doStarted = false;
			doPaused = false;
			
			fireDoEvent(new WDoEventImpl(this, Type.done, "pso",
				"At final iteration " + iteration + ": final result",
				iteration, iteration));

			notifyAll();
		}

		return null; //Fixing
	}
	
	
	/**
	 * Checking whether the terminated condition is satisfied.
	 * @param results learning/execution results.
	 * @return true then the algorithm can stop.
	 */
	protected abstract boolean terminatedCondition(Object...results); //Fixing
	
	
	@Override
	public void addListener(WListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(WListener.class, listener);
		}
	}


	@Override
	public void removeListener(WListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(WListener.class, listener);
		}
	}
	
	
	/**
	 * Getting an array of listeners.
	 * @return array of listeners.
	 */
	protected WListener[] getListeners() {
		if (listenerList == null) return new WListener[] {};
		synchronized (listenerList) {
			return listenerList.getListeners(WListener.class);
		}

	}
	
	
	/**
	 * Firing information event.
	 * @param evt information event.
	 */
	protected void fireInfoEvent(WInfoEvent evt) {
		if (listenerList == null) return;
		
		WListener[] listeners = getListeners();
		for (WListener listener : listeners) {
			try {
				listener.receivedInfo(evt);
			}
			catch (Throwable e) { 
				Util.trace(e);
			}
		}
	}

	
	/**
	 * Firing learning event.
	 * @param evt learning event.
	 */
	protected void fireDoEvent(WDoEvent evt) {
		if (listenerList == null) return;
		
		WListener[] listeners = getListeners();
		for (WListener listener : listeners) {
			try {
				listener.receivedDo(evt);
			}
			catch (Throwable e) {
				Util.trace(e);
			}
		}
	}


	@Override
	public boolean doPause() throws RemoteException {
		if (!isDoRunning()) return false;
		
		doPaused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return true;
	}


	@Override
	public boolean doResume() throws RemoteException {
		if (!isDoPaused()) return false;
		
		doPaused = false;
		notifyAll();
		
		return true;
	}


	@Override
	public boolean doStop() throws RemoteException {
		if (!isDoStarted()) return false;
		
		doStarted = false;
		
		if (doPaused) {
			doPaused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return true;
	}


	@Override
	public boolean isDoStarted() throws RemoteException {
		return doStarted;
	}


	@Override
	public boolean isDoPaused() throws RemoteException {
		return doStarted && doPaused;
	}


	@Override
	public boolean isDoRunning() throws RemoteException {
		return doStarted && !doPaused;
	}

	
	@Override
	public WConfig getConfig() throws RemoteException {
		return config;
	}


	@Override
	public void setConfig(WConfig config) throws RemoteException {
		if (config != null) this.config.putAll(config);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exported) return null;
		
		Remote stub = null;
		try {
			stub = UnicastRemoteObject.exportObject(this, serverPort);
		}
		catch (Exception e) {
			try {
				if (stub != null) UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Exception e2) {}
			stub = null;
		}
	
		exported = stub != null;
		return stub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (!exported) return;

		try {
        	UnicastRemoteObject.unexportObject(this, true);
			exported = false;
		}
		catch (NoSuchObjectException e) {
			exported = false;
			Util.trace(e);
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}

	
	@Override
	public void close() throws Exception {
		try {
			unexport();
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}


}
