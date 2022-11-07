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
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class represents pool client manager.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PoolClientManager extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Pool item.
	 */
	protected DatasetPoolExchangedItem dpeItem = null;
	
	
	/**
	 * Pool client table.
	 */
	protected PoolClientTable tblPoolClient = null;
	
	
	/**
	 * Constructor with pool item.
	 * @param dpeItem pool item.
	 * @param comp parent component.
	 */
	public PoolClientManager(DatasetPoolExchangedItem dpeItem, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Pool client manager", true);
		this.dpeItem = dpeItem;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());

		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		
		JLabel lblIntro = new JLabel("Clients of pool \"" + dpeItem.toString() + "\"");
		header.add(lblIntro);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		tblPoolClient = new PoolClientTable(dpeItem);
		body.add(new JScrollPane(tblPoolClient), BorderLayout.CENTER);
		
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
				tblPoolClient.update();
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
		if (!tblPoolClient.isModified()) {
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
		if (!tblPoolClient.isModified()) return;
		
		List<ClientWrapper> clients = tblPoolClient.getClients(false);
		for (ClientWrapper client : clients) {
			try {
				client.close();
				dpeItem.removeClient(client);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		tblPoolClient.update();
		
		JOptionPane.showMessageDialog(this, "Successful client applying", "Successful client applying", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Showing the pool client manager.
	 * @param dpeItem pool item.
	 * @param comp parent component.
	 */
	public static void show(DatasetPoolExchangedItem dpeItem, Component comp) {
		new PoolClientManager(dpeItem, comp).setVisible(true);
	}
	
	
}



/**
 * This is table of pool clients.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class PoolClientTable extends SortableSelectableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor with pool item.
	 * @param pdeItem pool item.
	 */
	public PoolClientTable(DatasetPoolExchangedItem pdeItem) {
		super(new PoolClientTM(null));
		update(pdeItem);
		
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
	public void update(DatasetPoolExchangedItem pdeItem) {
		getPoolClientTM().update(pdeItem);
		init();
	}
	
	
	/**
	 * Update with current pool item.
	 */
	public void update() {
		getPoolClientTM().update();
		init();
	}

	
	@Override
	protected void init() {
		super.init();
		
		TableColumnModel tcm = getColumnModel();
		if (tcm.getColumnCount() > PoolClientTM.CLIENT_OBJECT_INDEX) {
			tcm.getColumn(PoolClientTM.CLIENT_OBJECT_INDEX).setMaxWidth(0);
			tcm.getColumn(PoolClientTM.CLIENT_OBJECT_INDEX).setMinWidth(0);
			tcm.getColumn(PoolClientTM.CLIENT_OBJECT_INDEX).setPreferredWidth(0);
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
		ClientWrapper client = (ClientWrapper)getValueAt(selectedRow, PoolClientTM.CLIENT_OBJECT_INDEX);
		if (client == null) return;
		
		client.reset();
		setValueAt(client.getStatus(), selectedRow, PoolClientTM.STATUS_INDEX);
	}
	
	
	/**
	 * Getting pool client table model.
	 * @return pool client table model.
	 */
	protected PoolClientTM getPoolClientTM() {
		return (PoolClientTM)getModel();
	}
	
	
	/**
	 * Testing whether the client model is modified.
	 * @return whether the client model is modified.
	 */
	public boolean isModified() {
		return getPoolClientTM().isModified();
	}
	
	
	/**
	 * Getting clients
	 * @param attached attaching flag.
	 * @return clients with attaching flag.
	 */
	protected List<ClientWrapper> getClients(boolean attached) {
		List<ClientWrapper> clients = Util.newList();
		for (int i = 0; i < getRowCount(); i++) {
			ClientWrapper client = (ClientWrapper)getValueAt(i, PoolClientTM.CLIENT_OBJECT_INDEX);
			boolean attachedIndicator = (boolean)getValueAt(i, PoolClientTM.ATTACHED_INDEX);
			if (client != null && attachedIndicator == attached) clients.add(client);
		}
		
		return clients;
	}
	
	
}


/**
 * This is table model of pool clients.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class PoolClientTM extends SortableSelectableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Object index 1.
	 */
	protected static final int CLIENT_OBJECT_INDEX = 1;

	
	/**
	 * Status index.
	 */
	protected static final int STATUS_INDEX = 2;

	
	/**
	 * Attached index.
	 */
	protected static final int ATTACHED_INDEX = 3;
	
	
	/**
	 * Internal pool item.
	 */
	protected DatasetPoolExchangedItem pdeItem = null;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor with pool item.
	 * @param pdeItem pool item.
	 */
	public PoolClientTM(DatasetPoolExchangedItem pdeItem) {
		super();
		update(pdeItem);
	}

	
	/**
	 * Updating table model based on pool item.
	 * @param pdeItem pool item.
	 */
	public void update(DatasetPoolExchangedItem pdeItem) {
		this.pdeItem = pdeItem;
		if (pdeItem == null) {
			setDataVector(Util.newVector(), toColumns());
			modified = false;
			return;
		}
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < pdeItem.getClientSize(); i++) {
			try {
				ClientWrapper client = pdeItem.getClient(i);
				Vector<Object> row = toRow(client);
				if (row != null) data.add(row);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		setDataVector(data, toColumns());
		modified = false;
	}
	
	
	/**
	 * Update with current pool item.
	 */
	public void update() {
		update(pdeItem);
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model, specified by {@link Vector} of string.
	 */
	protected static Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Client");
		columns.add("Client object");
		columns.add("Status");
		columns.add("Attached");
		
		return columns;
	}


	/**
	 * Create row from client.
	 * @param client specified client.
	 * @return row created from client.
	 */
	protected static Vector<Object> toRow(ClientWrapper client) {
		if (client == null) return null;
		
		Vector<Object> row = Util.newVector();
		row.add(client.getName());
		row.add(client);
		row.add(client.getStatus());
		row.add(true);
		
		return row;
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

