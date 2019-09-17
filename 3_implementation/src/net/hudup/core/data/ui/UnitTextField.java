/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.data.Unit;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ui.TagTextField;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * Text field to store a unit.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitTextField extends TagTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public UnitTextField() {
		super();
		// TODO Auto-generated constructor stub
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e) && getUnit() != null) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
			
		});
	}

	
	/**
	 * Getting this text field.
	 * @return this text field.
	 */
	private UnitTextField getThis() {
		return this;
		
	}

	
	/**
	 * Creating context menu.
	 * @return {@link JPopupMenu} as context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miCopyURI2 = UIUtil.makeMenuItem((String)null, "Copy", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					ClipboardUtil.util.setText(getThis().getText());
				}
			});
		contextMenu.add(miCopyURI2);
		
		return contextMenu;
	}

	
	/**
	 * Setting unit.
	 * @param unit specified unit.
	 */
	public void setUnit(Unit unit) {
		
		tag = unit;
		if (unit == null)
			setText("");
		else
			setText(unit.toString());
	}
	
	
	/**
	 * Getting internal unit.
	 * @return the internal unit.
	 */
	public Unit getUnit() {
		return (Unit)tag;
	}


}
