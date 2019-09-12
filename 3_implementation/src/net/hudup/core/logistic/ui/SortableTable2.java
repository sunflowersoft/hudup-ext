package net.hudup.core.logistic.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This Java table that supports sorting.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
public class SortableTable2 extends JTable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SortableTable2() {
		init();
	}

	
	/**
	 * Constructor with table model.
	 * @param dm table model.
	 */
	public SortableTable2(TableModel dm) {
		super(dm);
		init();
	}

	
	/**
	 * Constructor with table model and column model.
	 * @param dm table model.
	 * @param cm column model.
	 */
	public SortableTable2(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		init();
	}

	
	/**
	 * Constructor with row number and column number.
	 * @param numRows row number
	 * @param numColumns column number.
	 */
	public SortableTable2(int numRows, int numColumns) {
		super(numRows, numColumns);
		init();
	}

	
	/**
	 * Constructor with row data vector and column names vector.
	 * @param rowData row data vector.
	 * @param columnNames column names vector.
	 */
	public SortableTable2(@SuppressWarnings("rawtypes") Vector rowData, @SuppressWarnings("rawtypes") Vector columnNames) {
		super(rowData, columnNames);
		init();
	}

	
	/**
	 * Constructor with row data array and column names array.
	 * @param rowData row data array.
	 * @param columnNames column names array.
	 */
	public SortableTable2(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		init();
	}

	
	/**
	 * Constructor with table model, column model, and selection model.
	 * @param dm table model.
	 * @param cm column model.
	 * @param sm selection model.
	 */
	public SortableTable2(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		init();
	}

	
    /**
     * Initialize the table.
     */
    protected void init() {
    	setAutoCreateRowSorter(true);
    }
	
    
}
