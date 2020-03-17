/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This class is remote wrapper of KBase pointer.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class KBasePointerRemoteWrapper extends PointerRemoteWrapper implements KBasePointer, KBasePointerRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with remote KBase pointer.
	 * @param remoteKBasePointer remote KBase pointer.
	 */
	public KBasePointerRemoteWrapper(KBasePointerRemote remoteKBasePointer) {
		super(remoteKBasePointer);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote KBase pointer and exclusive mode.
	 * @param remoteKBasePointer remote KBase pointer.
	 * @param exclusive exclusive mode.
	 */
	public KBasePointerRemoteWrapper(KBasePointerRemote remoteKBasePointer, boolean exclusive) {
		super(remoteKBasePointer, exclusive);
		// TODO Auto-generated constructor stub
	}

	
}
