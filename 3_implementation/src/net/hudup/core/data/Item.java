/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.Cloneable;

/**
 * This class represent profile of an item, called item profile, which inherits directly {@link Profile}.
 * Note, {@link Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class Item extends Profile implements Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Item() {
		super();
	}
	
	
	
	@Override
	public Object clone() {
		Item item = new Item();
		item.attRef = this.attRef;
		
		item.attValues.clear();
		item.attValues.addAll(this.attValues);
		
		return item;
	}
	
	
}
