/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;

/**
 * Table to show user/item profiles.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UserProfileTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with flag of whether user profile is used.
	 * @param user if true, user profile is used.
	 */
	public UserProfileTable(boolean user) {
		super(new UserProfileTableModel(user));
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * Update table by specified dataset.
	 * @param dataset specified dataset.
	 */
	public void update(Dataset dataset) {
		getProfileTableModel().update(dataset);
	}
	
	
	/**
	 * Getting model for this table.
	 * @return model of this table.
	 */
	UserProfileTableModel getProfileTableModel() {
		return (UserProfileTableModel) getModel();
	}
	
	
}


/**
 * Model of user/item profile.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class UserProfileTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Flag of whether user profile or item profile is used.
	 */
	protected boolean user = false;
	
	
	/**
	 * Constructor with flag of whether user profile or item profile is used.
	 * @param user flag of whether user profile or item profile is used.
	 */
	public UserProfileTableModel(boolean user) {
		super();
		
		this.user = user;
	}
	
	
	/**
	 * Update this model by specified dataset.
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
				
				Profile profile = user ? dataset.getUserProfile(id) : dataset.getItemProfile(id);
				if (profile == null)
					continue;
				
				int n = profile.getAttCount();
				Vector<Object> row = Util.newVector();
				for (int i = 0; i < n; i++) {
					row.add(profile.getValue(i));
				}
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
		
		
		setDataVector(
				data, 
				toColumns(user ? dataset.getUserAttributes() : dataset.getItemAttributes()));
		
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Create columns by specified attributes.
	 * @param attributes specified attributes.
	 * @return column identifiers.
	 */
	static Vector<String> toColumns(AttributeList attributes) {
		Vector<String> columns = Util.newVector();
		
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			columns.add(attribute.getName());
		}
		
		return columns;
	}
	
	
}



