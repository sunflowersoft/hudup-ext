/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import net.hudup.core.logistic.BaseClass;

/**
 * The class is a wrapper of remote non-executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class NonexecutableAlgRemoteWrapper extends ExecutableAlgRemoteWrapper implements NonexecutableAlg, NonexecutableAlgRemote {

	
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

	
}
