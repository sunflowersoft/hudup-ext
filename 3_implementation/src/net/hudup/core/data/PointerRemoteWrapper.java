/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This class is remote wrapper of pointer.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PointerRemoteWrapper extends DatasetRemoteWrapper implements Pointer, PointerRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with remote pointer.
	 * @param remotePointer remote pointer.
	 */
	public PointerRemoteWrapper(PointerRemote remotePointer) {
		super(remotePointer);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote pointer and exclusive mode.
	 * @param remotePointer remote pointer.
	 * @param exclusive exclusive mode.
	 */
	public PointerRemoteWrapper(PointerRemote remotePointer, boolean exclusive) {
		super(remotePointer, exclusive);
		// TODO Auto-generated constructor stub
	}

	
}
