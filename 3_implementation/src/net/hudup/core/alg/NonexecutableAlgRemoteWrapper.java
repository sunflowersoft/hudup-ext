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
 * The class is a wrapper of remote non-executable algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote non-executable algorithm) and REM wrapper (normal non-executable algorithm).
 * The server creates REM stub (remote non-executable algorithm) and the client creates and uses the REM wrapper as normal non-executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class NonexecutableAlgRemoteWrapper extends AlgExtRemoteWrapper implements NonexecutableAlg, NonexecutableAlgRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with specified remote non-executable algorithm.
	 * @param remoteNonexecutableAlg remote non-executable algorithm.
	 */
	public NonexecutableAlgRemoteWrapper(NonexecutableAlgRemote remoteNonexecutableAlg) {
		super(remoteNonexecutableAlg);
	}

	
	/**
	 * Constructor with specified remote non-executable algorithm and exclusive mode.
	 * @param remoteNonexecutableAlg remote non-executable algorithm.
	 * @param exclusive exclusive mode.
	 */
	public NonexecutableAlgRemoteWrapper(NonexecutableAlgRemote remoteNonexecutableAlg, boolean exclusive) {
		super(remoteNonexecutableAlg, exclusive);
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {NonexecutableAlgRemote.class.getName()};
	}

	
}
