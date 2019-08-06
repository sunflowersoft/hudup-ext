/**
 * 
 */
package net.hudup.data.bit;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Util;

/**
 * This class represent a bit rating matrix.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitMatrix implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Internal bit rating matrix.
	 */
	public byte[][] matrix = new byte[0][];
	
	
	/**
	 * List of row identifiers.
	 */
	public List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * List of column identifiers.
	 */
	public List<Integer> columnIdList = Util.newList();

	
	/**
	 * Assigning (initializing) a bit rating matrix by specified byte rating matrix, list of row identifiers, and list of column identifiers.
	 * @param matrix specified byte rating matrix.
	 * @param rowIdList specified list of row identifiers.
	 * @param columnIdList specified list of column identifiers.
	 * @return {@link BitMatrix}.
	 */
	public static BitMatrix assign(byte[][] matrix, List<Integer> rowIdList, List<Integer> columnIdList) {
		BitMatrix bMatrix = new BitMatrix();
		bMatrix.matrix = matrix;
		bMatrix.rowIdList = rowIdList;
		bMatrix.columnIdList = columnIdList;
		
		return bMatrix;
	}
	
	
}
