/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.hudup.core.logistic.LogUtil;

/**
 * This is abstract implementation of application;
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AppAbstract implements App {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal application creator.
	 */
	protected Appor appor = null;
	
	
	/**
     * Exporting flag.
     */
	protected boolean exported = false;

	
	/**
	 * Constructor with application creator.
	 * @param appor specified application creator.
	 */
	public AppAbstract(Appor appor) {
		this.appor = appor;
	}

	
	@Override
	public String getName() throws RemoteException {
		return appor != null ? appor.getName() : "Noname";
	}

	
	@Override
	public String getDesc() throws RemoteException {
		return getName();
	}


	@Override
	public synchronized boolean export(int serverPort) throws RemoteException {
		if (exported) return false;
		try {
			return (exported = (UnicastRemoteObject.exportObject(this, serverPort) != null));
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean unexport() throws RemoteException {
		if (exported) {
			try {
	        	return !(exported = !UnicastRemoteObject.unexportObject(this, true));
			}
			catch (Throwable e) {LogUtil.trace(e);}
			return false;
		}
		else
			return false;
	}

	
	@Override
	public boolean discard() throws RemoteException {
		boolean discarded = false;
		if (appor == null || !(appor instanceof ApporAbstract))
			discarded = discard0();
		else {
			ApporAbstract aa = (ApporAbstract)appor;
			if (this != aa.app)
				discarded = discard0();
			else
				discarded = aa.discard(this);
		}
		
		this.appor = null;
		
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return discarded;
	}


	/**
	 * Discard this application.
	 * @return true if closing is successful.
	 */
	protected abstract boolean discard0();


}
