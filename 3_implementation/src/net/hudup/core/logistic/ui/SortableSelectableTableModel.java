/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import javax.swing.table.DefaultTableModel;

/**
 * This is Java table model that supports both sortable ability and selectable ability.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class SortableSelectableTableModel extends DefaultTableModel {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public SortableSelectableTableModel() {
		
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (getRowCount() == 0)
			return super.getColumnClass(columnIndex);
		else {
			Object value = getValueAt(0, columnIndex);
			if (value == null)
				return super.getColumnClass(columnIndex);
			else
				return value.getClass();
		}
	}


}
