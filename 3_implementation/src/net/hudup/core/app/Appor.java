/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import java.io.Serializable;

import net.hudup.core.client.PowerServer;

/**
 * This interface represents an application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Appor extends Serializable, java.lang.AutoCloseable {

	
	/**
	 * Getting name of this application creator.
	 * @return name of this application creator.
	 */
	String getName();
	
	
	/**
	 * Create a application.
	 * @param server power server.
	 * @return created application.
	 */
	App create(PowerServer server);
	
	
}
