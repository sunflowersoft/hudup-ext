/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This class represents a temporal exchanged dataset pool to make pool result empty.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolExchangedMakePoolResultEmpty extends DatasetPoolExchanged {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Forcing unexport flag.
	 */
	protected boolean forceUnexport = false;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolExchangedMakePoolResultEmpty(boolean forceUnexport) {
		this.forceUnexport = forceUnexport;

	}
	

	/**
	 * Getting forcing unexport flag.
	 * @return forcing unexport flag.
	 */
	public boolean isForceUnexport() {
		return forceUnexport;
	}
	
	
}
