package net.hudup.logistic.math.ui;

import javax.swing.JTable;

import net.hudup.data.bit.BitData;
import net.hudup.logistic.math.BitProbItemMatrix;


/**
 * This is Java table to show binary probability matrix {@link BitProbMatrixTable}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BitProbMatrixTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BitProbMatrixTable() {
		super(new BitProbMatrixTableModel());
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * Update table by binary probability matrix.
	 * @param matrix binary probability matrix.
	 */
	public void update(BitProbItemMatrix matrix) {
		getMatrixModel().update(matrix);
	}
	
	
	/**
	 * Update table by binary data.
	 * @param bitData bit data (binary data).
	 */
	public void update(BitData bitData) {
		BitProbItemMatrix matrix = new BitProbItemMatrix();
		matrix.setup(bitData);
		
		update(matrix);
	}
	
	
	/**
	 * Getting model of this table.
	 * @return {@link BitProbMatrixTableModel} as model of this table.
	 */
	public BitProbMatrixTableModel getMatrixModel() {
		return (BitProbMatrixTableModel)getModel();
	}
	
	
}
