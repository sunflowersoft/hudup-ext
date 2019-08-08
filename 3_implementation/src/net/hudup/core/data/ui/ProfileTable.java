package net.hudup.core.data.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class is table for showing a collection of profiles.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class ProfileTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with a collection of profiles.
	 * @param profiles collection of profiles.
	 */
	public ProfileTable(Collection<Profile> profiles) {
		super(new ProfileTableModel(profiles));
		initGUI();
	}

	
	/**
	 * Constructor with a fetcher of profiles.
	 * @param fetcher fetcher of profiles.
	 */
	public ProfileTable(Fetcher<Profile> fetcher) {
		super(new ProfileTableModel(fetcher));
		initGUI();
	}
	
	
	/**
	 * Initializing the table.
	 */
	protected void initGUI() {
		this.getProfileTableModel().setEditable(false);

		setAutoResizeMode(AUTO_RESIZE_OFF);
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				ProfileTableModel model = getProfileTableModel();
				if(!model.isEditable()) return;
				
				ActionEvent evt = null;
				if(e.getKeyCode() == KeyEvent.VK_DELETE)
					evt = new ActionEvent(model, 0, "Remove");
				else if(e.getKeyCode() == KeyEvent.VK_F5)
					evt = new ActionEvent(model, 0, "Refresh");
				
				if (evt != null) actionPerformed(evt);
			}
		});
	}
	
	
	/**
	 * Getting profile table.
	 * @return profile table model.
	 */
	ProfileTableModel getProfileTableModel() {
		return (ProfileTableModel)getModel();
	}
	
	
	/**
	 * Update table with specified collection of profiles.
	 * @param profiles specified collection of profiles.
	 */
	public void update(Collection<Profile> profiles) {
		getProfileTableModel().update(profiles);
	}

	
	/**
	 * Update table with specified fetcher of profiles.
	 * @param profiles specified fetcher of profiles.
	 */
	public void update(Fetcher<Profile> profiles) {
		getProfileTableModel().update(profiles);
	}
	
	
	/**
	 * Performing action.
	 * @param e action event.
	 */
	protected void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if(cmd.equals("Add")) {
			
		}
		else if(cmd.equals("Remove")) {
			int selectedRow = getSelectedRow();
			if(selectedRow != -1)
				getProfileTableModel().removeRow(selectedRow);
		}
		else if(cmd.equals("Refresh")) {
			
		}
	}

	
	/**
	 * Getting whether being editable.
	 * @return whether being editable.
	 */
	public boolean isEditable() {
		return getProfileTableModel().isEditable();
	}
	
	
	/**
	 * Getting whether being editable.
	 * @param editable editable flag.
	 */
	public void setEditable(boolean editable) {
		getProfileTableModel().setEditable(editable);
	}

	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellRenderer(row, column);
		else {
			TableCellRenderer renderer = getDefaultRenderer(value.getClass());
			if(renderer == null)
				return super.getCellRenderer(row, column);
			else
				return renderer;
		}
	}
	
	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellEditor(row, column);
		else {
			TableCellEditor editor = getDefaultEditor(value.getClass());
			if(editor == null)
				return super.getCellEditor(row, column);
			else
				return editor;
		}
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
@NextUpdate
class ProfileTableModel extends DefaultTableModel implements TableModelListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal data as list of profiles
	 */
	protected List<Profile> data = Util.newList();
	
	
	/**
	 * Internal attribute reference.
	 */
	protected AttributeList attRef = null;
	
	
	/**
	 * Editable flag.
	 */
	protected boolean editable = true;
	
	
	/**
	 * Constructor with collection of profiles.
	 * @param profiles collection of profiles.
	 */
	public ProfileTableModel(Collection<Profile> profiles) {
		super();
		update(profiles);
	}

	
	/**
	 * Constructor with fetcher of profiles.
	 * @param fetcher fetcher of profiles.
	 */
	public ProfileTableModel(Fetcher<Profile> fetcher) {
		super();
		update(fetcher);
	}
	
	
	/**
	 * Updating this model by the specified collection of profiles.
	 * @param profiles specified collection of profiles.
	 */
	public void update(Collection<Profile> profiles) {
		this.data.clear();
		this.data.addAll(profiles);
		this.attRef = null;
		
		Vector<Vector<Object>> dataVector = Util.newVector();
		AttributeList attributes = null;

		for (Profile profile : profiles) {
			if (profile == null)
				continue;
			if (attributes == null)
				attributes = profile.getAttRef();
			
			int n = profile.getAttCount();
			Vector<Object> row = Util.newVector();
			for (int i = 0; i < n; i++) {
				row.add(profile.getValue(i));
			}
			dataVector.add(row);
		}
		
		this.attRef = attributes;
		if (attributes == null) this.data.clear();
		setDataVector(
				dataVector, 
				toColumns(attributes));
	}

	
	/**
	 * Updating this model by the specified fetcher of profiles.
	 * @param fetcher specified fetcher of profiles.
	 */
	public void update(Fetcher<Profile> fetcher) {
		List<Profile> profiles = Util.newList();
		FetcherUtil.fillCollection(profiles, fetcher, false);
		try {
			fetcher.reset();;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		update(profiles);
		profiles.clear();
	}
	
	
	/**
	 * Getting internal data.
	 * @return internal data.
	 */
	public List<Profile> getData() {
		return this.data;
	}
	
	
	/**
	 * Getting attribute list.
	 * @return attribute list.
	 */
	public AttributeList getAttributeList() {
		return this.attRef;
	}
	
	
	/**
	 * Extracting data.
	 * @return data as list of profiles.
	 */
	protected void updateInternalData() {
		this.data.clear();
		if (this.attRef == null)
			return;
		
		Vector<?> vectors = getDataVector();
		for (int i = 0; i < vectors.size(); i++) {
			Vector<?> vector = (Vector<?>)vectors.get(i);
			Profile profile = new Profile(this.attRef);
			for (int j = 0; j < vector.size(); j++) {
				Object value = vector.get(j);
				profile.setValue(j, value);
			}
			
			this.data.add(profile);
		}
	}
	
	
	/**
	 * Getting whether being editable.
	 * @return whether being editable.
	 */
	public boolean isEditable() {
		return editable;
	}
	
	
	/**
	 * Getting whether being editable.
	 * @param editable editable flag.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return isEditable();
	}


	@NextUpdate
	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		updateInternalData(); //This code line is not optimized, which is a work-around solution.
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



