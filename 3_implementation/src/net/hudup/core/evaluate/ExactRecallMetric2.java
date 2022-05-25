/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

/**
 * This class implements recall metric for estimation on query ID in more detailed.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExactRecallMetric2 extends ExactRecallMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExactRecallMetric2() {

	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Exact recall 2";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Hudup";
	}

	
	@Override
	public String getName() {
		return "Exact recall 2";
	}


}
