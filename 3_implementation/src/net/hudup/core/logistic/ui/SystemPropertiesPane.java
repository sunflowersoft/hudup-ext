/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.logistic.SystemUtil;

/**
 * This class shows panel of system properties.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class SystemPropertiesPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * System properties text area.
	 */
	protected SystemPropertiesTextArea txtSystemProperties;
	
	
	/**
	 * Refreshing button.
	 */
	protected JButton btnRefresh;
	
	
	/**
	 * Enhancing button.
	 */
	protected JButton btnEnhancePerformance;
	
	
	/**
	 * Doing server tasks button.
	 */
	protected JButton btnDoServerTasks;

	
	/**
	 * Default constructor.
	 */
	public SystemPropertiesPane() {
		super(new BorderLayout());
		
		this.txtSystemProperties = new SystemPropertiesTextArea();
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
		
		this.btnEnhancePerformance = new JButton("Enhance performance");
		this.btnEnhancePerformance.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					enhancePerformance();
				}
				
			});
		footer.add(this.btnEnhancePerformance);
		
		this.btnDoServerTasks = new JButton("Do server task");
		this.btnDoServerTasks.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doServerTasks();
				}
				
			});
		footer.add(this.btnDoServerTasks);
		this.btnDoServerTasks.setVisible(false);
	}
	
	
	/**
	 * Enhancing performance.
	 */
	protected void enhancePerformance() {
		SystemUtil.enhance();
		txtSystemProperties.refresh();
		
		JOptionPane.showMessageDialog(this, "Enhance performance successfully", "Enhance performance", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Performing server tasks.
	 */
	protected void doServerTasks() {
		JOptionPane.showMessageDialog(this, "Server tasks performance not implemented yet", "Not implemented yet", JOptionPane.WARNING_MESSAGE);
	}
	
	
}
