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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageManifest;
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
	 * Registering panel.
	 */
	protected JPanel paneRegister = null;

	
	/**
	 * Data driver list panel.
	 */
	protected JPanel paneDataDriverList = null;

	
	/**
	 * System properties panel.
	 */
	protected JPanel paneSystemProperties = null;

	
	/**
	 * Constructor with variables.
	 * @param comp parent component.
	 * @param title title.
	 * @param vars specified variables.
	 */
	public SysConfigDlgExt(Component comp, String title, Object...vars) {
		super(comp, title, vars);
		
	}

	
	@Override
	protected void init(Object...vars) {
		setLayout(new BorderLayout());
		
		JTabbedPane body = new JTabbedPane();
		add(body, BorderLayout.CENTER);
		
		paneSysConfig = new SysConfigPane(); 
		paneSysConfig.hideOkCancel();
		body.add("Configuration", paneSysConfig);
		
		if (vars.length == 0)
			paneRegister = PluginStorageManifest.createPane(null);
		else if (vars[0] instanceof PluginChangedListener)
			paneRegister = PluginStorageManifest.createPane((PluginChangedListener)vars[0]);
		body.add("Plugins", paneRegister);
		
		paneDataDriverList = DataDriverListTable.createPane(DataDriverList.list());
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

	
}
