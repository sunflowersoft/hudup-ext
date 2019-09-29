/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.event.EventListenerList;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * The class is a wrapper of remote algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote algorithm) and REM wrapper (normal algorithm).
 * The server creates REM stub (remote algorithm) and the client creates and uses the REM wrapper as normal algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class AlgRemoteWrapper implements Alg, AlgRemote, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


    /**
     * Exported algorithm must be serializable.
     */
    protected AlgRemote exportedStub = null;

	
	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote algorithm. It must be serializable.
	 */
	protected AlgRemote remoteAlg = null;
	
	
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();
    

	/**
	 * Constructor with specified remote algorithm.
	 * @param remoteAlg remote algorithm.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg) {
		// TODO Auto-generated constructor stub
		this(remoteAlg, true);
	}

	
	/**
	 * Constructor with specified remote algorithm and exclusive mode.
	 * @param remoteAlg remote algorithm.
	 * @param exclusive exclusive mode.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteAlg = remoteAlg;
		this.exclusive = exclusive;
	}

	
	/**
	 * Getting exclusive mode.
	 * @return exclusive mode.
	 */
	public boolean isExclusive() {
		return exclusive;
	}
	
	
	/**
	 * Setting exclusive mode.
	 * @param exclusive exclusive mode.
	 */
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
	
	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteAlg.queryName();
	}

	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteAlg.queryConfig();
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		try {
			return remoteAlg.queryConfig();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AlgAbstract)
			((AlgAbstract)remoteAlg).resetConfig();
		else
			LogUtil.error("resetConfig() not supported");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AlgAbstract)
			return ((AlgAbstract)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("createDefaultConfig() not supported");
			return null;
		}
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		try {
			return remoteAlg.queryName();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteAlg.getDescription();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof AlgAbstract) {
			AlgAbstract newAlg = (AlgAbstract) ((AlgAbstract)remoteAlg).newInstance();
			return new AlgRemoteWrapper(newAlg, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteAlg.addSetupListener(listener);
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteAlg.removeSetupListener(listener);
	}


	@Override
	public void fireSetupEvent(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		remoteAlg.fireSetupEvent(evt);
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		remoteAlg.receivedSetup(evt);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		//Remote wrapper can export itself because this function is useful when the wrapper as remote algorithm can be called remotely by remote evaluator via Evaluator.remoteStart method.
		if (exportedStub == null)
			exportedStub = (AlgRemote) NetUtil.RegistryRemote.export(this, serverPort);
	
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteAlg != null) {
			try {
				remoteAlg.unexport();
			} catch (Exception e) {e.printStackTrace();}
		}
		remoteAlg = null;
		
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
		}
	}

	
	/**
	 * Getting remote algorithm.
	 * @return remote algorithm.
	 */
	public AlgRemote getRemoteAlg() {
		return remoteAlg;
	}

	
	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return DSUtil.shortenVerbalName(getName());
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
