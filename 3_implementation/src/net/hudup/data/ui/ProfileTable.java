package net.hudup.data.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;


/**
 * This class is table for showing a collection of profiles.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProfileTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with a collection of profiles.
	 * @param profiles a fetcher as a collection of profiles.
	 */
	public ProfileTable(Fetcher<Profile> profiles) {
		super(new ProfileTableModel(profiles));
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * Update table with specified fetcher of profiles.
	 * @param profiles specified fetcher of profiles.
	 */
	public void update(Fetcher<Profile> profiles) {
		getProfileTableModel().update(profiles);
	}
	
	
	/**
	 * Getting profile table.
	 * @return profile table model.
	 */
	ProfileTableModel getProfileTableModel() {
		return (ProfileTableModel)getModel();
	}
	
	
}


/**
 * This class is table model for a collection of profiles.
 * Such collection is represented by a fetcher of profiles.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class ProfileTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with fetcher of profiles.
	 * 
	 * @param profiles fetcher of profiles.
	 */
	public ProfileTableModel(Fetcher<Profile> profiles) {
		super();
		update(profiles);
	}
	
	
	/**
	 * Updating this model by the specified fetcher of profiles.
	 * 
	 * @param profiles specified fetcher of profiles.
	 */
	public void update(Fetcher<Profile> profiles) {
		Vector<Vector<Object>> data = Util.newVector();
		
		AttributeList attributes = null;
		try {
			while (profiles.next()) {
				Profile profile = profiles.pick();
				if (profile == null)
					continue;
				if (attributes == null)
					attributes = profile.getAttRef();
				
				int n = profile.getAttCount();
				Vector<Object> row = Util.newVector();
				for (int i = 0; i < n; i++) {
					row.add(profile.getValue(i));
				}
				data.add(row);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				profiles.reset();;
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		setDataVector(
				data, 
				toColumns(attributes));
		
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Converting the specified attribute list to vector of strings.
	 * @param attributes specified attribute list
	 * @return column identifiers as vector of strings.
	 */
	static Vector<String> toColumns(AttributeList attributes) {
		Vector<String> columns = Util.newVector();
		if (attributes == null)
			return columns;
		
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			columns.add(attribute.getName());
		}
		
		return columns;
	}
	
	
}



