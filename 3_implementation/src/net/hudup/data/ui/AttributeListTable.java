/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is Java table to show attribute list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AttributeListTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Supported attribute type.
	 */
	private final static Type SUPPORT_TYPE [] = {
		Type.bit, Type.nominal, Type.integer, Type.real, Type.string, Type.date, Type.time};

	
	/**
	 * Default constructor.
	 */
	public AttributeListTable() {
		super(new AttributeListTM());
		
		this.setDefaultEditor(Type.class, new TypeCellEditor());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!getAttributeListTM().enabled)
					return;

				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (!getAttributeListTM().enabled)
					return;

				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					removeRow();
				else if (e.getKeyCode() == KeyEvent.VK_ADD && e.isControlDown())
					addRow();
			}
			
		});
		
	}

	
	/**
	 * Getting table model of attribute list.
	 * @return {@link AttributeListTM} as table model of attribute list.
	 */
	public AttributeListTM getAttributeListTM() {
		return (AttributeListTM)getModel();
	}
	
	
	/**
	 * Getting attribute list.
	 * @param attList specified attribute list.
	 */
	public void set(AttributeList attList) {
		getAttributeListTM().set(attList);
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		getAttributeListTM().setEnabled(enabled);
	}
	
	
	/**
	 * Clear table.
	 */
	public void clear() {
		getAttributeListTM().clear();
	}
	
	
	/**
	 * Getting attribute list.
	 * @return {@link AttributeList} as attribute list.
	 */
	public AttributeList getAttributeList() {
		return getAttributeListTM().getAttributeList();
	}
	
	
	/**
	 * Applying attribute list to this table mode.
	 * @return whether apply action is successfully
	 */
	public boolean apply() {
		return getAttributeListTM().apply();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		AttributeListTM model = getAttributeListTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		else
			return renderer;
	}
	
	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
        TableCellEditor editor = null;
		AttributeListTM model = getAttributeListTM();
		if (column == 1)
			editor = getDefaultEditor(Type.class);
		else
			editor = getDefaultEditor(model.getColumnClass(row, column));
    	
    	if(editor == null)
    		return super.getCellEditor(row, column);
    	else
    		return editor;
    }

	
	/**
	 * Create context menu.
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miAddRow = UIUtil.makeMenuItem((String)null, "Add", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					addRow();
				}
			});
		contextMenu.add(miAddRow);
		
		JMenuItem miInsertRow = UIUtil.makeMenuItem((String)null, "Insert", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					insertRow();
				}
			});
		contextMenu.add(miInsertRow);

		JMenuItem miClearRowData = UIUtil.makeMenuItem((String)null, "Clear data", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					clearRow();
				}
			});
		contextMenu.add(miClearRowData);
			
		JMenuItem miRemoveRow = UIUtil.makeMenuItem((String)null, "Remove", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					removeRow();
				}
			});
		contextMenu.add(miRemoveRow);

		return contextMenu;
	}

	
	/**
	 * Adding a row.
	 */
	protected void addRow() {
		Vector<Object> emptyRow = getAttributeListTM().emptyRow();
		getAttributeListTM().addRow(emptyRow);
		
	}
	
	
	/**
	 * Inserting a row.
	 */
	protected void insertRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		Vector<Object> emptyRow = getAttributeListTM().emptyRow();
		getAttributeListTM().insertRow(selectedRow, emptyRow);
	}

	
	/**
	 * Clearing a row.
	 */
	protected void clearRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		getAttributeListTM().setValueAt("", selectedRow, 0);
		getAttributeListTM().setValueAt(Type.integer, selectedRow, 1);
		getAttributeListTM().setValueAt(false, selectedRow, 2);
		getAttributeListTM().setValueAt(false, selectedRow, 3);
	}

	
	/**
	 * Removing selected row.
	 */
	protected void removeRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		if (getRowCount() <= 1) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't remove this row due to one-row table", 
					"Can't remove this row", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		getAttributeListTM().removeRow(selectedRow);
	}

	
	/**
	 * Cell editor of attribute type.
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static class TypeCellEditor extends DefaultCellEditor {

		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default constructor.
		 */
		public TypeCellEditor() {
			super(createComboBox());
			
		}
		
		/**
		 * Creating Java combo-box of attribute type.
		 * @return {@link JComboBox} as Java combo-box of attribute type.
		 */
		private static JComboBox<Type> createComboBox() {
			JComboBox<Type> comb = new JComboBox<Type>();
			
			for (Type type : SUPPORT_TYPE)
				comb.addItem(type);
			
			return comb;
		}
	}
	
	
}



/**
 * This class is table model of attribute list. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class AttributeListTM extends DefaultTableModel {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Maximum size.
	 */
	public final static int MAX_SIZE = 10;
	
	
	/**
	 * Attribute list.
	 */
	protected AttributeList attList = null;
	
	
	/**
	 * Enabled flag.
	 */
	protected boolean enabled = true;

	
	/**
	 * Default constructor.
	 */
	public AttributeListTM() {
		super();
		set(new AttributeList());
	}
	
	
	/**
	 * Setting attribute list.
	 * @param attList attribute list.
	 */
	public void set(AttributeList attList) {
		this.attList = attList;
		
		refresh();
	}
	
	
	/**
	 * Creating column names.
	 * @return column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("Attribute");
		columns.add("Type");
		columns.add("Is Key");
		columns.add("Auto increment");
		
		return columns;
	}
	
	
	/**
	 * Creating empty row.
	 * @return empty row.
	 */
	public Vector<Object> emptyRow() {
		Vector<Object> row = Util.newVector();
		row.add("");
		row.add(Type.integer);
		row.add(false);
		row.add(false);
		
		return row;
	}
	
	
	/**
	 * Refreshing this table model.
	 */
	public void refresh() {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < attList.size(); i++) {
			Attribute att = attList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(att.getName());
			row.add(att.getType());
			row.add(att.isKey());
			row.add(att.isAutoInc());
			
			data.add(row);
		}
		
		int n = MAX_SIZE - data.size();
		for (int i = 0; i < n; i++) {
			Vector<Object> row = emptyRow();
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	

	/**
	 * Getting class at specified row and column.
	 * @param row specified row
	 * @param column specified column.
	 * @return class at specified row and column.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return null;
		else
			return value.getClass();
	}
	
	
	/**
	 * Clearing Java table model.
	 */
	public void clear() {
		
		Vector<String> columns = createColumns();

		int n = MAX_SIZE;
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < n; i++) {
			Vector<Object> row = emptyRow();
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * Getting attribute list.
	 * @return {@link AttributeList} as attribute list.
	 */
	public AttributeList getAttributeList() {
		return attList;
	}
	
	
	/**
	 * Setting enabled flag.
	 * @param enabled enabled flag.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return (enabled);
	}

	
	/**
	 * Applying the internal attribute list into this model.
	 * @return whether apply action successfully.
	 */
	public boolean apply() {
		
		attList.clear();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			String name = ((String)getValueAt(i, 0)).trim();
			Type type = (Type)getValueAt(i, 1);
			boolean isKey = (Boolean)getValueAt(i, 2);
			boolean autoInc = (Boolean)getValueAt(i, 3);
			
			if (name.isEmpty())
				continue;
			
			Attribute att = new Attribute(name, type);
			att.setKey(isKey);
			att.setAutoInc(autoInc);
			
			attList.add(att);
		}
		
		return true;
	}
	
	
}