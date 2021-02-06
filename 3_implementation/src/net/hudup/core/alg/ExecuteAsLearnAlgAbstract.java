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
 * This is the most abstract class for executing-learning algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class ExecuteAsLearnAlgAbstract extends AlgExtAbstract implements ExecuteAsLearnAlg, ExecuteAsLearnAlgRemote {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
     * Default constructor.
     */
    public ExecuteAsLearnAlgAbstract() {
		super();
	}


	@Override
	public Object learnStart(Object... info) throws RemoteException {
		return executeAsLearn(null);
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {ExecuteAsLearnAlgRemote.class.getName()};
	}

	
}
