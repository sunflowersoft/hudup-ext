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
 * This is abstract implementation of task;
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class TaskAbstract implements Task {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal tasker.
	 */
	protected Tasker tasker = null;
	
	
	/**
	 * Constructor with tasker.
	 * @param tasker specified tasker.
	 */
	public TaskAbstract(Tasker tasker) {
		this.tasker = tasker;
	}

	
	@Override
	public String getName() throws RemoteException {
		return tasker.getName();
	}

	
	@Override
	public String getDesc() throws RemoteException {
		return getName();
	}


}
