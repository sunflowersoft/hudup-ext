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
	 * Default constructor.
	 */
	public SystemPropertiesPane() {
		super(new BorderLayout());
		
		final SystemPropertiesTextArea txtSystemProperties = new SystemPropertiesTextArea();
		add(new JScrollPane(txtSystemProperties), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					txtSystemProperties.refresh();
				}
			});
		footer.add(btnRefresh);
		
		JButton btnEnhancePerformance = new JButton("Enhance performance");
		btnEnhancePerformance.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					SystemUtil.enhance();
					txtSystemProperties.refresh();
					
					JOptionPane.showMessageDialog(
							UIUtil.getDialogForComponent(getThis()), 
							"Enhance performance successfully", 
							"Enhance performance", 
							JOptionPane.INFORMATION_MESSAGE);

				}
			});
		footer.add(btnEnhancePerformance);
		
	}
	
	
	/**
	 * Getting this system pane.
	 * @return this system pane.
	 */
	private SystemPropertiesPane getThis() {
		return this;
	}
	
	
	
}
