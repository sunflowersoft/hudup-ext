/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.factory;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.ui.UnitTable;
import net.hudup.core.logistic.UriAssoc;
import net.hudup.core.logistic.xURI;

/**
 * This interface defines factory utility for creating suitable provider associator {@link ProviderAssoc}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Factory {

	
	/**
	 * Creating suitable provider associator according to data configuration.
	 * @param config specified configuration.
	 * @return suitable provider associator according to data configuration referred by {@link ProviderAssoc}.
	 */
	ProviderAssoc createProviderAssoc(DataConfig config);
	
	
	/**
	 * Utility method to create a table for showing data in unit.
	 * @param uri store URI.
	 * @return {@link UnitTable} created.
	 */
	UnitTable createUnitTable(xURI uri);
	
	
//	/**
//	 * Creating suitable URI associator according to data configuration.
//	 * @param config specified configuration.
//	 * @return suitable URI associator according to data configuration referred by {@link UriAssoc}.
//	 */
//	UriAssoc createUriAssoc(DataConfig config);


	/**
	 * Creating suitable URI associator according to specified URI.
	 * @param uri specified URI.
	 * @return suitable URI associator according to specified URI referred by {@link UriAssoc}.
	 */
	UriAssoc createUriAssoc(xURI uri);

}
