/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import net.hudup.core.data.DatasetPoolsService;

/**
 * This interface represents a local service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ServiceLocal {
	

	/**
	 * Getting referred power server.
	 * @return referred power server.
	 */
	PowerServer getReferredServer();


	/**
	 * Getting dataset pools service.
	 * @return dataset pools service.
	 */
	DatasetPoolsService getDatasetPoolsService();

	
}
