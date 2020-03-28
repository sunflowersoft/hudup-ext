/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import net.hudup.core.logistic.LogUtil;

/**
 * This Java table model that supports sorting.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class SortableTableModel extends DefaultTableModel {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SortableTableModel() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with row count and column count.
	 * @param rowCount row count.
	 * @param columnCount column count.
	 */
	public SortableTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	
	/**
	 * Constructor with column names and row count.
	 * @param columnNames column names.
	 * @param rowCount row count.
	 */
	public SortableTableModel(@SuppressWarnings("rawtypes") Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	
	/**
	 * Constructor with column names and row count.
	 * @param columnNames column names.
	 * @param rowCount row count.
	 */
	public SortableTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	
	/**
	 * Constructor with data and column names.
	 * @param data data vector.
	 * @param columnNames column names.
	 */
	public SortableTableModel(@SuppressWarnings("rawtypes") Vector data, @SuppressWarnings("rawtypes") Vector columnNames) {
		super(data, columnNames);
	}

	
	/**
	 * Constructor with data and column names.
	 * @param data data array.
	 * @param columnNames column names.
	 */
	public SortableTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
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

	
}
