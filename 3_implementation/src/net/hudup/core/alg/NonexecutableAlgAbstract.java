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
 * This is the most abstract class for non-executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class NonexecutableAlgAbstract extends ExecutableAlgAbstract
		implements NonexecutableAlg, NonexecutableAlgRemote {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NonexecutableAlgAbstract() {
		super();
	}

	
	/**
	 * This method is not used for non-executable algorithm.
	 */
	@Override
	public Object execute(Object input) throws RemoteException {
		return null;
	}


}
