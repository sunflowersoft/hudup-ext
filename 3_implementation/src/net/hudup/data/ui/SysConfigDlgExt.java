/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageManifest.PluginStorageManifestPanel;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ui.DataDriverListTable;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.logistic.SystemPropertiesPane;

/**
 * This class is extended dialog allowing users to modifying system configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SysConfigDlgExt extends SysConfigDlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Body tab panel.
	 */
	protected JTabbedPane body; //Do not setting null to this variable.

	
	/**
	 * Registering panel.
	 */
	protected PluginStorageManifestPanel paneRegister; //Do not setting null to this variable.

	
	/**
	 * Data driver list panel.
	 */
	protected JPanel paneDataDriverList; //Do not setting null to this variable.

	
	/**
	 * System properties panel.
	 */
	protected JPanel paneSystemProperties; //Do not setting null to this variable.

	
	/**
	 * Constructor with variables.
	 * @param comp parent component.
	 * @param title title.
	 * @param vars specified variables.
	 */
	public SysConfigDlgExt(Component comp, String title, Object...vars) {
		super(comp, title, vars);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				
				if (!isModified())
					return;
				
				int confirm = JOptionPane.showConfirmDialog(
						comp, 
						"System properties are modified. Do you want to apply them?", 
						"System properties are modified", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					onApply();
			}

		});
	}

	
	@Override
	protected void init(Object...vars) {
		setLayout(new BorderLayout());
		
		body = new JTabbedPane();
		add(body, BorderLayout.CENTER);
		
		paneSysConfig = new SysConfigPane(); 
		paneSysConfig.hideOkCancel();
		body.add("Configuration", paneSysConfig);
		
		if (vars.length == 0)
			paneRegister = new PluginStorageManifestPanel(null);
		else if (vars[0] instanceof PluginChangedListener)
			paneRegister = new PluginStorageManifestPanel((PluginChangedListener)vars[0]);
		body.add("Plugins", paneRegister);
		
		paneDataDriverList = DataDriverListTable.createPane(DataDriverList.get());
		body.add("Data drivers", paneDataDriverList);
		
		paneSystemProperties = new SystemPropertiesPane();
		body.add("System properties", paneSystemProperties);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
		footer.add(btnClose);
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		
	}

	
	/**
	 * Removing system configuration panel.
	 */
	public void removeSysConfigPane() {
		if (body != null && paneSysConfig != null) {
			body.remove(paneSysConfig);
			paneSysConfig = null;
		}
	}
	
	
	/**
	 * Removing plug-in storage manifest.
	 */
	public void removePluginStorageManifest() {
		if (body != null && paneRegister != null) {
			body.remove(paneRegister);
			paneRegister = null;
		}
	}
	
	
	/**
	 * This event-driven method applies changes on system properties.
	 */
	protected void onApply() {
		if (!isModified())
			return;
		
		if (paneSysConfig != null && paneSysConfig.isModified())
			paneSysConfig.apply();
		
		if (paneRegister != null && paneRegister.isModified())
			paneRegister.apply();
	}
	
	
	/**
	 * Testing whether system properties are modified.
	 * @return whether system properties are modified.
	 */
	protected boolean isModified() {
		return (paneSysConfig != null && paneSysConfig.isModified())
				|| (paneRegister != null && paneRegister.isModified());
	}
	
	
	/**
	 * Getting plug-in storage manifest.
	 * @return plug-in storage manifest.
	 */
	public PluginStorageManifestPanel getPluginStorageManifest() {
		return paneRegister;
	}
	
	
}
