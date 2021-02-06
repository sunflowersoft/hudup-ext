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
 * The class is a wrapper of remote executing-learning algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote executing-learning algorithm) and REM wrapper (normal executing-learning algorithm).
 * The server creates REM stub (remote executing-learning algorithm) and the client creates and uses the REM wrapper as normal executing-learning algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class ExecuteAsLearnAlgRemoteWrapper extends AlgExtRemoteWrapper implements ExecuteAsLearnAlg, ExecuteAsLearnAlgRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with specified remote executing-learning algorithm.
	 * @param remoteExecuteAsLearnAlg remote executing-learning algorithm.
	 */
	public ExecuteAsLearnAlgRemoteWrapper(ExecuteAsLearnAlgRemote remoteExecuteAsLearnAlg) {
		super(remoteExecuteAsLearnAlg);
	}

	
	/**
	 * Constructor with specified remote executing-learning algorithm and exclusive mode.
	 * @param remoteExecuteAsLearnAlg remote executing-learning algorithm.
	 * @param exclusive exclusive mode.
	 */
	public ExecuteAsLearnAlgRemoteWrapper(ExecuteAsLearnAlgRemote remoteExecuteAsLearnAlg, boolean exclusive) {
		super(remoteExecuteAsLearnAlg, exclusive);
	}


	@Override
	public Object executeAsLearn(Object input) throws RemoteException {
		return ((ExecuteAsLearnAlgRemote)remoteAlg).executeAsLearn(input);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {ExecuteAsLearnAlgRemote.class.getName()};
	}

	
}
