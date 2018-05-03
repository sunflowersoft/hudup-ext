package net.hudup.core.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;


/**
 * This class represents a list of many units. As a convention, it is called unit list.
 * It provides utility methods processing on a list of unit such as getting unit, adding unit, removing unit.
 * It owns an internal variable {@link #unitList} containing all units.
 * Recall objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc. Unit is represented by {@link Unit} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitList {
	
	
	/**
	 * Internal variable {@link #unitList} contains all units.
	 */
	protected List<Unit> unitList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public UnitList() {
		
	}
	
	
	/**
	 * Getting the size of unit list.
	 * @return size of unit list.
	 */
	public int size() {
		return unitList.size();
	}
	
	
	/**
	 * Looking up a unit inside this list via unit name.
	 * 
	 * @param unitName specified unit name.
	 * @return index of the unit having specified name in this unit list. If there is no such unit, the method returns -1.
	 */
	public int indexOf(String unitName) {
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			
			if (unit.getName().equals(unitName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Getting a unit at specified index.
	 * @param index specified index.
	 * @return unit at specified index.
	 */
	public Unit get(int index) {
		return unitList.get(index);
	}
	
	
	/**
	 * Clearing this unit list, which means that all units are removed from this list.
	 */
	public void clear() {
		unitList.clear();
	}
	
	
	/**
	 * Adding the specified unit into this list.
	 * @param unit specified unit.
	 * @return whether add successfully.
	 */
	public boolean add(Unit unit) {
		if (contains(unit))
			return false;
		else
			return unitList.add(unit);
	}
	
	
	/**
	 * Converting the specified name into a unit and adding such unit into this list.
	 * @param unitName specified unit name.
	 * @return whether add successfully.
	 */
	public boolean add(String unitName) {
		return add(new Unit(unitName));
	}
	
	
	
	/**
	 * Adding all units of the specified list into this list.
	 * @param unitList specified unit list.
	 */
	public void addAll(UnitList unitList) {
		addAll(unitList.unitList);
	}

	
	
	/**
	 * Adding all units of the specified collection into this list.
	 * @param unitList specified collection of units.
	 */
	public void addAll(Collection<Unit> unitList) {
		for (Unit unit : unitList) {
			add(unit);
		}
	}
	
	
	/**
	 * Converting the specified collection of names into the collection of units and then adding all units of such collection into this list.
	 * @param unitNameList specified collection of names.
	 */
	public void addNameList(Collection<String> unitNameList) {
		for (String unitName : unitNameList) {
			add(unitName);
		}
	}
	
	
	/**
	 * Removing the unit at specified index.
	 * @param index specified index.
	 */
	public void remove(int index) {
		unitList.remove(index);
	}
	
	
	/**
	 * Removing the unit that has the specified name from this list.
	 * @param unitName specified name.
	 */
	public void remove(String unitName) {
		int index = indexOf(unitName);
		if (index != -1)
			remove(index);
	}
	
	
	/**
	 * Removing the specified unit from this list.
	 * @param unit specified unit.
	 */
	public void remove(Unit unit) {
		if (unit != null)
			remove(unit.getName());
	}

	
	/**
	 * Removing all units of specified collection from this list.
	 * @param units specified collection of units.
	 */
	public void removeAll(Collection<Unit> units) {
		for (Unit unit : units)
			remove(unit);
	}
	
	
	/**
	 * Removing all units of specified list from this list.
	 * @param unitList specified unit list.
	 */
	public void removeAll(UnitList unitList) {
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			remove(unit);
		}
	}

	
	/**
	 * Checking whether or not this list contains the specified unit.
	 * @param unit specified unit.
	 * @return whether or not this list contains the specified unit.
	 */
	public boolean contains(Unit unit) {
		if (unit == null)
			return false;
		else
			return unitList.contains(unit);
	}
	
	
	
	/**
	 * Checking whether or not this list contains the unit that as the specified name.
	 * @param unitName specified name.
	 * @return whether or not this list contains the unit that as the specified name.
	 */
	public boolean contains(String unitName) {
		for (Unit unit : unitList) {
			if (unit.getName().equals(unitName))
				return true;
		}
		
		return false;
	}
	
	
	/**
	 * Checking whether or not this list contains units of specified list.
	 * @param unitList specified unit list.
	 * @return whether or not this list contains units of specified list.
	 */
	public boolean contains(UnitList unitList) {
		for (Unit unit : unitList.unitList) {
			
			if (!contains(unit))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Converting this unit list into a {@link List} of units.
	 * @return converted {@link List} of units.
	 */
	public List<Unit> getList() {
		return unitList;
	}
	
	
	/**
	 * Converting this unit list into an array of units.
	 * @return converted array of units.
	 */
	public Unit[] toArray() {
		return unitList.toArray(new Unit[0]);
	}
	
	
	/**
	 * Getting names of all units inside this list as a {@link List}.
	 * @return {@link List} of unit names.
	 */
	public List<String> toNameList() {
		List<String> list = Util.newList();
		for (Unit unit : unitList) {
			list.add(unit.getName());
		}
		return list;
	}

	
	/**
	 * Getting list of main (essential, important) units whose {@link Unit#extra} (s) are false.
	 * @return list of main unit.
	 */
	public UnitList getMainList() {
		UnitList list = new UnitList();
		
		for (Unit unit : unitList) {
			if (!unit.isExtra())
				list.add(unit);
		}
		return list;
	}
	
	
	/**
	 * Getting list of extra (auxiliary) units whose {@link Unit#extra} (s) are true.
	 * @return list of extra unit.
	 */
	public UnitList getExtraList() {
		UnitList list = new UnitList();
		
		for (Unit unit : unitList) {
			if (unit.isExtra())
				list.add(unit);
		}
		return list;
	}

	
	/**
	 * Setting all units of this list to be essential of extra according to the input parameter {@code extra}.
	 * @param extra if {@code true}, units are set to be extra (auxiliary) units. If {@code false}, units are set to be essential (important, main) units.
	 */
	public void setExtra(boolean extra) {
		for (Unit unit : unitList) {
			unit.setExtra(extra);
		}
	}
	
	
	/**
	 * This method firstly connects with the store (directory or database) specified in the specified configuration so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
	 * Finally, retrieved {@link UnitList} are put in the output parameter {@code unitList}.
	 * @param config specified configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * @param unitList this is output parameter that receives retrieved units.
	 * @return whether connected.
	 */
	public static boolean connectUnitList(DataConfig config, UnitList unitList) {
		unitList.clear();
		xURI uri = config.getStoreUri();
		
		boolean result = false;
		
		DataDriver driver = DataDriver.create(config.getStoreUri());
		if (driver.isFlatServer()) {
			UriAdapter adapter = new UriAdapter(config);
			xURI store = adapter.isStore(uri) ? uri : adapter.getStoreOf(uri);
			
			List<xURI> uriList = adapter.getUriList(store, null);
			
			for (xURI u : uriList) {
				String lastname = u.getLastName();
				if (lastname != null && !lastname.isEmpty()) {
					Unit unit = new Unit(lastname);
					unitList.add(unit);
				}
			}
			
			adapter.close();
			result = true;
		}
		else if (driver.isDbServer()) {
			
			Connection conn = null;
			ResultSet rs = null;
			try {
				conn = createConnection(config);
				
				DatabaseMetaData metadata = conn.getMetaData();
				rs = metadata.getTables(null, null, null, new String[] {"TABLE"});
				
				while (rs.next()) {
					Unit unit = new Unit(rs.getString("TABLE_NAME"));
					unitList.add(unit);
				}
				
				result = true;
			}
			catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
			finally {
				try {
					if (rs != null)
						rs.close();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					if (conn != null)
						conn.close();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		else if (driver.isHudupServer()) {
			
			if (driver.getType() == DataType.hudup_rmi) {
				Service service = net.hudup.core.client.DriverManager.getRemoteService(
						uri.getHost(),
						uri.getPort(),
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				result = (service != null);
			}
			else if (driver.getType() == DataType.hudup_socket) {
				SocketConnection connection = net.hudup.core.client.DriverManager.getSocketConnection(
						uri,
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				result = (connection != null && connection.isConnected());
				if (connection != null)
					connection.close();
			}
				
		}
		
		
		UnitList defaultUnitList = DataConfig.getDefaultUnitList();
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			if (!defaultUnitList.contains(unit))
				unit.setExtra(true);
		}
		
		return result;
	}
	
	
	/**
	 * Creating database connection with given configuration.
	 * Connection information such as connection URI of store, user name, and password is stored in such configuration.
	 *  
	 * @param config specified connection.
	 * @return database connection.
	 */
	public static Connection createConnection(DataConfig config) {
		String username = config.getStoreAccount();
		HiddenText password = config.getStorePassword();
		
		try {
			
			String url = config.getStoreUri().toString();
			if (username == null || password == null)
				return DriverManager.getConnection(url);
			else
				return DriverManager.getConnection(url, username, password.getText());
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
