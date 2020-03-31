/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.io.Serializable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.I18nUtil;

/**
 * This class shows a waiting dialog.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class WaitDialog implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Parent component of waiting dialog.
	 */
	protected Component comp = null;
	
	
	/**
	 * Waiting dialog.
	 */
	protected JDialog waitDlg = null;
	
	
	/**
	 * Default constructor.
	 * @param comp parent component.
	 */
	public WaitDialog(Component comp) {
		this.comp = comp;
	}

	
	/**
	 * Starting method.
	 * @return this waiting dialog.
	 */
	public synchronized WaitDialog start() {
		if (waitDlg != null) return this;
		
		AbstractRunner runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				waitDlg = createDialog(comp);
				waitDlg.setVisible(true);
				waitDlg = null;
				thread = null;
			}
			
			@Override
			protected void clear() {
				
			}
			
		};
		
		runner.start();
		
		return this;
	}
	
	
	/**
	 * Stopping method.
	 * @return this waiting dialog.
	 */
	public synchronized WaitDialog stop() {
		if (waitDlg != null)
			waitDlg.dispose(); 
		waitDlg = null;
		
		return this;
	}
	
	
	/**
	 * Creating dialog for waiting.
	 * @param comp parent component.
	 * @return dialog for waiting.
	 */
	public static JDialog createDialog(Component comp) {
		JDialog waitDlg = new JDialog(UIUtil.getFrameForComponent(comp), "Please wait...", true);
		waitDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		waitDlg.setLocationRelativeTo(null);
		waitDlg.setSize(200, 100);
		waitDlg.setLayout(new BorderLayout());
		waitDlg.add(new JLabel(I18nUtil.message("please_wait") + "..."), BorderLayout.NORTH);
		
		waitDlg.add(new JLabel(UIUtil.getImageIcon("wait-64x64.gif", I18nUtil.message("please_wait"))), BorderLayout.CENTER);

		waitDlg.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		return waitDlg;
	}
	
	
}
