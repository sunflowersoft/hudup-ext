/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class is the status bar.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class StatusBar extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of labels.
	 */
	private JLabel[] paneList = new JLabel[5];
	
	
	/**
	 * Default constructor.
	 */
	public StatusBar() {
		super();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < paneList.length; i++) {
			if ( i > 0)
				add(new JLabel("  "));
			paneList[i] = new JLabel();
			add(paneList[i]);
		}
		
	}
	

	/**
	 * Setting text for panel 0.
	 * @param text text for panel 0.
	 */
	public void setTextPane0(String text) {
		paneList[0].setText(text);
	}
	
	
	/**
	 * Setting text for panel 1.
	 * @param text text for panel 1.
	 */
	public void setTextPane1(String text) {
		paneList[1].setText(text);
	}
	
	
	/**
	 * Setting text for panel 2.
	 * @param text text for panel 2.
	 */
	public void setTextPane2(String text) {
		paneList[2].setText(text);
	}

	
	/**
	 * Setting text for panel 3.
	 * @param text text for panel 3.
	 */
	public void setTextPane3(String text) {
		paneList[3].setText(text);
	}

	
	/**
	 * Setting text for panel 4.
	 * @param text text for panel 4.
	 */
	public void setTextPane4(String text) {
		paneList[4].setText(text);
	}

	
	/**
	 * Setting text for last panel.
	 * @param text text for last panel.
	 */
	public void setTextPaneLast(String text) {
		setTextPane4(text);
	}
	
	
	/**
	 * Clearing text.
	 */
	public void clearText() {
		for (JLabel textPane : paneList) {
			textPane.setText("");
		}
	}
	
	
	/**
	 * Getting pane 0.
	 * @return pane 0.
	 */
	public JLabel getPane0() {
		return paneList[0];
	}
	
	
	/**
	 * Getting pane 1.
	 * @return pane 1.
	 */
	public JLabel getPane1() {
		return paneList[1];
	}


	/**
	 * Getting pane 2.
	 * @return pane 2.
	 */
	public JLabel getPane2() {
		return paneList[2];
	}


	/**
	 * Getting pane 3.
	 * @return pane 3.
	 */
	public JLabel getPane3() {
		return paneList[3];
	}
	
	
	/**
	 * Getting pane 4.
	 * @return pane 4.
	 */
	public JLabel getPane4() {
		return paneList[4];
	}
	
	
	/**
	 * Getting last pane.
	 * @return last pane.
	 */
	public JLabel getLastPane() {
		return getPane4();
	}

	
	/**
	 * Getting texts from all text panes.
	 * @return texts from all text panes.
	 */
	public String[] getTexts() {
		String[] texts = new String[paneList.length];
		for (int i = 0; i < paneList.length; i++) {
			texts[i] = paneList[i].getText();
			texts[i] = texts[i] != null ? texts[i] : "";
		}
		
		return texts;
	}
	
	
	/**
	 * Setting texts to all text panes.
	 * @param texts texts set to all text panes.
	 */
	public void setTexts(String[] texts) {
		if (texts == null) return;
		
		int n = Math.min(texts.length, paneList.length);
		for (int i = 0; i < n; i++) {
			String text = texts[i];
			text = text != null ? text : "";
			paneList[i].setText(text);
		}
	}
	
	
}



/**
 * This class is the extended status bar.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
@Deprecated
class StatusBar2 extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of labels.
	 */
	private JTextField[] paneList = new JTextField[5];
	
	
	/**
	 * Default constructor.
	 */
	public StatusBar2() {
		super();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < paneList.length; i++) {
			if ( i > 0)
				add(new JLabel("  "));
			paneList[i] = new JTextField();
			paneList[i].setEditable(false);
			add(paneList[i]);
		}
		
	}
	

	/**
	 * Setting text for text pane.
	 * @param textPane text pane.
	 * @param text specified text.
	 */
	private static void setText(JTextField textPane, String text) {
		textPane.setText(text);
		textPane.setCaretPosition(0);
	}
	
	
	/**
	 * Setting text for panel 0.
	 * @param text text for panel 0.
	 */
	public void setTextPane0(String text) {
		setText(paneList[0], text);
	}
	
	
	/**
	 * Setting text for panel 1.
	 * @param text text for panel 1.
	 */
	public void setTextPane1(String text) {
		setText(paneList[1], text);
	}
	
	
	/**
	 * Setting text for panel 2.
	 * @param text text for panel 2.
	 */
	public void setTextPane2(String text) {
		setText(paneList[2], text);
	}

	
	/**
	 * Setting text for panel 3.
	 * @param text text for panel 3.
	 */
	public void setTextPane3(String text) {
		setText(paneList[3], text);
	}

	
	/**
	 * Setting text for panel 4.
	 * @param text text for panel 4.
	 */
	public void setTextPane4(String text) {
		setText(paneList[4], text);
	}

	
	/**
	 * Setting text for last panel.
	 * @param text text for last panel.
	 */
	public void setTextPaneLast(String text) {
		setTextPane4(text);
	}
	
	
	/**
	 * Clearing text.
	 */
	public void clearText() {
		for (JTextField textPane : paneList) {
			setText(textPane, "");
		}
	}
	
	
	/**
	 * Getting pane 0.
	 * @return pane 0.
	 */
	public JTextField getPane0() {
		return paneList[0];
	}
	
	
	/**
	 * Getting pane 1.
	 * @return pane 1.
	 */
	public JTextField getPane1() {
		return paneList[1];
	}


	/**
	 * Getting pane 2.
	 * @return pane 2.
	 */
	public JTextField getPane2() {
		return paneList[2];
	}


	/**
	 * Getting pane 3.
	 * @return pane 3.
	 */
	public JTextField getPane3() {
		return paneList[3];
	}
	
	
	/**
	 * Getting pane 4.
	 * @return pane 4.
	 */
	public JTextField getPane4() {
		return paneList[4];
	}
	
	
	/**
	 * Getting last pane.
	 * @return last pane.
	 */
	public JTextField getLastPane() {
		return getPane4();
	}

	
}
