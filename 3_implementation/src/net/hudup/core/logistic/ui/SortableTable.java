/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.hudup.core.logistic.LogUtil;

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
	public SortableTable(Vector<? extends Vector<?>> rowData, Vector<?> columnNames) {
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
	
    
    /*
	 * The call of DefaultTableModel#getValueAt(int, int) can cause out of bound error from DefaultTableColumnModel#getColumn(int).
     * @see javax.swing.JTable#getCellRenderer(int, int).
	 * If setting this method as default super.getCellRenderer(row, column), the method getColumnClass(int) in model must be defined exactly according to column classes.
     */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		try {
			Object value = getValueAt(row, column);
			if (value == null)
				return super.getCellRenderer(row, column);
			else {
				TableCellRenderer renderer = getDefaultRenderer(value.getClass());
				if(renderer == null)
					return super.getCellRenderer(row, column);
				else
					return renderer;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return getDefaultRenderer(Object.class);
		}
	}
	
	
	/*
	 * The call of DefaultTableModel#getValueAt(int, int) can cause out of bound error from DefaultTableColumnModel#getColumn(int).
	 * @see javax.swing.JTable#getCellEditor(int, int)
	 */
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		try {
			Object value = getValueAt(row, column);
			if (value == null)
				return super.getCellEditor(row, column);
			else {
				TableCellEditor editor = getDefaultEditor(value.getClass());
				if(editor == null)
					return super.getCellEditor(row, column);
				else
					return editor;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return getDefaultEditor(Object.class);
		}
    }


}
