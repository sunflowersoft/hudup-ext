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

/**
 * This Java table model that supports sorting.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
public class SortableTableModel2 extends DefaultTableModel {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SortableTableModel2() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with row count and column count.
	 * @param rowCount row count.
	 * @param columnCount column count.
	 */
	public SortableTableModel2(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	
	/**
	 * Constructor with column names and row count.
	 * @param columnNames column names.
	 * @param rowCount row count.
	 */
	public SortableTableModel2(@SuppressWarnings("rawtypes") Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	
	/**
	 * Constructor with column names and row count.
	 * @param columnNames column names.
	 * @param rowCount row count.
	 */
	public SortableTableModel2(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	
	/**
	 * Constructor with data and column names.
	 * @param data data vector.
	 * @param columnNames column names.
	 */
	public SortableTableModel2(@SuppressWarnings("rawtypes") Vector data, @SuppressWarnings("rawtypes") Vector columnNames) {
		super(data, columnNames);
	}

	
	/**
	 * Constructor with data and column names.
	 * @param data data array.
	 * @param columnNames column names.
	 */
	public SortableTableModel2(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	
}
