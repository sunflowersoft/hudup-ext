/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.Container;
import java.util.Collection;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;

/**
 * The graphic user interface (GUI) component as list box {@link JList} shows the {@link UnitList}.
 * Note, {@link UnitList} represents a list of many units; as a convention, it is called unit list.
 * Recall objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc. Unit is represented by {@link Unit} class.
 * Your attention please, the list of default units is returned by the static method {@link DataConfig#getDefaultUnitList()}.
 * In fact, important units such as rating unit, user profile, item profile, context are registered with {@link DataConfig} class as default units.
 * <br><br>
 * This {@link UnitListBox} firstly connects with the store (directory or database) specified in the internal configuration {@link #config} so as to retrieve list of units (retrieved {@link UnitList}) from such store.
 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
 * Finally, this {@link UnitListBox} shows the retrieved {@link UnitList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitListBox extends JList<Unit> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor.
	 */
	public UnitListBox() {
		
	}
	
	
	/**
	 * This method firstly connects with the store (directory or database) specified in the specified configuration so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
	 * Finally, retrieved {@link UnitList} are shown in this {@link UnitListBox}.
	 * @param config specified configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * @return whether connect successfully.
	 */
	public boolean connectUpdate(DataConfig config) {
		this.config = config;
		
		UnitList unitList = new UnitList();
		boolean connect = UnitList.connectUnitList(config, unitList);
		
		update(unitList);
		return connect;
	}
	
	
	/**
	 * Update this {@link UnitListBox} by specified {@link UnitList}.
	 * @param unitList specified {@link UnitList}.
	 */
	public void update(UnitList unitList) {
		setListData(unitList.toArray());
		
		Container container = getParent();
		if (container instanceof JPanel)
			((JPanel)container).updateUI();
	}
	
	
	/**
	 * Get the list of units from this {@link UnitListBox}.
	 * @return {@link UnitList} of this {@link UnitListBox}.
	 */
	public UnitList getUnitList() {
		UnitList unitList = new UnitList();
		
		ListModel<Unit> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			unitList.add(model.getElementAt(i));
		}
	
		return unitList;
	}
	
	
	/**
	 * Getting list of selected units.
	 * @return selected unit list.
	 */
	public UnitList getSelectedList() {
		List<Unit> list = getSelectedValuesList();
		UnitList unitList = new UnitList();
		for (Object object : list) {
			unitList.add((Unit)object);
		}
		return unitList;
	}

	
	/**
	 * Adding a specified unit into this {@link UnitListBox}.
	 * @param unit specified unit.
	 */
	public void add(Unit unit) {
		UnitList unitList = getUnitList();
		unitList.add(unit);
		update(unitList);
	}
	
	
	/**
	 * Adding a specified collection of units into this list box.
	 * @param units specified collection of units.
	 */
	public void addAll(Collection<Unit> units) {
		UnitList unitList = getUnitList();
		unitList.addAll(units);
		update(unitList);
	}

	
	/**
	 * Adding a specified list of units into this {@link UnitListBox}.
	 * @param list specified list of units as {@link UnitList}.
	 */
	public void addAll(UnitList list) {
		UnitList unitList = getUnitList();
		unitList.addAll(list);
		update(unitList);
	}

	
	/**
	 * Removing a unit from this {@link UnitListBox}.
	 * @param unit specified unit which is removed from this {@link UnitListBox}. 
	 */
	public void remove(Unit unit) {
		UnitList unitList = getUnitList();
		unitList.remove(unit.getName());
		update(unitList);
	}
	
	
	/**
	 * Removing units in the specified collection from this {@link UnitListBox}.
	 * @param units specified collection of units which are removed from this {@link UnitListBox}.
	 */
	public void removeAll(Collection<Unit> units) {
		UnitList unitList = getUnitList();
		unitList.removeAll(units);
		update(unitList);
	}

	
	/**
	 * Removing units in the specified {@link UnitList} from this {@link UnitListBox}.
	 * @param list specified {@link UnitList} whose units are removed from this {@link UnitListBox}.
	 */
	public void removeAll(UnitList list) {
		UnitList unitList = getUnitList();
		unitList.removeAll(list);
		
		update(unitList);
	}

	
	/**
	 * Removing selected units.
	 * @return list of removed units.
	 */
	public UnitList removeSelectedList() {
		UnitList selected = getSelectedList();
		removeAll(selected);
		return selected;
	}

	
	/**
	 * Clearing this list box, which means that making this list box empty.
	 */
	public void clear() {
		update(new UnitList());
	}
	
	
}
