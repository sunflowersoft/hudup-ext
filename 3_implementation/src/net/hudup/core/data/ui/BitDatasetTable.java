/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.MapVector;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitItem;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class is Java table of bit dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BitDatasetTable extends RatingValueTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BitDatasetTable() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingValueTM newTableModel() {
		// TODO Auto-generated method stub
		return new BitDatasetTableModel();
	}


	/**
	 * Update this table with specified bit data and additional (item) identifiers.
	 * @param bitData specified bit data.
	 * @param moreIds additional (item) identifiers.
	 */
	public void update(BitData bitData, Collection<Integer> moreIds) {
		// TODO Auto-generated method stub
		BitDatasetTableModel model = (BitDatasetTableModel)getRatingValueTM();
		model.update(bitData, moreIds);
		
	}

	
}



/**
 * This class is table model of bit dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
class BitDatasetTableModel extends RatingValueTM {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal bit data.
	 */
	protected BitData bitData = null;
	
	
	/**
	 * Default constructor.
	 */
	public BitDatasetTableModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void update(Dataset dataset, Collection<Integer> notUsed) {
		BitData bitData = null;
		
		if (dataset != null)
			bitData = BitData.create(dataset);
		update(bitData, notUsed);
	}
	
	
	/**
	 * Updating this model with specified bit data and not used (item) identifiers.
	 * @param bitData specified bit data.
	 * @param notUsed not used (item) identifiers.
	 */
	public void update(BitData bitData, Collection<Integer> notUsed) {
		this.rowIndexes.clear();
		this.columnIndexes.clear();
		this.columnIds.clear();
		this.bitData = null;
		this.setDataVector(new String[0][], new String[0][]);
		
		if (bitData == null)
			return;
		
		this.bitData = bitData;
		
		List<Integer> bitItemIds = Util.newList();
		bitItemIds.addAll(this.bitData.bitItemIds());
		this.columnIds.addAll(bitItemIds);
		Collections.sort(this.columnIds);

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		List<Integer> rowIds = Util.newList();
		List<Integer> realSessionIds = this.bitData.realSessionIds();
		rowIds.addAll(realSessionIds);
		Collections.sort(rowIds);
		
		for (int i = 0; i < rowIds.size(); i++) {
			int rowId = rowIds.get(i);
			this.rowIndexes.put(rowId, i);
			
			Vector<Object> row = Util.newVector();
			row.setSize(this.columnIds.size());
			int sessionIdx = realSessionIds.indexOf(new Integer(rowId));
			for (int j = 0; j < this.columnIds.size(); j++) {
				int bitItemId = this.columnIds.get(j);
				BitItem item = this.bitData.get(bitItemId);
				BitSet bs = item.bitItem().getBitSet();
				if (bs.get(sessionIdx))
					row.set(j, 1);
				else
					row.set(j, "");
			}
			
			row.insertElementAt("User " + rowId, 0);
			data.add(row);
		}
		
//		for (int i = 0; i < columnIds_.size(); i++) {
//			int columnId = columnIds_.get(i);
//			columnIndexes_.put(columnId, i + 1);
//		}
		this.setDataVector(data, toBitColumns(this.columnIds, this.bitData));
	}

	
	/**
	 * Converting bit column (item) identifiers into vector.
	 * @param bitColumnIds bit column (item) identifiers.
	 * @return vector of bit column (item) identifiers.
	 */
	public static Vector<String> toBitColumns(List<Integer> bitColumnIds, BitData bitData) {
		Vector<String> columns = Util.newVector();
		columns.add("");
		for (int bitItemId : bitColumnIds) {
			BitItem item = bitData.get(bitItemId);
			int realItemId = item.pair().key();
			int rating = (int)item.pair().value();
			columns.add("Item" + TextParserUtil.CONNECT_SEP + realItemId + TextParserUtil.CONNECT_SEP + rating);
		}
		return columns;
	}
	
	
	@Override
	public void setMoreRatingVector(RatingVector vRat, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method");
	}
	
	
	@Override
	public List<MapVector<Object>> getRow(int rowIdx) {
		
		int rowIdFound = -1;
		Set<Integer> rowIds = this.rowIndexes.keySet();
		for (int rowId : rowIds) {
			if (this.rowIndexes.get(rowId) == rowIdx) {
				rowIdFound = rowId;
				break;
			}
				
		}
		
		if (rowIdFound == -1)
			return Util.newList();
		
		int n = getColumnCount();
		List<MapVector<Object>> result = Util.newList();
		
		MapVector<Object> vector = new MapVector<Object>(rowIdFound);
		for (int i = 1; i < n; i++) {
			Object value = getValueAt(rowIdx, i);
			String strValue = (value != null ? value.toString() : "");
			
			if (strValue.isEmpty() || strValue.equals("0")) 
				continue;
			
			int bitColumnId = this.columnIds.get(i - 1);
			BitItem item = this.bitData.get(bitColumnId);
			int realFieldId = item.pair().key();
			double ratingValue = item.pair().value();

			vector.put(realFieldId, ratingValue);
		}
		
		result.add(vector);
		
		return result;
	}
	
	
}
