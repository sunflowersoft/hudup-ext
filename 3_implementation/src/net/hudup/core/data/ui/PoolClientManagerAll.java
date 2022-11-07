/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumnModel;

import net.hudup.core.Util;
import net.hudup.core.data.ClientWrapper;
import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.ui.PoolClientTableAll.ItemClient;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class represents pool client manager over pool service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PoolClientManagerAll extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Pool service.
	 */
	protected DatasetPoolsService poolsService = null;
	
	
	/**
	 * Pool client table over pool service.
	 */
	protected PoolClientTableAll tblPoolClientAll = null;
	
	
	/**
	 * Constructor with pool service.
	 * @param poolsService pool service.
	 * @param comp parent component.
	 */
	public PoolClientManagerAll(DatasetPoolsService poolsService, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Pool client manager over service", true);
		this.poolsService = poolsService;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());

		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		
		JLabel lblIntro = new JLabel("Clients from pool service");
		header.add(lblIntro);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		tblPoolClientAll = new PoolClientTableAll(poolsService);
		body.add(new JScrollPane(tblPoolClientAll), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply();
				dispose();
			}
		});
		footer.add(btnOK);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply();
			}
		});
		footer.add(btnApply);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblPoolClientAll.update();
			}
		});
		footer.add(btnRefresh);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
		
	}

	
	@Override
	public void dispose() {
		if (!tblPoolClientAll.isModified()) {
			super.dispose();
			return;
		}

		int answer = JOptionPane.showConfirmDialog(this, "Client model is modified.\nDo you want to apply such modification?", "Modification confirmation", JOptionPane.OK_CANCEL_OPTION);
		if (answer == JOptionPane.OK_OPTION) onApply();
		
		super.dispose();
	}


	/**
	 * On-driven applying client changing.
	 */
	private void onApply() {
		if (!tblPoolClientAll.isModified()) return;
		
		List<ItemClient> ics = tblPoolClientAll.getClients(false);
		for (ItemClient ic : ics) {
			try {
				ic.item.removeReleaseClient(ic.client);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		tblPoolClientAll.update();
		
		JOptionPane.showMessageDialog(this, "Successful client applying", "Successful client applying", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Showing the pool client manager.
	 * @param poolsService pool service.
	 * @param comp parent component.
	 */
	public static void show(DatasetPoolsService poolsService, Component comp) {
		new PoolClientManagerAll(poolsService, comp).setVisible(true);
	}
	
	
}



/**
 * This is table of pool clients.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class PoolClientTableAll extends SortableSelectableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor with pool service.
	 * @param poolsService pool service.
	 */
	public PoolClientTableAll(DatasetPoolsService poolsService) {
		super(new PoolClientTMAll(null));
		update(poolsService);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isEditable()) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if (contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
	}
	
	
	/**
	 * Updating table based on pool item.
	 * @param pdeItem pool item.
	 */
	public void update(DatasetPoolsService poolsService) {
		getPoolClientTMAll().update(poolsService);
		init();
	}
	
	
	/**
	 * Update with current pool item.
	 */
	public void update() {
		getPoolClientTMAll().update();
		init();
	}

	
	@Override
	protected void init() {
		super.init();
		
		TableColumnModel tcm = getColumnModel();
		if (tcm.getColumnCount() > PoolClientTMAll.POOL_OBJECT_INDEX) {
			tcm.getColumn(PoolClientTMAll.POOL_OBJECT_INDEX).setMaxWidth(0);
			tcm.getColumn(PoolClientTMAll.POOL_OBJECT_INDEX).setMinWidth(0);
			tcm.getColumn(PoolClientTMAll.POOL_OBJECT_INDEX).setPreferredWidth(0);
		}
		if (tcm.getColumnCount() > PoolClientTMAll.CLIENT_OBJECT_INDEX) {
			tcm.getColumn(PoolClientTMAll.CLIENT_OBJECT_INDEX).setMaxWidth(0);
			tcm.getColumn(PoolClientTMAll.CLIENT_OBJECT_INDEX).setMinWidth(0);
			tcm.getColumn(PoolClientTMAll.CLIENT_OBJECT_INDEX).setPreferredWidth(0);
		}
	}


	/**
	 * Create context menu.
	 * @return context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miSaveScript = UIUtil.makeMenuItem((String)null, "Reset", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					resetClient();
				}
			});
		contextMenu.add(miSaveScript);
		
		return contextMenu;
	}
	
	
	/**
	 * Resetting client
	 */
	private void resetClient() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return;
		ClientWrapper client = (ClientWrapper)getValueAt(selectedRow, PoolClientTMAll.CLIENT_OBJECT_INDEX);
		if (client == null) return;
		
		client.reset();
		setValueAt(client.getStatus(), selectedRow, PoolClientTMAll.STATUS_INDEX);
	}
	
	
	/**
	 * Getting pool client table model over service.
	 * @return pool client table model over service.
	 */
	protected PoolClientTMAll getPoolClientTMAll() {
		return (PoolClientTMAll)getModel();
	}
	
	
	/**
	 * Testing whether the client model is modified.
	 * @return whether the client model is modified.
	 */
	public boolean isModified() {
		return getPoolClientTMAll().isModified();
	}
	
	
	/**
	 * Getting clients
	 * @param attached attaching flag.
	 * @return clients with attaching flag.
	 */
	protected List<ItemClient> getClients(boolean attached) {
		List<ItemClient> clients = Util.newList();
		for (int i = 0; i < getRowCount(); i++) {
			DatasetPoolExchangedItem item = (DatasetPoolExchangedItem)getValueAt(i, PoolClientTMAll.POOL_OBJECT_INDEX);
			ClientWrapper client = (ClientWrapper)getValueAt(i, PoolClientTMAll.CLIENT_OBJECT_INDEX);
			if (item == null || client == null) continue;
			
			boolean attachedIndicator = (boolean)getValueAt(i, PoolClientTMAll.ATTACHED_INDEX);
			if (client != null && attachedIndicator == attached) clients.add(new ItemClient(item, client));
		}
		
		return clients;
	}
	
	
	/**
	 * This class represents a pair of item and client.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected static class ItemClient {
		
		/**
		 * Pool item.
		 */
		public DatasetPoolExchangedItem item = null;
		
		/**
		 * Attached client.
		 */
		public ClientWrapper client = null;
		
		/**
		 * Constructor with item and client.
		 * @param item specified item.
		 * @param client specified client.
		 */
		public ItemClient(DatasetPoolExchangedItem item, ClientWrapper client) {
			this.item = item;
			this.client = client;
		}
		
	}
	
	
}



