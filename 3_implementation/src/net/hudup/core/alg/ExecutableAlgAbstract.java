/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

/**
 * This is the most abstract class for executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class ExecutableAlgAbstract extends AlgExtAbstract implements ExecutableAlg, ExecutableAlgRemote {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
     * Default constructor.
     */
    public ExecutableAlgAbstract() {
		super();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {ExecutableAlgRemote.class.getName()};
	}

	
}
