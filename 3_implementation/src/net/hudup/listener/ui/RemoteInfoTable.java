/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.listener.RemoteInfo;
import net.hudup.listener.RemoteInfoList;

/**
 * This graphic user interface (GUI) is the table for showing remote information {@link RemoteInfo}.
 * It allows users to modify such remote information.
 * This table contains a built-in list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public RemoteInfoTable() {
		super(new RemoteInfoTM());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.setDefaultRenderer(HiddenText.class, new HiddenTextCellRenderer());
	}
	
	
	/**
	 * Getting the model of this table.
	 * @return {@link RemoteInfoTM} of this table.
	 */
	public RemoteInfoTM getRemoteInfoTM() {
		return (RemoteInfoTM)this.getModel();
		
	}
	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
	}

	
	/**
	 * Update this table by the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		getRemoteInfoTM().update(rInfoList);
		init();
	}
	
	
	/**
	 * Getting the remote information at currently selected row.
	 * @return {@link RemoteInfo} at currently selected row.
	 */
	public RemoteInfo getSelectedRemoteInfo() {
		int row = getSelectedRow();
		if (row == -1)
			return null;
		
		return getRemoteInfoTM().getRemoteInfo(row);
	}


	/**
	 * Selecting the row showing the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 */
	public void selectRemoteInfo(String host, int port) {
		int n = getRowCount();
		int selected = -1;
		for (int i = 0; i < n; i++) {
			String h = (String)getValueAt(i, 1);
			int p = (Integer)getValueAt(i, 2);
			
			if (h.compareToIgnoreCase(host) == 0 && p == port) {
				selected = i;
				break;
			}
		}
		
		if (selected != -1)
			this.getSelectionModel().addSelectionInterval(selected, selected);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		RemoteInfoTM model = getRemoteInfoTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	/**
	 * The cell renderer for showing a value at each cell of {@link RemoteInfoTable}.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private class HiddenTextCellRenderer extends DefaultTableCellRenderer.UIResource {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * Default constructor.
		 */
		public HiddenTextCellRenderer() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
        public void setValue(Object value) {
            if (value == null) {
                setText("");
            }
            else if (value instanceof HiddenText) {
                setText( ((HiddenText)value).getMask() );
            }
            else {
            	HiddenText hidden = new HiddenText(value.toString());
            	setText(hidden.getMask());
            }
        }
		
	}
	
	
}



