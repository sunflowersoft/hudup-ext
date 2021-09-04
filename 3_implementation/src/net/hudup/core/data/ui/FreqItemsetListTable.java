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
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.MapVector;
import net.hudup.core.data.Pair;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitDataUtil;
import net.hudup.core.data.bit.BitItemset;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.ui.TxtOutput;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is Java table to show list of frequent itemsets.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class FreqItemsetListTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
	 * Default constructor.
	 */
	public FreqItemsetListTable() {
		super();
		setModel(new FreqItemsetListTM());
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					viewRow();
				}
			}
		});
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	
	/**
	 * Updating this table with specified list of bit itemsets and bit data.
	 * @param itemsetList list of bit itemsets.
	 * @param bitData bit data.
	 */
	public void update(List<BitItemset> itemsetList, BitData bitData) {
		FreqItemsetListTM model = getTableModel();
		model.update(itemsetList, bitData);
	}
	
	
	/**
	 * Updating this table with specified list of bit itemsets and bit item map.
	 * @param itemsetList list of bit itemsets.
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 */
	public void update(List<BitItemset> itemsetList, Map<Integer, Pair> bitItemMap) {
		FreqItemsetListTM model = getTableModel();
		model.update(itemsetList, bitItemMap);
	}

	
	/**
	 * Clearing content of this table.
	 */
	public void clear() {
		FreqItemsetListTM model = getTableModel();
		model.clear();
		
	}
	
	
	/**
	 * Getting model of this table.
	 * @return {@link FreqItemsetListTM} as model of this table.
	 */
	public FreqItemsetListTM getTableModel() {
		return (FreqItemsetListTM)super.getModel();
	}
	
	
	/**
	 * Getting list of bit itemsets.
	 * @return list of {@link BitItemset}
	 */
	public List<BitItemset> getItemsetList() {
		return getTableModel().getItemsetList();
	}
	
	
	
	/**
	 * Creating context menu.
	 * @return {@link JPopupMenu} as context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miViewRow = UIUtil.makeMenuItem((String)null, "View row", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					viewRow();
				}
			});
		contextMenu.add(miViewRow);
		
		return contextMenu;
	}
	
	
	/**
	 * Viewing a row.
	 */
	protected void viewRow() {
		int selIdx = getSelectedRow();
		if (selIdx == -1)
			return;
		
		MapVector<Object> row = getTableModel().getRow(selIdx);
		
		List<Integer> columnIds = Util.newList();
		columnIds.addAll(row.fieldIds());
		Collections.sort(columnIds);
		if (columnIds.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No row", 
					"No row", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> rowData = new Vector<Object>(row.toList(columnIds));
		rowData.insertElementAt(getTableModel().getValueAt(selIdx, 1), 0);
		rowData.insertElementAt(getTableModel().getValueAt(selIdx, 0), 0);
		data.add(rowData);
		
		Vector<String> columnNames = FreqItemsetListTM.toColumns(columnIds);
		
		DefaultTableModel dm = new DefaultTableModel(data, columnNames) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
			
		};
		JTable tbl = new JTable(dm);
		tbl.setAutoResizeMode(AUTO_RESIZE_OFF);
		
		JDialog viewer = null;
		Dialog dlg = UIUtil.getDialogForComponent(this);
		if (dlg != null)
			viewer = new JDialog(dlg, "Row values", false);
		else
			viewer = new JDialog(UIUtil.getDialogForComponent(this), "Row values", false);
			
		viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		viewer.setSize(600, 200);
		viewer.setLayout(new BorderLayout());
		
		viewer.add(new JScrollPane(tbl), BorderLayout.CENTER);
		
		TxtOutput txtInfo = new TxtOutput();
		txtInfo.setRows(5);
		txtInfo.setColumns(10);
		viewer.add(new JScrollPane(txtInfo), BorderLayout.SOUTH);
		txtInfo.setEditable(false);
		
		String line = row.toString("Frequent itemset ", "");
		line += "\nThe support is " + getTableModel().getValueAt(selIdx, 1);
		
		txtInfo.setText(line);
		txtInfo.setCaretPosition(0);
		
		viewer.setVisible(true);
	}
	
}



