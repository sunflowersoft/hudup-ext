/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import net.hudup.core.alg.AlgDesc2List;

/**
 * This is the map that contains descriptions of algorithms in plug-in storage.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PluginAlgDesc2ListMap implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The map contains descriptions of algorithms in plug-in storage.
	 */
	protected Map<String, AlgDesc2List> algDescListMap = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public PluginAlgDesc2ListMap() {

	}

	
	/**
	 * Getting names of registered tables.
	 * @return names of registered tables.
	 */
	public Set<String> regNames() {
		return algDescListMap.keySet();
	}
	
	
	/**
	 * Getting descriptions list of registered table specified by its name. 
	 * @param regName name of registered table.
	 * @return descriptions list of registered table specified by its name.
	 */
	public AlgDesc2List get(String regName) {
		return algDescListMap.get(regName);
	}
	
	
	/**
	 * Getting descriptions list of next update list. 
	 * @return descriptions list of next update list.
	 */
	public AlgDesc2List getNextUpdateList() {
		return get(PluginStorage.NEXT_UPDATE_LIST);
	}

	
	/**
	 * Putting descriptions list of registered table. 
	 * @param regName name of registered table.
	 * @param algDesc2List descriptions list of registered table.
	 * @return the old descriptions list.
	 */
	public AlgDesc2List put(String regName, AlgDesc2List algDesc2List) {
		return algDescListMap.put(regName, algDesc2List);
	}
	
	
	/**
	 * Removing descriptions list of registered table. 
	 * @param regName name of registered table.
	 * @return the old descriptions list.
	 */
	public AlgDesc2List remove(String regName) {
		return algDescListMap.remove(regName);
	}

	
	/**
	 * Getting the size of this map.
	 * @return the size of this map.
	 */
	public int size() {
		return algDescListMap.size();
	}
	

}
