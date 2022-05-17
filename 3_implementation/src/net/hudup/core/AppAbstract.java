/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.rmi.RemoteException;

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
		return discarded;
	}


	/**
	 * Discard this application.
	 * @return true if closing is successful.
	 */
	protected abstract boolean discard0();


}
