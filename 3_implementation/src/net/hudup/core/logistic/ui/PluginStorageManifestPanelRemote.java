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
	 * @param listener specified listener.
	 */
	public PluginStorageManifestPanelRemote(PluginChangedListener listener) {
		// TODO Auto-generated constructor stub
		super(listener);
		this.server = listener instanceof PowerServer ? (PowerServer)listener : null;
		
		JOptionPane.showMessageDialog(
			this, 
			"Remote plugin storage manifest not implemented yet", 
			"Not implemented yet", 
			JOptionPane.ERROR_MESSAGE);
	}



}
