/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * This class represents a movement tool bar.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MovingToolbar extends JToolBar {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Moving first button.
	 */
	protected JButton first = null;
	
	
	/**
	 * Moving previous button.
	 */
	protected JButton previous = null;

	
	/**
	 * Moving next button.
	 */
	protected JButton next = null;

	
	/**
	 * Moving last button.
	 */
	protected JButton last = null;

	
	/**
	 * Information label.
	 */
	protected JLabel info = null;
	
	
	/**
	 * Default constructor.
	 */
	public MovingToolbar() {
		first = UIUtil.makeIconButton(
			"movefirst-16x16.png",
			"movefirst", 
			"Move first - http://www.iconarchive.com", 
			"Move first", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					moveFirst();
				}
			});
		add(first);

		previous = UIUtil.makeIconButton(
			"moveprevious-16x16.png",
			"moveprevious", 
			"Move previous - http://www.iconarchive.com", 
			"Move previous", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					movePrevious();
				}
			});
		add(previous);
		
		next = UIUtil.makeIconButton(
			"movenext-16x16.png",
			"movenext", 
			"Move next - http://www.iconarchive.com", 
			"Move next", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					moveNext();
				}
			});
		add(next);
		
		last = UIUtil.makeIconButton(
			"movelast-16x16.png",
			"movelast", 
			"Move last - http://www.iconarchive.com", 
			"Move last", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					moveLast();
				}
			});
		add(last);
		
		info = new JLabel();
		add(info);
	}


	/**
	 * Updating tool bar.
	 */
	public void update() {
		
	}
	
	/**
	 * Move first.
	 */
	public void moveFirst() {
		
	}
	
	
	/**
	 * Move previous.
	 */
	public void movePrevious() {
		
	}


	/**
	 * Move next.
	 */
	public void moveNext() {
		
	}


	/**
	 * Move last.
	 */
	public void moveLast() {
		
	}

	
	/**
	 * Enabling / disabling buttons.
	 * @param enabled flag to unable / disable buttons.
	 */
	public void enableButtons(boolean enabled) {
		first.setEnabled(enabled);
		previous.setEnabled(enabled);
		next.setEnabled(enabled);
		last.setEnabled(enabled);
	}

	
	/**
	 * Showing / hiding buttons.
	 * @param shown flag to show / hide buttons.
	 */
	public void showButtons(boolean shown) {
		first.setVisible(shown);
		previous.setVisible(shown);
		next.setVisible(shown);
		last.setVisible(shown);
	}

	
}
