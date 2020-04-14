/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;

/**
 * This class is a map of general-type elements but it represents a (rating) vector having identifier.
 * @author Loc Nguyen
 * @version 10.0
 * @param <T> general type.
 */
public class MapVector<T> implements Serializable {


	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Identifier of this vector.
	 */
	protected int id = -1;
	
	
	/**
	 * Internal map.
	 */
	protected Map<Integer, T> map = Util.newMap();
	

	/**
	 * Default constructor.
	 */
	public MapVector() {
	}
	
	
	/**
	 * Constructor with identifier.
	 * @param id specified idenifier.
	 */
	public MapVector(int id) {
		this.id = id;
	}
	
	
	/**
	 * Getting identifier.
	 * @return identifier of this vector.
	 */
	public int id() {
		return id;
	}
	
	
	/**
	 * Setting identifier.
	 * @param id identifier of this vector.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Getting size of vector.
	 * @return size of vector.
	 */
	public int size() {
		return map.size();
	}
	
	
	/**
	 * Method is used to get rating at the key
	 * @param fieldId field identifier.
	 * @return key contain in the map.
	 */
	public T get(int fieldId) {
		return map.get(fieldId);
	}
	
	
	/**
	 * Getting collection of values.
	 * @return collection of values
	 */
	public Collection<T> gets() {
		return map.values();
	}
	
	
	/**
	 * Method is used to put a (rating) value at field identifier.
	 * @param fieldId specified field identifier.
	 * @param value specified (rating) value.
	 */
	public void put(int fieldId, T value) {
		map.put(fieldId, value);
	}
	
	
	/**
	 * To remove rating at specified field identifier.
	 * @param fieldId specified field identifier.
	 */
	public void remove(int fieldId) {
		map.remove(fieldId);
	}

	
	/**
	 * To remove (rating) values at specified field identifiers.
	 * @param fieldIds collection of specified field identifiers.
	 */
	public void remove(Collection<Integer> fieldIds) {
		 for (Integer fieldId : fieldIds) {
			 map.remove(fieldId);
		 }
	}
	
	
	/**
	 * Clearing this vector.
	 */
	public void clear() {
		map.clear();
	}
	
	
	/**
	 * Setting up this vector by a specified map of values.
	 * @param map specified map.
	 */
	protected void setup(Map<Integer, T> map) {
		clear();
		this.map.putAll(map);
	}
	
	
	/**
	 * This method is used to retrieve keys (fields).
	 * @return key contained in the map.
	 */
	public Set<Integer> fieldIds() {
		return map.keySet();
	}


	/**
	 * Getting list of value according to specified pattern.
	 * @param fieldPattern specified pattern.
	 * @return list of value according to pattern
	 */
	public List<T> toList(List<Integer> fieldPattern) {
		List<T> list = Util.newList();
		for (int field : fieldPattern) {
			list.add(map.get(field));
		}
		return list;
	}

	
	/**
	 * Testing whether this vector contains specified field id.
	 * @param fieldId specified field id.
	 * @return whether contains field id
	 */
	public boolean contains(int fieldId) {
		return map.containsKey(fieldId);
	}

	
	/**
	 * Converting this vector into text by specified row and column.
	 * @param rowName specified row.
	 * @param columnName specified column.
	 * @return text presentation of this vector.
	 */
	public String toString(String rowName, String columnName) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(rowName + " " + id + ":" + " ");
		List<Integer> fieldIds = Util.newList();
		fieldIds.addAll(fieldIds());
		Collections.sort(fieldIds);
		
		int count = 0;
		for (int i = 0; i < fieldIds.size(); i++) {
			if (count > 0)
				buffer.append(", ");
			
			int fieldId = fieldIds.get(i);
			Object value = get(fieldId);
			String svalue = value == null ? "" : value.toString();
			if (svalue.isEmpty())
				continue;
			
			buffer.append(columnName + " " + fieldId + "=" + value);
			
			count ++;
		}
		
		
		return buffer.toString();
		
	}
	
	
}
