/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.ExtraStorage;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * This is abstract class of {@link Alg} interface.
 * This abstract class gives out default implementation for some methods such as {@link #getConfig()},
 * {@link #resetConfig()} and {@link #createDefaultConfig()}. 
 * It also declares the configuration variable {@link #config}.
 * Note that every {@link Alg} owns a configuration specified by {@link DataConfig} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AlgAbstract implements Alg, AlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * This variable represents configuration of algorithm. It is returned value of {@link #getConfig()} method.
	 * The {@link #resetConfig()} resets it.
	 */
	protected DataConfig config = null;

	
    /**
     * Exported algorithm stub must be serializable.
     */
    protected AlgRemote exportedStub = null;
    
    
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected transient EventListenerList2 listenerList = new EventListenerList2();
    

    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean learnStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean learnPaused = false;

    
    /**
	 * Default constructor.
	 */
	public AlgAbstract() {
		this.config = createDefaultConfig();
	}

	
	@Override
	public Object learnStart(Object... info) throws RemoteException {
		if (isLearnStarted()) return null;
		
		int maxIteration = 1000;
		int iteration = 0;
		learnStarted = true;
		while (learnStarted && (maxIteration <= 0 || iteration < maxIteration)) {
			
			//Do something with main tasks here.
			
			
			iteration ++;

			//Pseudo-code to fire doing setup event.
			fireSetupEvent(new SetupAlgEvent(this, Type.doing, getName(), "Setting up is doing: " + getDescription(), iteration, maxIteration));
			
			//if (terminatedCondition)
				learnStarted = false; //Pseudo-code to stop learning process.

			synchronized (this) {
				while (learnPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {LogUtil.trace(e);}
				}
			}
			
		}
		
		synchronized (this) {
			learnStarted = false;
			learnPaused = false;
			
			//Pseudo-code to fire done setup event.
			fireSetupEvent(new SetupAlgEvent(this, Type.done, getName(), "Setting up is done: " + getDescription(), iteration, maxIteration));

			notifyAll();
		}
		
		return null;
	}


	@Override
	public synchronized boolean learnPause() throws RemoteException {
		if (!isLearnRunning()) return false;
		
		learnPaused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}


	@Override
	public synchronized boolean learnResume() throws RemoteException {
		if (!isLearnPaused()) return false;
		
		learnPaused = false;
		notifyAll();
		
		return true;
	}


	@Override
	public synchronized boolean learnStop() throws RemoteException {
		if (!isLearnStarted()) return false;
		
		learnStarted = false;
		
		if (learnPaused) {
			learnPaused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}


	@Override
	public boolean learnForceStop() throws RemoteException {
		return learnStop();
	}


	@Override
	public boolean isLearnStarted() throws RemoteException {
		return learnStarted;
	}


	@Override
	public boolean isLearnPaused() throws RemoteException {
		return learnStarted && learnPaused;
	}


	@Override
	public boolean isLearnRunning() throws RemoteException {
		return learnStarted && !learnPaused;
	}


	@Override
	public DataConfig getConfig() {
		return config;
	}

	
	/**
	 * Setting the configuration of this algorithm by specified configuration.
	 * @param config specified configuration.
	 */
	public synchronized void setConfig(DataConfig config) {
		this.config = config;
	}
	
	
	@Override
	public synchronized void resetConfig() {
		config.clear();
		config.putAll(createDefaultConfig());
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig();
		config.put(DataConfig.DELAY_UNSETUP, false); //Please pay attention to this code line.
		return config;
	}

	
	@Override
	public String toString() {
		return DSUtil.shortenVerbalName(getName());
	}


	@Override
	public String queryName() throws RemoteException {
		return getName();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {AlgRemote.class.getName()};
	}


	@Override
	public DataConfig queryConfig() throws RemoteException {
		DataConfig config = new DataConfig();
		config.putAll(getConfig());
		return config;
	}


	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		if (listenerList == null) listenerList = new EventListenerList2();
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		if (listenerList == null) return;
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
	}


	/**
	 * Getting an array of listeners for this EM.
	 * @return array of listeners for this EM.
	 */
	protected SetupAlgListener[] getSetupListeners() {
		if (listenerList == null) return new SetupAlgListener[] {};
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		if (listenerList == null) return;
		
		SetupAlgListener[] listeners = getSetupListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		fireSetupEvent(evt);
	}


	@Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Class.forName(className);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null)
			exportedStub = (AlgRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
	
			ExtraStorage.removeUnmanagedExportedObject(this);
		}
	}

	
	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}


//	@Override
//	public boolean isAgent() throws RemoteException {
//		return config.getAsBoolean(DataConfig.AGENT_FIELD);
//	}
//
//
//	@Override
//	public void setAgent(boolean isAgent) throws RemoteException {
//		config.put(DataConfig.AGENT_FIELD, isAgent); //Should use variable instead.
//	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	public Alg newInstance() {
		try {
			Alg alg = getClass().getDeclaredConstructor().newInstance();
			if (this instanceof DuplicatableAlg)
				alg.getConfig().putAll((DataConfig)this.getConfig().clone());
			
			return alg;
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			unexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
	}
	
	
}
