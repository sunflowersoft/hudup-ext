/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;

/**
 * This class represents a template table.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TempTable extends SortableSelectableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Highlight cell renderer.
	 */
	protected HighlightCellRenderer highlightCellRenderer = new HighlightCellRenderer();

	
	/**
	 * Default constructor.
	 */
	public TempTable() {
		super(new TempTableModel());
		fixInit();
	}

	
	/**
	 * Constructor with model.
	 * @param model specified model.
	 */
	public TempTable(TempTableModel model) {
		super(model);
		fixInit();
	}


	/**
	 * Setting fixed configuration.
	 */
	protected void fixInit() {
//		this.setDefaultEditor(Type.class, new TypeCellEditor());
		
//		setAutoResizeMode(AUTO_RESIZE_OFF); //Allowing horizontal scroll bar when putting it in JScrollPane.
//		getTableHeader().setReorderingAllowed(false); //Preventing change the column order.
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick();
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onKeyPressed(e);
			}
		});

	}
	
	
	@Override
	protected void init() {
		super.init();
		
//		int lastColumn = getColumnCount() - 1;
//		if (lastColumn > 0) {
//			getColumnModel().getColumn(lastColumn).setMaxWidth(0);
//			getColumnModel().getColumn(lastColumn).setMinWidth(0);
//			getColumnModel().getColumn(lastColumn).setPreferredWidth(0);
//		}
	}

	
	/**
	 * Create context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		if (!isEditable()) return null;
		
		TempTable thisTable = this;
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miView = new JMenuItem("View");
		miView.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisTable, "View", "View", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		ctxMenu.add(miView);

		return ctxMenu;
	}
	
	
	/**
	 * Getting table model.
	 * @return table model.
	 */
	public TempTableModel getModel2() {
		return (TempTableModel)getModel();
	}
	

	/**
	 * Update table by some parameter.
	 * @param parameter some parameter.
	 */
	public void update(Object parameter) {
//		int selectedRow = getSelectedRow();

		getModel2().update(parameter);
		init();
		
//		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
	
	}
	

	
	/**
	 * Double click event method.
	 */
	protected void onDoubleClick() {
		if (!isEditable()) return;

		JOptionPane.showMessageDialog(this, "Double click", "Double click", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Key pressed event method.
	 * @param e key event.
	 */
	protected void onKeyPressed(KeyEvent e) {
		if (!isEditable()) return;

		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			JOptionPane.showMessageDialog(this, "ENTER key pressed", "ENTER key pressed", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(e.getKeyCode() == KeyEvent.VK_F5) {
			update((Object)null);
		}
	}

	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return super.getCellRenderer(row, column);
//		if (column == 0) return highlightCellRenderer;
	}

	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);
//		if (column == 1) return new TypeCellEditor();
    }
	
	
	@Override
	public String getToolTipText(MouseEvent event) {
		return super.getToolTipText(event);
	}


	/**
	 * This class represents highlight cell renderer according to pool.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class HighlightCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default background color.
		 */
		private Color defaultBackgroundColor = null;
		
		/**
		 * Default selected background color.
		 */
		private Color defaultSelectedBackgroundColor = null;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			defaultBackgroundColor = defaultBackgroundColor != null ? defaultBackgroundColor : comp.getBackground();
			defaultSelectedBackgroundColor = defaultSelectedBackgroundColor != null ? defaultSelectedBackgroundColor : table.getSelectionBackground();
			
			boolean someCondition = false;
			if (someCondition) {
				if (!isSelected)
					comp.setBackground(new Color(0, 255, 0));
				else
					comp.setBackground(new Color(0, 200, 200));
			}
			else {
				if (!isSelected)
					comp.setBackground(defaultBackgroundColor);
				else
					comp.setBackground(defaultSelectedBackgroundColor);
			}
			
			return comp;
		}
		
	}


	/**
	 * Specified cell editor.
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	protected class TypeCellEditor extends DefaultCellEditor {

		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default constructor.
		 */
		public TypeCellEditor() {
			super(new JComboBox<String>(new String[] {"type1", "type2", "type3"}));
		}
		
	}


}



/**
 * This class represents a template table model.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class TempTableModel extends SortableSelectableTableModel {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Modified flag.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor.
	 */
	public TempTableModel() {
		super();
	}
	
	
	/**
	 * Update table by some parameter.
	 * @param parameter some parameter.
	 */
	protected void update(Object parameter) {
		Vector<Vector<Object>> data = Util.newVector(0);
		
		setDataVector(data, toColumns());
		modified = false;
	}

	
	/**
	 * Converting something to row.
	 * @return row created from something.
	 */
	protected Vector<Object> toRow() {
		Vector<Object> row = Util.newVector(0);
		
		return row;
	}

	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		return columns;
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
//		if (columnIndex == 3) return Boolean.class;
	}
	
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		super.setValueAt(value, row, column);
		modified = true;
	}


}



