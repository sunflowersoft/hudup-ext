/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.rmi.RemoteException;

/**
 * This interface supports whether the implemented object is agent.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface AgentSupport {

	
	/**
	 * Checking whether the implemented object is agent.
	 * @return whether the implemented object is agent.
	 * @throws RemoteException if any error raises.
	 */
	boolean isAgent() throws RemoteException;


	/**
	 * Setting whether the implemented object is agent.
	 * @param isAgent agent mode.
	 * @throws RemoteException if any error raises.
	 */
	void setAgent(boolean isAgent) throws RemoteException;
	
	
}