/**
 * This is model of the table ({@link RemoteInfoTable}) for showing remote information.
 * Note, {@link RemoteInfoTable} allows users to modify such remote information.
 * This model contains the list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RemoteInfoTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RemoteInfoTM() {
		super();
	}
	
	
	/**
	 * Creating names of columns of tables.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Host");
		columns.add("Port");
		columns.add("Account");
		columns.add("Password");
		
		return columns;
	}
	
	
	/**
	 * Update this model from the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			
			Vector<Object> row = Util.newVector();
			
			row.add(i + 1);
			row.add(rInfo.host);
			row.add(rInfo.port);
			row.add(rInfo.account);
			row.add(rInfo.password);
				
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * Getting the remote information at specified row.
	 * @param row specified row.
	 * @return {@link RemoteInfo} at specified row.
	 */
	public RemoteInfo getRemoteInfo(int row) {
		String host = (String) getValueAt(row, 1);
		int port = (Integer) getValueAt(row, 2);
		String account = (String) getValueAt(row, 3);
		HiddenText password = (HiddenText) getValueAt(row, 4);
		return new RemoteInfo(host, port, account, password);
		
	}
	
	
	/**
	 * Getting the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 * @return {@link RemoteInfo} having specified host and port.
	 */
	public RemoteInfo getRemoteInfo(String host, int port) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return rInfo;
		}
		
		return null;
	}
	
	
	/**
	 * Getting the list of remote information of this model.
	 * @return {@link RemoteInfoList} of this model.
	 */
	public RemoteInfoList getRemoteInfoList() {
		RemoteInfoList rInfoList = new RemoteInfoList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			rInfoList.add(rInfo);
		}
		
		return rInfoList;
	}
	
	
	
	/**
	 * Getting the class of value at specified row and column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and column.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


}



/**
 * This graphic user interface (GUI) is the table for showing remote information {@link RemoteInfo}.
 * It allows users to modify such remote information.
 * This table contains a built-in list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class RemoteInfoTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public RemoteInfoTable2() {
		super(new RemoteInfoTM2());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.setDefaultRenderer(HiddenText.class, new HiddenTextCellRenderer());
	}
	
	
	/**
	 * Getting the model of this table.
	 * @return {@link RemoteInfoTM2} of this table.
	 */
	public RemoteInfoTM2 getRemoteInfoTM() {
		return (RemoteInfoTM2)this.getModel();
		
	}
	
	
	/**
	 * Update this table by the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		getRemoteInfoTM().update(rInfoList);
	}
	
	
	/**
	 * Getting the remote information at currently selected row.
	 * @return {@link RemoteInfo} at currently selected row.
	 */
	public RemoteInfo getSelectedRemoteInfo() {
		int row = getSelectedRow();
		if (row == -1)
			return null;
		
		return getRemoteInfoTM().getRemoteInfo(row);
	}


	/**
	 * Selecting the row showing the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 */
	public void selectRemoteInfo(String host, int port) {
		int n = getRowCount();
		int selected = -1;
		for (int i = 0; i < n; i++) {
			String h = (String)getValueAt(i, 1);
			int p = (Integer)getValueAt(i, 2);
			
			if (h.compareToIgnoreCase(host) == 0 && p == port) {
				selected = i;
				break;
			}
		}
		
		if (selected != -1)
			this.getSelectionModel().addSelectionInterval(selected, selected);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		RemoteInfoTM2 model = getRemoteInfoTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	/**
	 * The cell renderer for showing a value at each cell of {@link RemoteInfoTable2}.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private class HiddenTextCellRenderer extends DefaultTableCellRenderer.UIResource {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * Default constructor.
		 */
		public HiddenTextCellRenderer() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
        public void setValue(Object value) {
            if (value == null) {
                setText("");
            }
            else if (value instanceof HiddenText) {
                setText( ((HiddenText)value).getMask() );
            }
            else {
            	HiddenText hidden = new HiddenText(value.toString());
            	setText(hidden.getMask());
            }
        }
		
	}
	
	
}


/**
 * This is model of the table ({@link RemoteInfoTable2}) for showing remote information.
 * Note, {@link RemoteInfoTable2} allows users to modify such remote information.
 * This model contains the list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class RemoteInfoTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RemoteInfoTM2() {
		super();
	}
	
	
	/**
	 * Creating names of columns of tables.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Host");
		columns.add("Port");
		columns.add("Account");
		columns.add("Password");
		
		return columns;
	}
	
	
	/**
	 * Update this model from the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			
			Vector<Object> row = Util.newVector();
			
			row.add(i + 1);
			row.add(rInfo.host);
			row.add(rInfo.port);
			row.add(rInfo.account);
			row.add(rInfo.password);
				
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * Getting the remote information at specified row.
	 * @param row specified row.
	 * @return {@link RemoteInfo} at specified row.
	 */
	public RemoteInfo getRemoteInfo(int row) {
		String host = (String) getValueAt(row, 1);
		int port = (Integer) getValueAt(row, 2);
		String account = (String) getValueAt(row, 3);
		HiddenText password = (HiddenText) getValueAt(row, 4);
		return new RemoteInfo(host, port, account, password);
		
	}
	
	
	/**
	 * Getting the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 * @return {@link RemoteInfo} having specified host and port.
	 */
	public RemoteInfo getRemoteInfo(String host, int port) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return rInfo;
		}
		
		return null;
	}
	
	
	/**
	 * Getting the list of remote information of this model.
	 * @return {@link RemoteInfoList} of this model.
	 */
	public RemoteInfoList getRemoteInfoList() {
		RemoteInfoList rInfoList = new RemoteInfoList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			rInfoList.add(rInfo);
		}
		
		return rInfoList;
	}
	
	
	
	/**
	 * Getting the class of value at specified row and column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and column.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


}



