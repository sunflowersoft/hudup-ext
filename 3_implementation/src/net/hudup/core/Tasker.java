/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import net.hudup.core.client.PowerServer;

/**
 * This interface represents a tasker.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Tasker {

	
	/**
	 * Getting name of this tasker.
	 * @return name of this tasker.
	 */
	String getName();
	
	
	/**
	 * Create a task.
	 * @param server power server.
	 * @return created task.
	 */
	Task create(PowerServer server);
	
	
}
