/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.Serializable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.hudup.core.logistic.AbstractRunner;

/**
 * This class shows a waiting dialog.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
public class WaitDialog implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Waiting dialog.
	 */
	protected JDialog waitDlg = null;
	
	
	/**
	 * Default constructor.
	 */
	public WaitDialog() {

	}

	
	/**
	 * Starting method.
	 */
	public synchronized void start() {
		if (waitDlg != null) return;
		
		AbstractRunner runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				waitDlg = createDialog();
				waitDlg.setVisible(true);
				waitDlg = null;
				thread = null;
			}
			
			@Override
			protected void clear() {
				
			}
			
		};
		
		runner.start();
	}
	
	
	/**
	 * Stopping method.
	 */
	public synchronized void stop() {
		if (waitDlg != null)
			waitDlg.dispose(); 
		waitDlg = null;
	}
	
	
	/**
	 * Creating dialog for waiting.
	 * @return dialog for waiting.
	 */
	protected static JDialog createDialog() {
		JDialog waitDlg = new JDialog((Frame)null, "Please wait...", true);
		waitDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		waitDlg.setLocationRelativeTo(null);
		waitDlg.setSize(300, 200);
		waitDlg.setLayout(new BorderLayout());
		waitDlg.add(new JLabel("Please wait..."), BorderLayout.CENTER);
		
		return waitDlg;
	}
	
	
}
