/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import javax.swing.table.DefaultTableModel;

import net.hudup.core.logistic.LogUtil;

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
	 * Editable flag.
	 */
	protected boolean editable = true;
	
	
	/**
	 * Default constructor.
	 */
	public SortableSelectableTableModel() {
		
	}


	/*
	 * The call of DefaultTableModel#getValueAt(int, int) can cause out of bound error from DefaultTableColumnModel#getColumn(int).
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		try {
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
		catch (Throwable e) {
			LogUtil.trace(e);
			return Object.class;
		}
	}


	/**
	 * Checking whether this table is editable.
	 * @return whether this table is editable.
	 */
	public boolean isEditable() {
		return editable;
	}
	
	
	/**
	 * Setting whether this table is editable.
	 * @param editable editable flag.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	
}
