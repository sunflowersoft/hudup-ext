package net.hudup.core.logistic.ui;

import javax.swing.JOptionPane;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.client.PowerServer;

/**
 * Panel for plug-in storage manifest from remote connection.
 * 
 * @author Loc Nguyen
 * @version 13.0
 */
public class PluginStorageManifestPanelRemote extends PluginStorageManifestPanel {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Constructor with server and plug-in changed listener.
	 * @param server specified server.
	 * @param listener specified listener.
	 */
	public PluginStorageManifestPanelRemote(PowerServer server, PluginChangedListener listener) {
		// TODO Auto-generated constructor stub
		super(listener, server);
		this.server = server;
		
		JOptionPane.showMessageDialog(
			this, 
			"Remote plugin storage manifest not implemented yet", 
			"Not implemented yet", 
			JOptionPane.ERROR_MESSAGE);
	}


	@Override
	protected PluginStorageManifest createPluginStorageManifest(PluginChangedListener listener, Object parameter) {
		// TODO Auto-generated method stub
		if ((parameter == null) || !(parameter instanceof PowerServer))
			return super.createPluginStorageManifest(listener, parameter);
		else
			return new PluginStorageManifestRemote((PowerServer)parameter, listener == null || listener.getPort() < 0 ? 0 : listener.getPort());
	}

}
