/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.JOptionPane;

import net.hudup.core.PluginAlgDesc2ListMap;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ui.DatasetConfigurator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.SnapshotParserImpl;

/**
 * Panel for plug-in storage manifest from remote connection.
 * 
 * @author Loc Nguyen
 * @version 13.0
 */
public class PluginStorageManifestPanelRemote extends PluginStorageManifestPanel implements ClassProcessor {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;

	
	/**
	 * Constructor with server and plug-in changed listener.
	 * @param listener specified listener.
	 * @param connectInfo connection information.
	 */
	public PluginStorageManifestPanelRemote(PluginChangedListener listener, ConnectInfo connectInfo) {
		super(listener, connectInfo = (connectInfo != null ? connectInfo : new ConnectInfo()));
		
		this.server = (listener != null && listener instanceof PowerServer) ? (PowerServer)listener : null;
		this.connectInfo = connectInfo;
		
		if (connectInfo.bindUri != null) {
			setEnabled(false);
			tblRegister.setEditable(this.server != null);
			reloadAlg.setEnabled(this.server != null);
			apply.setEnabled(this.server != null);
			reset.setEnabled(this.server != null);
			importAlg.setEnabled(this.server != null && !this.connectInfo.pullMode);
		}
	}


	@Override
	protected PluginStorageManifest createPluginStorageManifest(PluginChangedListener listener,
			ConnectInfo connectInfo) {
		if (listener == null)
			return super.createPluginStorageManifest(listener, connectInfo);
		else if (listener instanceof PowerServer) {
			if (connectInfo.bindUri == null)
				return super.createPluginStorageManifest(listener, connectInfo);
			else {
				int port = 0;
				try {
					if (connectInfo != null && connectInfo.bindUri != null)
						port = connectInfo.bindUri.getPort();
					else
						port = listener.getPort();
				} catch (Exception e) {port = 0; LogUtil.trace(e);}
				
				return new PluginStorageManifestRemote((PowerServer)listener, connectInfo, port);
			}
		}
		else
			return super.createPluginStorageManifest(listener, connectInfo);
	}


	@Override
	public byte[] getByteCode(String className) throws RemoteException {
		return ClassProcessor.getByteCode0(className);
	}


	@Override
	protected void import0() {
		if (connectInfo.bindUri == null) {
			super.import0();
			return;
		}
		
		if (connectInfo.pullMode) {
			JOptionPane.showMessageDialog(
				UIUtil.getFrameForComponent(this), 
				"Unable to import due to pull mode", 
				"Unable to import", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (tblRegister.isModified()) {
			int confirm = JOptionPane.showConfirmDialog(
				this, 
				"System properties are modified. Do you want to apply them?", 
				"System properties are modified", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				apply();
		}
		
		boolean idle = tblRegister.isListenersIdle();
		if (!idle) {
			JOptionPane.showMessageDialog(
				UIUtil.getFrameForComponent(this), 
				"Unable to import due to busy listeners", 
				"Unable to import", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!Util.getPluginManager().isFired()) Util.getPluginManager().fireSimply();

		ImportAlgDlgRemote importAlgDlg = new ImportAlgDlgRemote(tblRegister);
		importAlgDlg.setVisible(true);
		
		if (importAlgDlg.getImportedCount() == 0) {
			JOptionPane.showMessageDialog(this, 
				"No algorithm imported", 
				"No algorithm imported", 
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			UnicastRemoteObject.exportObject(this, connectInfo.bindUri.getPort());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			try {
	        	UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Throwable e1) {}
		}

		PluginAlgDesc2ListMap pluginDescMap = importAlgDlg.getPluginAlgDescListMap();
		boolean applied = true;
		try {
			applied = server.applyPlugin(pluginDescMap,
					connectInfo.account.getName(), connectInfo.account.getPassword(), this);
			if (applied) tblRegister.update();
		}
		catch (Exception e) {
			applied = false;
			LogUtil.trace(e);
		}

    	try {
   			UnicastRemoteObject.unexportObject(this, true);
    	}
    	catch (Throwable e) {LogUtil.trace(e);}

    	if (applied) {
			JOptionPane.showMessageDialog(this, 
				"Sending " + importAlgDlg.getImportedCount() + " imported algorithms to server.\n", 
				"Successful importing", 
				JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(this, 
					"Impossible to send imported algorithms to server", 
					"No algorithm sent to server", 
					JOptionPane.ERROR_MESSAGE);
		}
		
	}


}



/**
 * This is GUI allowing users to import algorithms remotely to server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
class ImportAlgDlgRemote extends ImportAlgDlg {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Map of plug-in algorithm descriptions.
	 */
	protected PluginAlgDesc2ListMap pluginDescMap = new PluginAlgDesc2ListMap();

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public ImportAlgDlgRemote(Component comp) {
		super(comp);
	}

	
	@Override
	protected void browse() {
		List<Alg> parserList = Util.newList();
		SnapshotParserImpl snapshotParser = new SnapshotParserImpl(); 
		parserList.add(snapshotParser);
		
		DataDriverList dataDriverList = new DataDriverList();
		dataDriverList.add(new DataDriver(DataType.file));
		
		DataConfig defaultConfig = (DataConfig)txtBrowse.getTag();
		if (defaultConfig == null) {
			defaultConfig = new DataConfig();
			defaultConfig.setParser(snapshotParser);
		}
		DatasetConfigurator configurator = new DatasetConfigurator(this, parserList, dataDriverList, defaultConfig);
		DataConfig config = configurator.getResultedConfig();
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtBrowse.setText(config.getStoreUri().toString(), config);
	}


	@Override
	protected void loadClassesFromStore(xURI storeUri, List<Alg> outAlgList) {
		List<Alg> algList = Util.getPluginManager().loadInstances(Alg.class, storeUri);
		outAlgList.addAll(algList);
	}


	@Override
	protected void ok() {
		importedCount = 0;
		
		List<Alg> selectedList = tblAlgDescImport.getSelectedAlgList();
		pluginDescMap.clear();
		for (Alg alg : selectedList) {
			AlgDesc2 algDesc = new AlgDesc2(alg);
			algDesc.registered = true;
			algDesc.inNextUpdateList = false;
			algDesc.isExported = false;
			algDesc.removed = false;
			
			AlgDesc2List algDescs = pluginDescMap.get(algDesc.tableName);
			if (algDescs == null) {
				algDescs = new AlgDesc2List();
				pluginDescMap.put(algDesc.tableName, algDescs);
			}
			algDescs.add(algDesc);

			importedCount++;
		}
		
		ok = true;
		dispose();
	}


	/**
	 * Getting map of plug-in algorithm descriptions.
	 * @return map of plug-in algorithm descriptions.
	 */
	public PluginAlgDesc2ListMap getPluginAlgDescListMap() {
		return pluginDescMap;
	}
	
	
}