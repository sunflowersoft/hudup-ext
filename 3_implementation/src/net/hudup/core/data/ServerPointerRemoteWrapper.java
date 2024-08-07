/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This class is remote wrapper of server pointer.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ServerPointerRemoteWrapper extends PointerRemoteWrapper implements ServerPointer, ServerPointerRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Default constructor.
     */
    protected ServerPointerRemoteWrapper() {

    }

    
	/**
	 * Constructor with remote server pointer.
	 * @param remoteServerPointer remote server pointer.
	 */
	public ServerPointerRemoteWrapper(ServerPointerRemote remoteServerPointer) {
		super(remoteServerPointer);
	}

	
	/**
	 * Constructor with remote server pointer and exclusive mode.
	 * @param remoteServerPointer remote server pointer.
	 * @param exclusive eclusive mode.
	 */
	public ServerPointerRemoteWrapper(ServerPointerRemote remoteServerPointer, boolean exclusive) {
		super(remoteServerPointer, exclusive);
	}

	
}
