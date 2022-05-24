/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;

/**
 * This class represents an item of dataset pool with given name.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolExchangedItem implements Cloneable, Serializable, Comparable<DatasetPoolExchangedItem> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Name of dataset pool.
	 */
	protected String name = null;
	
	
	/**
	 * Internal dataset pool.
	 */
	protected DatasetPoolExchanged pool = null;
	
	
	/**
	 * Constructor with name and pool.
	 * @param name specified name.
	 * @param pool specified pool.
	 */
	public DatasetPoolExchangedItem(String name, DatasetPoolExchanged pool) {
		this.name = name;
		this.pool = pool;
	}


	/**
	 * Getting name.
	 * @return name.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Getting pool.
	 * @return pool.
	 */
	public DatasetPoolExchanged getDatasetPool() {
		return pool;
	}
	
	
	@Override
	public int compareTo(DatasetPoolExchangedItem o) {
		return this.name.compareTo(o.name);
	}


	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}
	
	
}
