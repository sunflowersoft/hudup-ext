/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext2;

import net.hudup.core.client.PowerServer;
import net.hudup.server.Transaction;
import net.hudup.server.ext.ExtendedService;

/**
 * This class is extended version of default service with improvement.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtendedService2 extends ExtendedService {

	
	/**
	 * Constructor with specified transaction.
	 * @param trans specified transaction.
	 */
	public ExtendedService2(Transaction trans) {
		super(trans);
	}

	
	/**
	 * Constructor with specified transaction and referred power server.
	 * @param trans specified transaction.
	 * @param referredServer referred power server.
	 */
	public ExtendedService2(Transaction trans, PowerServer referredServer) {
		super(trans, referredServer);
	}


	@Override
	protected void loadEvaluators() {
		super.loadEvaluators();
	}


	@Override
	protected void purgeListeners() {
		super.purgeListeners();
	}

	
}
