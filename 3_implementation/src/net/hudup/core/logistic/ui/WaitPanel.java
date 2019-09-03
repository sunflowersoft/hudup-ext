package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class shows a waiting panel.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class WaitPanel extends JPanel implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Waiting text.
	 */
	public final static String WAIT_TEXT = "Please wait...";
	
	
	/**
	 * Waiting label.
	 */
	protected JLabel lblWait = null;
	
	
	/**
	 * Default constructor.
	 */
	public WaitPanel() {
		// TODO Auto-generated constructor stub
		setLayout(new BorderLayout());
		
		lblWait = new JLabel(WAIT_TEXT);
		add(lblWait, BorderLayout.CENTER);
	}


	/**
	 * Setting waiting text.
	 * @param waitText waiting text.
	 */
	public void setWaitText(String waitText) {
		lblWait.setText(waitText);
	}
	
	
}