/**
 * This is table model of pool clients over pool service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class PoolClientTMAll extends SortableSelectableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Pool object index.
	 */
	protected static final int POOL_OBJECT_INDEX = 1;

	
	/**
	 * Client object index.
	 */
	protected static final int CLIENT_OBJECT_INDEX = 3;

	
	/**
	 * Status index.
	 */
	protected static final int STATUS_INDEX = 4;

	
	/**
	 * Attached index.
	 */
	protected static final int ATTACHED_INDEX = 5;
	
	
	/**
	 * Internal pool service.
	 */
	protected DatasetPoolsService poolsService = null;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor with pool service.
	 * @param poolsService pool service.
	 */
	public PoolClientTMAll(DatasetPoolsService poolsService) {
		super();
		update(poolsService);
	}

	
	/**
	 * Updating table model based on pool item.
	 * @param poolsService pool service.
	 */
	public void update(DatasetPoolsService poolsService) {
		this.poolsService = poolsService;
		if (poolsService == null) {
			setDataVector(Util.newVector(), toColumns());
			modified = false;
			return;
		}
		
		Vector<Vector<Object>> data = Util.newVector();
		try {
			Set<String> names = poolsService.names();
			for (String name : names) {
				try {
					DatasetPoolExchangedItem item = poolsService.get(name);
					Vector<Vector<Object>> itemData = toRow(item);
					if (itemData != null) data.addAll(itemData);
				}  catch (Throwable e) {LogUtil.trace(e);}
			}
			
			
		} catch (Throwable e) {LogUtil.trace(e);}
		
		setDataVector(data, toColumns());
		modified = false;
	}
	
	
	/**
	 * Update with current pool service.
	 */
	public void update() {
		update(poolsService);
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model, specified by {@link Vector} of string.
	 */
	protected static Vector<String> toColumns() {
		Vector<String> columns = PoolClientTM.toColumns();
		if (columns == null) return null;
		
		Vector<String> columnsAll = Util.newVector(); 
		columnsAll.add("Pool");
		columnsAll.add("Pool object");
		columnsAll.addAll(columns);
		
		return columnsAll;
	}


	/**
	 * Creating rows from pool.
	 * @param item pool item.
	 * @return rows created from pool.
	 */
	protected static Vector<Vector<Object>> toRow(DatasetPoolExchangedItem item) {
		if (item == null) return null;
		Vector<Vector<Object>> data = Util.newVector();
		try {
			int n = item.getClientSize();
			for (int i = 0; i < n; i++) {
				try {
					ClientWrapper client = item.getClient(i);
					Vector<Object> row = PoolClientTM.toRow(client);
					if (row == null) continue;
					
					Vector<Object> itemRow = Util.newVector();
					itemRow.add(item.getName());
					itemRow.add(item);
					itemRow.addAll(row);
					
					data.add(itemRow);
				} catch (Exception e) {LogUtil.trace(e);}
			}
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return data;
	}
	
	
	/**
	 * Testing whether this model is modified.
	 * @return whether model is modified.
	 */
	public boolean isModified() {
		return modified;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == ATTACHED_INDEX)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (!isEditable())
			return false;
		else if (column == ATTACHED_INDEX)
			return true;
		else
			return false;
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
		modified = true;
	}
	
	
}
