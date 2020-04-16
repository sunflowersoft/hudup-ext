/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This is Java table that supports both sortable ability and selectable ability.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class SortableSelectableTable extends JTable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of selecting listener.
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Default constructor.
	 */
	public SortableSelectableTable() {
		// TODO Auto-generated constructor stub
		super(new SortableSelectableTableModel());
		init();
	}


    /**
	 * Default constructor with specified model.
	 * @param model model of this table.
	 */
	public SortableSelectableTable(SortableSelectableTableModel model) {
		// TODO Auto-generated constructor stub
		super(model);
		init();
	}

	
	/**
	 * Getting the model of this table.
	 * @return the model of this table.
	 */
	public SortableSelectableTableModel getSortableSelectableTableModel() {
		return (SortableSelectableTableModel)getModel();
	}
	
	
	/**
	 * Initialize table.
	 */
	protected void init() {
		RowSorter<? extends TableModel> rowSorter = new TableRowSorter<SortableSelectableTableModel>(getSortableSelectableTableModel());
		setRowSorter(rowSorter);
		
    	TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
		    TableColumn tc = getColumnModel().getColumn(i);
		    if (!getColumnClass(i).equals(Boolean.class))
		    	continue;
		    
	    	tc.setHeaderRenderer(new SelectableHeaderRenderer(new HeaderItemListener(this)));
	    	((TableRowSorter<?>)rowSorter).setSortable(i, false);
		}
	}
	
	
	/**
	 * Checking whether this table is editable.
	 * @return whether this table is editable.
	 */
	public boolean isEditable() {
		return getSortableSelectableTableModel().isEditable();
	}

	
	/**
	 * Setting whether this table is editable.
	 * @param editable editable flag.
	 */
	public void setEditable(boolean editable) {
		getSortableSelectableTableModel().setEditable(editable);
		
    	TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
		    TableColumn tc = getColumnModel().getColumn(i);
		    TableCellRenderer hr = tc.getHeaderRenderer();
		    if ((hr != null) && (hr instanceof SelectableHeaderRenderer))
		    	((SelectableHeaderRenderer)hr).setEnabled(editable);
		}
	}



	/**
	 * This interface represents listener for selecting event.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	public static interface SelectListener extends EventListener {

		/**
		 * Responding to selecting event.
		 * @param evt selecting event.
		 */
		void select(SelectEvent evt);

	}


	/**
	 * This is selecting event.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	public static class SelectEvent extends EventObject {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Clicked column index.
		 */
		private int column = 0;
		
		/**
		 * Clicked column name.
		 */
		private String name = null;

		/**
		 * Flag to indicate whether column is selected.
		 */
		private boolean isSelected = false;
		
		/**
		 * Constructor with column index, column name, selected flag.
		 * @param table selectable table
		 * @param column column index
		 * @param name column name
		 * @param isSelected selected flag
		 */
		public SelectEvent(
				SortableSelectableTable table, 
				int column,
				String name,
				boolean isSelected) {
			super(table);
			this.column = column;
			this.name = name;
			this.isSelected = isSelected;
		}

		/**
		 * Getting sortable and selectable table.
		 * @return sortable and selectable table.
		 */
		public SortableSelectableTable getSortableSelectableTable() {
			return (SortableSelectableTable)getSource();
		}

		/**
		 * Testing whether column is selected.
		 * @return whether column is selected.
		 */
		public boolean isSelected() {
			return isSelected;
		}
		
		/**
		 * Getting clicked column index.
		 * @return clicked column index.
		 */
		public int getColumn() {
			return column;
		}
		
		/**
		 * Getting clicked column name.
		 * @return clicked column name.
		 */
		public String getName() {
			return name;
		}
		
	}


	/**
	 * This class is listener of item header.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	private class HeaderItemListener implements ItemListener {
		
		/**
		 * Table that supports both sortable ability and selectable ability.
		 */
		protected SortableSelectableTable table = null;
		
		/**
		 * Constructor with table that supports both sortable ability and selectable ability.
		 * @param table table that supports both sortable ability and selectable ability.
		 */
		public HeaderItemListener(SortableSelectableTable table) {
			this.table = table;
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {   
			Object source = e.getSource();   
			if (!(source instanceof SelectableHeaderRenderer))
				return;   
	      
			SelectableHeaderRenderer header = (SelectableHeaderRenderer)source;
			int n = getRowCount();
			int column = header.getColumn();
			boolean selected = header.isSelected();
			for (int i = 0; i < n; i++) {
				setValueAt(selected, i, column);
			}
			
			fireSelectEvent(
				new SelectEvent(
					table, 
					column,
					header.getText(),
					selected));
		}  
		
	}   

	
	/**
	 * Adding selecting listener.
	 * @param listener selecting listener.
	 */
	public void addSelectListener(SelectListener listener) {
        listenerList.add(SelectListener.class, listener);
    }

    
	/**
	 * Removing selecting listener.
	 * @param listener selecting listener.
	 */
    public void removeSelectListener(SelectListener listener) {
        listenerList.remove(SelectListener.class, listener);
    }
	
    
    /**
     * Getting selecting listeners.
     * @return selecting listeners.
     */
    public SelectListener[] getSelectListeners() {
        return listenerList.getListeners(SelectListener.class);
    }


    /**
     * Firing selecting event to selecting listeners.
     * @param evt selecting listeners.
     */
	protected void fireSelectEvent(SelectEvent evt) {
		SelectListener[] listeners = getSelectListeners();
		for (SelectListener listener : listeners) {
			listener.select(evt);
		}
	}

	
	/*
	 * The call of DefaultTableModel#getValueAt(int, int) can cause out of bound error from DefaultTableColumnModel#getColumn(int).
	 * @see javax.swing.JTable#getCellRenderer(int, int)
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

	
	/**
	 * Getting (column) name list according to selected flag.
	 * @param selected selected flag.
	 * @return (column) name list according to selected flag.
	 */
	public List<String> getSelectedNameList(boolean selected) {
		List<String> selectedList = Util.newList();
		
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
		    TableColumn tc = getColumnModel().getColumn(i);
		    if (!(tc.getHeaderRenderer() instanceof SelectableHeaderRenderer))
		    	continue;
		    
		    SelectableHeaderRenderer header = (SelectableHeaderRenderer)tc.getHeaderRenderer();
		    if (header.isSelected() == selected)
		    	selectedList.add(header.getText());
		}
		
		return selectedList;
	}


}