/**
 * This class is frequent itemset list model the {@link FreqItemsetListTable}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
class FreqItemsetListTM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of itemsets.
	 */
	protected List<BitItemset> itemsetList = Util.newList();
	
	
	/**
	 * List of column identifiers.
	 */
	protected List<Integer> columnIds = Util.newList();
	
			
	/**
	 * Map of column indices.
	 */
	protected Map<Integer, Integer> columnIndexes = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public FreqItemsetListTM() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Converting column identifiers into vector of texts.
	 * @param columnIds column identifiers.
	 * @return vector of column names.
	 */
	public static Vector<String> toColumns(List<Integer> columnIds) {
		Vector<String> columns = new Vector<String>();
		columns.add("");
		columns.add("Support");
		for (int columnId : columnIds) {
			columns.add("Item " + columnId);
		}
		return columns;
	}

	
	/**
	 * Converting an itemset into vector.
	 * @param idx specified index.
	 * @param itemset specified itemset.
	 * @param columnIds column identifiers.
	 * @param bitData bit data.
	 * @return a row as object vector of itemset.
	 */
	public static Vector<Object> toRow(
			int idx,
			BitItemset itemset, 
			List<Integer> columnIds, 
			BitData bitData) {
		
		Vector<Object> row = new Vector<Object>();
		row.add("Frequent itemset " + (idx + 1));
		row.add(MathUtil.format(itemset.getSupport()));
			
		for (int columnId : columnIds) {
			int found = itemset.indexOfReal(columnId, bitData);
			
			if (found != -1) {
				int bitItemId = itemset.get(found);
				double value = bitData.get(bitItemId).pair().value();
				row.add(MathUtil.format(value));
			}
			else {
				row.add("");
			}
		}
		
		return row;
	}
	
	
	/**
	 * Converting an itemset into vector.
	 * @param idx specified index.
	 * @param itemset specified itemset.
	 * @param columnIds column identifiers.
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 * @return object vector of itemset.
	 */
	public static Vector<Object> toRow(
			int idx,
			BitItemset itemset, 
			List<Integer> columnIds, 
			Map<Integer, Pair> bitItemMap) {
		
		Vector<Object> row = new Vector<Object>();
		row.add("Frequent itemset " + (idx + 1));
		row.add(MathUtil.format(itemset.getSupport()));
		
		for (int columnId : columnIds) {
			List<Integer> bitItemIdList = BitDataUtil.findBitItemIdOf(bitItemMap, columnId);
			if (bitItemIdList.size() == 0)
				continue;
			
			int bitItemIdFound = -1;
			for (int bitItemId : bitItemIdList) {
				if (itemset.indexOf(bitItemId) != -1) {
					bitItemIdFound = bitItemId;
					break;
				}
			}
			if (bitItemIdFound == -1)
				continue;
			
			if (bitItemMap.containsKey(columnId))
				row.add(MathUtil.format(bitItemMap.get(bitItemIdFound).value()));
			else
				row.add("");
		}
		
		return row;
	}

	
	/**
	 * Updating this model with list of bit itemsets and bit data.
	 * @param itemsetList list of bit itemsets.
	 * @param bitData bit data.
	 */
	public void update(List<BitItemset> itemsetList, BitData bitData) {
		this.itemsetList.clear();
		this.columnIds.clear();
		this.columnIndexes.clear();
		this.setDataVector(new String[0][], new String[0][]);
		this.itemsetList.clear();
		
		if (itemsetList == null || bitData == null)
			return;
		
		this.itemsetList = itemsetList;
		
		Set<Integer> realItemIds = Util.newSet();
		for (BitItemset itemset : itemsetList) {
			List<Pair> pairList = itemset.toItemPairList(bitData);
			
			for (Pair pair : pairList)
				realItemIds.add(pair.key());
		}
		this.columnIds.clear();
		this.columnIds.addAll(realItemIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		for (int i = 0; i < itemsetList.size(); i++) {
			BitItemset itemset = itemsetList.get(i);
			
			Vector<Object> row = toRow(i, itemset, this.columnIds, bitData);
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns(this.columnIds));
		
	}
	
	
	/**
	 * Updating this model with list of bit itemsets and bit item map.
	 * @param itemsetList list of bit itemsets.
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 */
	public void update(List<BitItemset> itemsetList, Map<Integer, Pair> bitItemMap) {
		this.itemsetList.clear();
		this.columnIds.clear();
		this.columnIndexes.clear();
		this.setDataVector(new String[0][], new String[0][]);
		this.itemsetList.clear();
		
		if (itemsetList == null || bitItemMap == null)
			return;
		
		this.itemsetList = itemsetList;
		
		Set<Integer> realItemIds = Util.newSet();
		for (BitItemset itemset : itemsetList) {
			List<Pair> pairList = BitDataUtil.toItemPairList(bitItemMap, itemset);
			
			for (Pair pair : pairList)
				realItemIds.add(pair.key());
		}
		this.columnIds.clear();
		this.columnIds.addAll(realItemIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		for (int i = 0; i < itemsetList.size(); i++) {
			BitItemset itemset = itemsetList.get(i);
			
			Vector<Object> row = toRow(i, itemset, this.columnIds, bitItemMap);
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns(this.columnIds));
		
	}

	
	/**
	 * Clearing this model.
	 */
	public void clear() {
		update(null, (BitData) null);
	}
	
	
	/**
	 * Getting map vector at specified row.
	 * @param row specified row.
	 * @return {@link MapVector} as map vector at specified row.
	 */
	public MapVector<Object> getRow(int row) {
		MapVector<Object> vector = new MapVector<Object>(row + 1);
		
		int n = getColumnCount();
		for (int i = 2; i < n; i++) {
			Object value = getValueAt(row, i);
			String strValue = (value != null ? value.toString() : "");
			
			if (strValue.isEmpty()) 
				continue;
			
			int columnId = this.columnIds.get(i - 2);
			vector.put(columnId, value);
		}
		
		return vector;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * Getting list of bit itemsets.
	 * @return list of {@link BitItemset}
	 */
	public List<BitItemset> getItemsetList() {
		return itemsetList;
	}
	
	
}
