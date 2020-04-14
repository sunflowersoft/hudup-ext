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
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * The graphic user interface (GUI) component as list box shows the {@link UnitList}.
 * It is an extended version of {@link UnitListBox}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitListBoxExt extends UnitListBox {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public UnitListBoxExt() {
		super();
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
			
		});
	}

	
	/**
	 * Create context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		if (config == null)
			return null;
		
		JPopupMenu ctxMenu = new JPopupMenu();
		Unit selected = getSelectedValue();
		if (selected == null)
			return null;
		
		JMenuItem miClearData = UIUtil.makeMenuItem((String)null, "Clear data", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					clearData();
				}
			});
		ctxMenu.add(miClearData);
		
		ctxMenu.addSeparator();
		
		JMenuItem miModify = UIUtil.makeMenuItem((String)null, "Modify structure", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					modify();
				}
			});
		ctxMenu.add(miModify);

		ctxMenu.addSeparator();
		
		JMenuItem miDrop = UIUtil.makeMenuItem((String)null, "Drop", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					drop();
				}
			});
		ctxMenu.add(miDrop);
		
		return ctxMenu;
	}
	
	
	/**
	 * Clearing data (units).
	 */
	public void clearData() {
		Unit selected = getSelectedValue();
		if (selected == null)
			return;
		
		Provider provider = new ProviderImpl(config);
		
		provider.deleteUnitData(selected.getName());
		provider.close();
	}
	
	
	/**
	 * Modifying data (units).
	 */
	protected void modify() {
		JOptionPane.showMessageDialog(
			null, 
			"Not implement yet", 
			"Not implement yet", 
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Dropping selected unit.
	 */
	protected void drop() {
		Unit selected = getSelectedValue();
		if (selected == null)
			return;
		
		Provider provider = new ProviderImpl(config);
		
		provider.dropUnit(selected.getName());
		provider.close();
		
		connectUpdate(config);
	}
	

}
