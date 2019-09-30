/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * This Java table that supports sorting.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class SortableTable extends JTable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SortableTable() {
		super(new SortableTableModel());
		init();
	}

	
	/**
	 * Constructor with table model.
	 * @param dm table model.
	 */
	public SortableTable(SortableTableModel dm) {
		super(dm);
		init();
	}

	
	/**
	 * Constructor with table model and column model.
	 * @param dm table model.
	 * @param cm column model.
	 */
	public SortableTable(SortableTableModel dm, TableColumnModel cm) {
		super(dm, cm);
		init();
	}

	
	/**
	 * Constructor with row number and column number.
	 * @param numRows row number
	 * @param numColumns column number.
	 */
	public SortableTable(int numRows, int numColumns) {
		super(new SortableTableModel(numRows, numColumns));
		init();
	}

	
	/**
	 * Constructor with row data vector and column names vector.
	 * @param rowData row data vector.
	 * @param columnNames column names vector.
	 */
	public SortableTable(@SuppressWarnings("rawtypes") Vector rowData, @SuppressWarnings("rawtypes") Vector columnNames) {
		super(new SortableTableModel(rowData, columnNames));
		init();
	}

	
	/**
	 * Constructor with row data array and column names array.
	 * @param rowData row data array.
	 * @param columnNames column names array.
	 */
	public SortableTable(Object[][] rowData, Object[] columnNames) {
		super(new SortableTableModel(rowData, columnNames));
		init();
	}

	
    /**
     * Initialize the table.
     */
    protected void init() {
       	setAutoCreateRowSorter(true);
    }
	
    
}