/**
 * This is selectable header renderer.
 * 
 * @author Loc Nguyen
 * @version 12,0
 *
 */
class SelectableHeaderRenderer extends JCheckBox implements TableCellRenderer, MouseListener {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Selectable header renderer.
	 */
	protected SelectableHeaderRenderer rendererComponent;


	/**
	 * Clicked column index.
	 */
	protected int column = 0;


	/**
	 * Flag to indicate whether mouse is pressed.
	 */
	protected boolean mousePressed = false;


	/**
	 * Constructor with item listener.
	 * @param itemListener item listener.
	 */
	public SelectableHeaderRenderer(ItemListener itemListener) { 
		super();
		rendererComponent = this;
		rendererComponent.addItemListener(itemListener);   
	}


	@Override
	public Component getTableCellRendererComponent(   
		JTable table, Object value,   
		boolean isSelected, boolean hasFocus, int row, int column) {
	
		if (table != null) {   
			JTableHeader header = table.getTableHeader();   
			if (header != null) {   
				rendererComponent.setForeground(header.getForeground());   
				rendererComponent.setBackground(header.getBackground());   
				rendererComponent.setFont(header.getFont());   
				header.addMouseListener(rendererComponent);   
			}   
		}   
	
		setColumn(column);
	
		rendererComponent.setText(value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));   
		return rendererComponent;   
	}   


	/**
	 * Setting selected column.
	 * @param column selected column.
	 */
	protected void setColumn(int column) {   
		this.column = column;   
	}   


	/**
	 * Getting selected column.
	 * @return selected column.
	 */
	public int getColumn() {   
		return column;   
	}   


	/**
	 * Handling mouse event.
	 * @param e mouse event.
	 */
	protected void handleClickEvent(MouseEvent e) {
		if (!mousePressed) return;
	
		mousePressed = false;   
		JTableHeader header = (JTableHeader)(e.getSource());   
		JTable tableView = header.getTable();  
	
		TableColumnModel columnModel = tableView.getColumnModel();   
		int viewColumn = columnModel.getColumnIndexAtX(e.getX());   
		int column = tableView.convertColumnIndexToModel(viewColumn);   

		if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {   
			doClick();
		}   
	}   


	@Override
	public void mouseClicked(MouseEvent e) {   
		handleClickEvent(e);   
		((JTableHeader)e.getSource()).repaint();   
	}   

	
	@Override
	public void mousePressed(MouseEvent e) {   
		mousePressed = true;   
	}   

	
	@Override
	public void mouseReleased(MouseEvent e) { }   

	
	@Override
	public void mouseEntered(MouseEvent e) { }


	@Override
	public void mouseExited(MouseEvent e) { }


}  

