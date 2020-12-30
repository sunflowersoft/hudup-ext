/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import net.hudup.core.data.PropList;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * Dialog allows users to configure system.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class SysConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Panel of system configuration.
	 */
	protected SysConfigPane paneSysConfig; //Do not setting null to this variable.

	
	/**
	 * Constructor with variables.
	 * @param comp parent component.
	 * @param title title.
	 * @param vars specified variables.
	 */
	public SysConfigDlg(Component comp, String title, Object...vars) {
		super(UIUtil.getFrameForComponent(comp), title, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		init(vars);
	}

	
	/**
	 * Initialize this configuration dialog.
	 * @param vars additional parameters
	 */
	protected void init(Object...vars) {
		paneSysConfig = new SysConfigPane() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void close() {
				dispose();
			}
		}; 
	
		add(paneSysConfig);
	}
	
	
	/**
	 * Update properties list.
	 * @param propList specified properties list. 
	 */
	public void update(PropList propList) {
		
		paneSysConfig.update(propList);
	}
	
	
//	/**
//	 * Getting system configuration panel.
//	 * @return system configuration panel.
//	 */
//	public SysConfigPane getSysConfigPane() {
//		return paneSysConfig;
//	}
	
	
}
