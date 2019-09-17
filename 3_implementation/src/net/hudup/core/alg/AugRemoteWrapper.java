/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class is wrapper of remote AUgorithm {@link AugRemote}.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@NextUpdate
@BaseClass //This class is not base class but the base class annotation prevents this class to be registered in plug-in storage.
public class AugRemoteWrapper extends ExecutableAlgRemoteWrapper {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with remote AUgorithm.
	 * @param remoteAug remote AUgorithm.
	 */
	public AugRemoteWrapper(AugRemote remoteAug) {
		super(remoteAug);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote AUgorithm and exclusive mode.
	 * @param remoteAug remote AUgorithm.
	 * @param exclusive exclusive mode.
	 */
	public AugRemoteWrapper(AugRemote remoteAug, boolean exclusive) {
		super(remoteAug, exclusive);
		// TODO Auto-generated constructor stub
	}

	
}
