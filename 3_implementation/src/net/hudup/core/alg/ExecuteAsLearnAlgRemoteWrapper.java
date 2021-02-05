/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.logistic.BaseClass;

/**
 * The class is a wrapper of remote executing-learning algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class ExecuteAsLearnAlgRemoteWrapper extends ExecutableAlgRemoteWrapper implements ExecuteAsLearnAlg, ExecuteAsLearnAlgRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified remote executing-learning algorithm.
	 * @param remoteExecuteAsLearnAlg remote non-executable algorithm.
	 */
	public ExecuteAsLearnAlgRemoteWrapper(ExecuteAsLearnAlgRemote remoteExecuteAsLearnAlg) {
		super(remoteExecuteAsLearnAlg);
	}

	
	/**
	 * Constructor with specified remote non-executable algorithm and exclusive mode.
	 * @param remoteExecuteAsLearnAlg remote executing-learning algorithm.
	 * @param exclusive exclusive mode.
	 */
	public ExecuteAsLearnAlgRemoteWrapper(ExecuteAsLearnAlgRemote remoteExecuteAsLearnAlg, boolean exclusive) {
		super(remoteExecuteAsLearnAlg, exclusive);
	}


	@Override
	public Object learnAsExecuteStart(Object input) throws RemoteException {
		return ((ExecuteAsLearnAlgRemote)remoteAlg).learnAsExecuteStart(input);
	}

	
}
