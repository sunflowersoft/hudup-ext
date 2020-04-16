/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.UUID;

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
	 * Internal UUID.
	 */
	protected UUID uuid = null;
	
	
	/**
	 * Default constructor.
	 */
	public Timestamp() {
		// TODO Auto-generated constructor stub
		uuid = UUID.randomUUID();
	}

	
	/**
	 * Getting UUID of this time stamp.
	 * @return UUID of this time stamp.
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	
	/**
	 * Testing whether this time stamp is valid.
	 * @return whether this time stamp is valid.
	 */
	public boolean isValid() {
		return uuid != null;
	}


	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null)
			return uuid == null;
		else if (!(obj instanceof Timestamp))
			return false;
		else if (!((Timestamp)obj).isValid())
			return !isValid();
		else
			return this.uuid.equals(((Timestamp)obj).uuid);
	}
	
	
}
