/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.client.ExtraService;
import net.hudup.core.logistic.LogUtil;

/**
 * This class shows remotely panel of system properties.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class SystemPropertiesPaneRemote extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal service.
	 */
	protected ExtraService service;
	
	
	/**
	 * System properties text area.
	 */
	protected SystemPropertiesTextAreaRemote txtSystemProperties;

	
	/**
	 * Refreshing button.
	 */
	protected JButton btnRefresh;

	
	/**
	 * Doing server tasks button.
	 */
	protected JButton btnDoServerTasks;

	
	/**
	 * Constructor with specified service.
	 * @param service specified service.
	 */
	public SystemPropertiesPaneRemote(ExtraService service) {
		super(new BorderLayout());
		this.service = service;
		
		this.txtSystemProperties = new SystemPropertiesTextAreaRemote(service);
		add(new JScrollPane(this.txtSystemProperties), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		this.btnRefresh = new JButton("Refresh");
		this.btnRefresh.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					txtSystemProperties.refresh();
				}
				
			});
		footer.add(this.btnRefresh);

		this.btnDoServerTasks = new JButton("Do server task");
		this.btnDoServerTasks.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doServerTasks();
				}
				
			});
		footer.add(this.btnDoServerTasks);
	}

	
	/**
	 * Performing server tasks.
	 */
	protected void doServerTasks() {
		try {
			service.doServerTasks();
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
	/**
	 * Showing system configuration dialog.
	 * @param service specified service.
	 * @param comp parent component.
	 */
	public static void showSysConfigDlg(ExtraService service, Component comp) {
		JDialog dlgSysConfig = new JDialog(UIUtil.getDialogForComponent(comp), "System properties dialog", true);
		dlgSysConfig.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgSysConfig.setSize(600, 400);
		dlgSysConfig.setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		
		dlgSysConfig.setLayout(new BorderLayout());
		SystemPropertiesPaneRemote paneSysConfig = new SystemPropertiesPaneRemote(service);
		dlgSysConfig.add(paneSysConfig, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlgSysConfig.add(footer, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dlgSysConfig.dispose();;
			}
			
		});
		footer.add(btnClose);
		
		dlgSysConfig.setVisible(true);
	}
	
	
}
