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
 * The class is a wrapper of remote executable algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote executable algorithm) and REM wrapper (normal executable algorithm).
 * The server creates REM stub (remote executable algorithm) and the client creates and uses the REM wrapper as normal executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class ExecutableAlgRemoteWrapper extends AlgExtRemoteWrapper implements ExecutableAlg, ExecutableAlgRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with specified remote executable algorithm.
	 * @param remoteExecutableAlg remote executable algorithm.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlgRemote remoteExecutableAlg) {
		super(remoteExecutableAlg);
	}

	
	/**
	 * Constructor with specified remote executable algorithm and exclusive mode.
	 * @param remoteExecutableAlg remote executable algorithm.
	 * @param exclusive exclusive mode.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlgRemote remoteExecutableAlg, boolean exclusive) {
		super(remoteExecutableAlg, exclusive);
	}


	@Override
	public Object execute(Object input) throws RemoteException {
		return ((ExecutableAlgRemote)remoteAlg).execute(input);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {ExecutableAlgRemote.class.getName()};
	}

	
}
