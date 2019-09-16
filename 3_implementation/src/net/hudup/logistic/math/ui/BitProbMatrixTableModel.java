package net.hudup.logistic.math.ui;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.logistic.math.BitProbItemMatrix;

/**
 * This is Java table model for show bit probability matrix (binary probability matrix) {@link BitProbItemMatrix}, which is model of {@link BitProbMatrixTable}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BitProbMatrixTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Bit probability matrix (binary probability matrix).
	 */
	protected BitProbItemMatrix matrix = null;
	
	
	/**
	 * Default constructor.
	 */
	public BitProbMatrixTableModel() {
		
	}
	
	
	/**
	 * Update table model by binary probability matrix.
	 * @param matrix binary probability matrix.
	 */
	public void update(BitProbItemMatrix matrix) {
		this.matrix = matrix;
		
		List<Integer> bitIdList = Util.newList();
		bitIdList.addAll(matrix.bitIds());
		Collections.sort(bitIdList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < bitIdList.size(); i++) {
			int rowBitId = bitIdList.get(i);
			Vector<Object> row = Util.newVector();
			
			Pair pair = this.matrix.getPair(rowBitId);
			int realId = pair.key();
			int realValue = (int)pair.value();
			row.add("Item" + TextParserUtil.CONNECT_SEP + realId + TextParserUtil.CONNECT_SEP + realValue);
			
			for (int j = 0; j < bitIdList.size(); j++) {
				int colBitId = bitIdList.get(j);
				double prob = this.matrix.get(rowBitId, colBitId);
				
				row.add(MathUtil.format(prob));
			}
			
			data.add(row);
		}
		
		Vector<String> columns = toBitColumns(bitIdList);
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * Creating table column names.
	 * @param bitIdList list of bit item id (s).
	 * @return bit column names
	 */
	private Vector<String> toBitColumns(List<Integer> bitIdList) {
		Vector<String> columns = Util.newVector();
		columns.add("");
		for (int bitId : bitIdList) {
			Pair pair = matrix.getPair(bitId);
			int realId = pair.key();
			int realValue = (int)pair.value();
			columns.add("Item" + TextParserUtil.CONNECT_SEP + realId + TextParserUtil.CONNECT_SEP + realValue);
		}
		return columns;
	}
	
	
}
