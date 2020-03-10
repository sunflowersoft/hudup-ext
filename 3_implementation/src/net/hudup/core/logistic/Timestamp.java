/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is the wrapper of a time stamp.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Timestamp implements Serializable {

	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal time stamp.
	 */
	protected long timestamp = 0;
	
	
	/**
	 * Default constructor.
	 */
	public Timestamp() {
		// TODO Auto-generated constructor stub
		timestamp = new Date().getTime();
	}

	
	/**
	 * Constructor with specified time stamp.
	 * @param timestamp specified time stamp.
	 */
	public Timestamp(long timestamp) {
		this.timestamp = timestamp >= 0 ? timestamp : 0;
	}
	
	
	/**
	 * Getting time stamp as long number.
	 * @return time stamp as long number.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	
	/**
	 * Testing whether this time stamp is valid.
	 * @return whether this time stamp is valid.
	 */
	public boolean isValid() {
		return timestamp > 0;
	}
	
	
}
