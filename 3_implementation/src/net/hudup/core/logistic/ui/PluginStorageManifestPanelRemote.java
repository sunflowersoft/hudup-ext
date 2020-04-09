package net.hudup.core.logistic.ui;

import javax.swing.JOptionPane;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.xURI;

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
	 * @param bindUri bound URI.
	 */
	public PluginStorageManifestPanelRemote(PluginChangedListener listener, xURI bindUri) {
		// TODO Auto-generated constructor stub
		super(listener, bindUri);
		this.server = listener instanceof PowerServer ? (PowerServer)listener : null;
		
		JOptionPane.showMessageDialog(
			this, 
			"Remote plugin storage manifest not implemented yet", 
			"Not implemented yet", 
			JOptionPane.ERROR_MESSAGE);
	}



}
