/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is Java table for external record.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalRecordTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with user-item flag.
	 * @param user user-item flag. If true, the record is user external record.
	 */
	public ExternalRecordTable(boolean user) {
		super(new ExternalRecordTableModel(user));
	}
	
	
	/**
	 * Update the table with specified dataset.
	 * @param dataset specified dataset.
	 */
	public void update(Dataset dataset) {
		getExternalRecordTableModel().update(dataset);
	}
	
	
	/**
	 * Getting the model of this table.
	 * @return external record table model of this table.
	 */
	ExternalRecordTableModel getExternalRecordTableModel() {
		return (ExternalRecordTableModel) getModel();
	}
	
	
}



/**
 * This class is table model for external record.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class ExternalRecordTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * User-item flag. If it is true, this is user table model.
	 */
	protected boolean user = false;
	
	
	/**
	 * Constructor with user-item flag.
	 * @param user User-item flag. If it is true, this is user table model.
	 */
	public ExternalRecordTableModel(boolean user) {
		super();
		
		this.user = user;
	}
	
	
	/**
	 * Updating this model with specified dataset.
	 * @param dataset specified dataset.
	 */
	public void update(Dataset dataset) {
		Vector<Vector<Object>> data = Util.newVector();
		
		Fetcher<Integer> fetcher = user ? dataset.fetchUserIds() : dataset.fetchItemIds();
		try {
			while (fetcher.next()) {
				Integer id = fetcher.pick();
				if (id == null || id < 0)
					continue;
				
				ExternalRecord record = user ? dataset.getUserExternalRecord(id) : 
					dataset.getItemExternalRecord(id);
				if (record == null)
					continue;
				
				Vector<Object> row = Util.newVector();
				row.add(id);
				row.add(record.unit);
				row.add(record.attribute);
				row.add(record.value);
				data.add(row);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				fetcher.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				LogUtil.trace(e);
			}
		}
		
		
		setDataVector(data, toColumn(user));
		
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Converting this external record into to vector of texts.
	 * @param user User-item flag. If it is true, this is user table model.
	 * @return vector of texts.
	 */
	static Vector<String> toColumn(boolean user) {
		Vector<String> columns = Util.newVector();
		columns.add(user ? "User id" : "Item id");
		columns.add("External unit");
		columns.add("External attribute");
		columns.add("External key");
		
		return columns;
	}
	
	
}



