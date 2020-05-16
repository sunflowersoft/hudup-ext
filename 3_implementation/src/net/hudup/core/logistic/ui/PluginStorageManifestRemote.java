/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import net.hudup.core.client.PowerServer;

/**
 * This class allows to manage plug-in storage from remote connection. It is extended version of {@link PluginStorageManifest}.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class PluginStorageManifestRemote extends PluginStorageManifest {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Constructor with power server.
	 * @param server power server.
	 */
	public PluginStorageManifestRemote(PowerServer server) {
		this(server, new RegisterRemoteTM(server), 0);
	}


	/**
	 * Default constructor with specified server and exported port.
	 * @param server power server.
	 * @param port specified exported port.
	 */
	public PluginStorageManifestRemote(PowerServer server, int port) {
		this(server, new RegisterRemoteTM(server), port);
	}
	
	
	/**
	 * Default constructor with power server, specified model and port.
	 * @param server power server.
	 * @param rtm specified model.
	 * @param port exported port. This port is used to export algorithms. Port 0 means to export algorithms at random port for getting remote references, not for naming.
	 */
	protected PluginStorageManifestRemote(PowerServer server, RegisterRemoteTM rtm, int port) {
		super(rtm, port);
		this.server = server;
	}
	
	
}


/**
 * This is table model of {@link PluginStorageManifestRemote} because {@link PluginStorageManifestRemote} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
class RegisterRemoteTM extends RegisterTM {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Power server.
	 */
	protected PowerServer server = null;

	
	/**
	 * Default constructor.
	 */
	public RegisterRemoteTM(PowerServer server) {
		super();
		this.server = server;
	}
	
	
}
