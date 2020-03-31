/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.awt.Component;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc;
import net.hudup.core.alg.AlgDescList;
import net.hudup.core.alg.AlgList;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This utility class stores dictionary of entries. It is called properties list.
 * Each entry represents a property, which is a pair of key and value.
 * @author Loc Nguyen
 * @version 10.0
 */
public class PropList implements TextParsable, Serializable, Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The name of the key whose values are read-only keys.
	 * For example, if the value associated of READ_ONLY_KEYS is &quot;minRating, maxRating&quot;,
	 * the values of two keys &quot;minRating&quot;, &quot;maxRating&quot; are read-only.
	 */
	private static final String READ_ONLY_KEYS = "readonlykeys";

	
	/**
	 * The name of the key whose values are invisible keys.
	 */
	private static final String INVISIBLE_KEYS = "invisiblekeys";

	
//	/**
//	 * The name of the key whose values are unsaved.
//	 */
//	private static final String UNSAVED_KEYS = "unsavedkeys";

	
	/**
	 * Name of root element of this properties list in XML format.
	 * Root element has many parameter elements.
	 */
	private static final String ELEMENT_CONFIG = "config";

	
	/**
	 * Name of parameter element of this properties list in XML format.
	 * Parameter element has key attribute, type attribute, class name attribute, value attribute.  
	 */
	private static final String ELEMENT_PARAM = "param";

	
	/**
	 * Name of key attribute in parameter element.
	 */
	private static final String ELEMENT_KEY = "key";

	
	/**
	 * Name of type attribute in parameter element.
	 */
	private static final String ELEMENT_TYPE = "type";

	
	/**
	 * Name of class name attribute in parameter element.
	 */
	private static final String ELEMENT_CLASSNAME = "classname";

	
	/**
	 * Name of value attribute in parameter element.
	 */
	private static final String ELEMENT_VALUE = "value";

	
	/**
	 * The suffix of value of key attribute in case of list of algorithm.
	 */
	private static final String ELEMENT_ALGORITHM = "alg";

	
	/**
	 * This is the main variable of this class. It is dictionary (map) of entries.
	 * Each entry represents a property, which is a pair of key and value.
	 * Most of built-in methods operates on this variable.
	 */
	private Map<String, Serializable> propMap = Util.newMap();
	
	
	/**
	 * The set of read-only keys.
	 * For example, if this set contains &quot;minRating, maxRating&quot;,
	 * the values of two keys &quot;minRating&quot;, &quot;maxRating&quot; are read-only.
	 */
	private Set<String> readOnlyKeys = Util.newSet();
	
	
	/**
	 * The set of invisible keys.
	 */
	private Set<String> invisibleKeys = Util.newSet();

	
//	/**
//	 * The set of unsaved keys.
//	 */
//	private Set<String> unsavedKeys = Util.newSet();

	
	/**
	 * Specifying extended types of values in {@link PropList}.
	 * <ul>
	 * <li>{@code hidden}: {@link HiddenText}</li>
	 * <li>{@code cdata}: {@link CData}</li>
	 * <li>{@code proplist}: {@link PropList}</li>
	 * <li>{@code alg}: {@link Alg}</li>
	 * <li>{@code alg_list}: {@link AlgList}</li>
	 * <li>{@code alg_desc}: {@link AlgDesc}</li>
	 * <li>{@code alg_desc_list}: {@link AlgDescList}</li>
	 * <li>{@code unknown}: Unknown object</li>
	 * </ul> 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static enum ExtendedType {
		hidden, cdata, proplist, alg, alg_list, alg_desc, alg_desc_list, unknown
	};
	
	
	/**
	 * Default constructor.
	 */
	public PropList() {
//		if (!(propMap instanceof Serializable) || !(readOnlyKeys instanceof Serializable)
//				|| !(invisibleKeys instanceof Serializable)
////				|| !(unsavedKeys instanceof Serializable)
//			)
//			throw new RuntimeException("Not serializable class");
	}
	
	
	/**
	 * Constructor with specific URI. Please see {@link xURI} for more details about URI.
	 * This constructor calls {@link #load(xURI)} method in order to initialize this class from reading resource located by specific URI.
	 * @param uri specified URI.
	 */
	public PropList(xURI uri) {
		super();
		
		load(uri);
	}

	
	/**
	 * Clearing all entries.
	 */
	public void clear() {
		propMap.clear();
		readOnlyKeys.clear();
		invisibleKeys.clear();
//		unsavedKeys.clear();
	}
	
	
	/**
	 * Getting the size of properties list.
	 * @return Size of this properties list
	 */
	public int size() {
		return propMap.size();
	}
	
	
	/**
	 * Extracting set of keys over all entries.
	 * @return Set of keys
	 */
	public Set<String> keySet() {
		return propMap.keySet();
	}
	
	
	/**
	 * Extracting and sorting keys over all entries.
	 * @return Sorted list of key
	 */
	public List<String> sortedKeyList() {
		List<String> sortedKeyList = Util.newList();
		sortedKeyList.addAll(keySet());
		Collections.sort(sortedKeyList);
		
		return sortedKeyList;
	}
	
	
	/**
	 * Retrieving all values.
	 * @return Collection of values
	 */
	public Collection<Serializable> values() {
		return propMap.values();
	}
	
	
	/**
	 * Testing whether the properties list contains the specific key.
	 * @param key The specific key
	 * @return Whether contains this key
	 */
	public boolean containsKey(String key) {
		return propMap.containsKey(key);
	}
	
	
	/**
	 * Putting the pair of key and value to the properties list.
	 * @param key Specific key
	 * @param value Associated value with the key. The value must be serializable.
	 */
	public void put(String key, Serializable value) {
		value = preprocessValue(key, value);
		
		if(!validate(key, value)) {
			System.out.println("Error: Key \"" + key + "\" invalidate");
			return;
		}
		
		propMap.put(key, value); 
	}
	
	
	/**
	 * Putting entire other properties list into this list.
	 * @param propList Other properties list
	 */
	public void putAll(PropList propList) {
		Set<String> keys = propList.keySet();
		for (String key : keys) {
			put(key, propList.get(key));
		}
		
		readOnlyKeys.addAll(propList.readOnlyKeys);
		invisibleKeys.addAll(propList.invisibleKeys);
//		unsavedKeys.addAll(propList.unsavedKeys);
	}
	
	
	/**
	 * Putting entries of other properties list so that this list does not contain such entries.
	 * @param propList other properties list.
	 */
	public void putKeep(PropList propList) {
		Set<String> keys = propList.keySet();
		for (String key : keys) {
			if (!containsKey(key))
				put(key, propList.get(key));
		}
		
		for (String key : propList.readOnlyKeys) {
			if (containsKey(key))
				readOnlyKeys.add(key);
		}
		
		for (String key : propList.invisibleKeys) {
			if (containsKey(key))
				invisibleKeys.add(key);
		}
		
//		for (String key : propList.unsavedKeys) {
//			if (containsKey(key))
//				unsavedKeys.add(key);
//		}
	}

	
	/**
	 * Putting entire other Java {@link Properties} list into this list.
	 * By default, if the Specific Java {@link Properties} list contains key with name {@link #READ_ONLY_KEYS},
	 * this list will keep its values as read-only entries.
	 * @param properties Specific Java {@link Properties} list
	 */
	public void putAll(Properties properties) {
		Set<Object> keys = properties.keySet();
		for (Object key : keys) {
			Object value = properties.get(key);
			
			String thisKey = key instanceof String ? (String)key : key.toString();
			Serializable thisValue = value instanceof Serializable ? (Serializable)value : value.toString();
			
			put(thisKey, thisValue);
		}
		
		//if the Specific Java {@link Properties} list contains key with name {@link #READ_ONLY_KEYS}, this list will keep its values as read-only entries.
		addSpecialKeys(properties, READ_ONLY_KEYS, readOnlyKeys);
		
		//if the Specific Java {@link Properties} list contains key with name {@link #INVISIBLE_KEYS}, this list will keep its values as invisible entries.
		addSpecialKeys(properties, INVISIBLE_KEYS, invisibleKeys);
		
//		//if the Specific Java {@link Properties} list contains key with name {@link #NOTSAVED_KEYS}, this list will keep its values as unsaved entries.
//		addSpecialKeys(properties, UNSAVED_KEYS, unsavedKeys);
	}
	
	
	/**
	 * Adding special keys like read-only keys, invisible keys, unsaved keys.
	 * @param properties given properties.
	 * @param keySetName name of key set.
	 * @param keySet given key set.
	 */
	private void addSpecialKeys(Properties properties, String keySetName, Set<String> keySet) {
		if (properties.contains(keySetName)) {
			String keySetValue = properties.get(keySetName).toString();
			addSpecialKeys(keySetValue, keySet);
		}
	}
	
	
	/**
	 * Adding special keys like read-only keys, invisible keys, unsaved keys.
	 * @param keySetValue key set value.
	 * @param keySet given key set.
	 */
	private void addSpecialKeys(String keySetValue, Set<String> keySet) {
		List<String> textList = TextParserUtil.parseListByClass(keySetValue, String.class, ",");
		Set<String> thisKeys = keySet();
		for (String text : textList) {
			if (thisKeys.contains(text)) keySet.add(text);
		}
	}

	
	/**
	 * Getting value associated with specific key.
	 * @param key Specific key
	 * @return Value by key
	 */
	public Serializable get(String key) {
		return propMap.get(key);
	}
	
	
	/**
	 * Removing entry identified by the specific key.
	 * @param key Specific key
	 */
	public void remove(String key) {
		if (!propMap.containsKey(key)) return;

		propMap.remove(key);
		
		try {
			removeReadOnly(key);
			removeInvisible(key);
//			removeUnsaved(key);
		} catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Removing entries identified by the specific collection of keys.
	 * @param keys Specific collection of keys
	 */
	public void remove(Collection<String> keys) {
		for (String key : keys)
			remove(key);
	}
	
	
	/**
	 * Getting the class of value associated with the specified key.
	 * @param key Specified key
	 * @return Class of value associated with the specified key
	 */
	public Class<?> getClassOf(String key) {
		if (!containsKey(key))
			return null;
		else if (isStringValue(key))
			return String.class;
		else if(isBooleanValue(key))
			return Boolean.class;
		else if(isIntValue(key))
			return Integer.class;
		else if(isTimeValue(key))
			return Long.class;
		else if(isRealValue(key))
			return Double.class;
		else if(isHiddenValue(key))
			return HiddenText.class;
		else if(isCDataValue(key))
			return CData.class;
		else
			return get(key).getClass();
		
	}
	
	
	/**
	 * The method returns the modified version of the referred value so that the class of this version is the same or compatible with the value associated with the specified key.
	 * @param key Specified key
	 * @param referredValue The referred value
	 * @return Modified version of the referred value so that the class of this version is the same or compatible with the value associated with the specified key.
	 */
	public Serializable getValueOf(String key, Serializable referredValue) {
		if (!containsKey(key))
			return null;
		
		if(getClassOf(key).equals(referredValue.getClass()))
			return referredValue;
		
		String sValue = referredValue.toString();
		if (isStringValue(key))
			return sValue;
		else if(isBooleanValue(key))
			return Boolean.parseBoolean(sValue);
		else if(isIntValue(key))
			return Integer.parseInt(sValue);
		else if(isTimeValue(key))
			return Long.parseLong(sValue);
		else if(isRealValue(key))
			return Double.parseDouble(sValue);
		else if(isHiddenValue(key))
			return new HiddenText(sValue);
		else if(isCDataValue(key))
			return new CData(sValue);
		else
			return referredValue;
	}
	
	
	/**
	 * Getting value associated with the specified key as a string.
	 * @param key Specified key
	 * @return Value as string
	 */
	public String getAsString(String key) {
		if (!containsKey(key)) return null;

		Serializable value = propMap.get(key);
		if (value instanceof String)
			return (String)value;
		else
			return value.toString();
	}
	
	
	/**
	 * Getting value associated with the specified key as a real number.
	 * @param key Specified key
	 * @return Value as real number
	 */
	public double getAsReal(String key) {
		if (!containsKey(key))
			return Constants.UNUSED;
		
		if (isRealValue(key) || isIntValue(key) || isTimeValue(key))
			return ((Number) get(key)).doubleValue();
		else
			return Double.parseDouble(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a boolean value.
	 * @param key Specified key
	 * @return Value as boolean
	 */
	public boolean getAsBoolean(String key) {
		if (!containsKey(key))
			return false;
		
		if (isBooleanValue(key))
			return (Boolean) get(key);
		else
			return Boolean.parseBoolean(get(key).toString());
	}
	
	
	/**
	 * Getting value associated with the specified key as an integer.
	 * @param key Specified key
	 * @return Value as integer
	 */
	public int getAsInt(String key) {
		if (!containsKey(key))
			return -1;
		
		if (isIntValue(key) || isTimeValue(key) || isRealValue(key))
			return ((Number) get(key)).intValue();
		else
			return Integer.parseInt(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a time.
	 * @param key Specified key
	 * @return Value as time
	 */
	public long getAsTime(String key) {
		if (!containsKey(key))
			return -1;
		
		if (isIntValue(key) || isLongValue(key) || isTimeValue(key) || isRealValue(key))
			return ((Number) get(key)).longValue();
		else
			return Long.parseLong(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a long number. In Hudup framework, long number is also time in mili-seconds.
	 * @param key Specified key
	 * @return Value as a long number.
	 */
	public long getAsLong(String key) {
		if (!containsKey(key))
			return -1;
		
		if (isIntValue(key) || isLongValue(key) || isTimeValue(key) || isRealValue(key))
			return ((Number) get(key)).longValue();
		else
			return Long.parseLong(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a hidden text.
	 * @param key Specified key
	 * @return Value as hidden text
	 */
	public HiddenText getAsHidden(String key) {
		if (!containsKey(key))
			return null;
		
		if (isHiddenValue(key))
			return (HiddenText) get(key);
		else
			return new HiddenText(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a {@link CData}.
	 * Note that {@link CData} represents a fragment of XML code that is not processed by browser, called CDATA.
	 * @param key Specified key
	 * @return Value as {@link CData}
	 */
	public CData getAsCData(String key) {
		if (!containsKey(key))
			return null;
		
		if (isCDataValue(key))
			return (CData) get(key);
		else
			return new CData(get(key).toString());
	}

	
	/**
	 * Getting value associated with the specified key as a {@link PropList}.
	 * @param key Specified key
	 * @return Value as {@link PropList}
	 */
	public PropList getAsPropList(String key) {
		if (!containsKey(key))
			return null;
		
		if (isPropListValue(key))
			return (PropList) get(key);
		else
			return null;
	}
	
	
	/**
	 * Getting value associated with the specified key as a {@link Alg}.
	 * <code>Alg</code> is the most abstract interface for all algorithms.
	 * @param key Specified key
	 * @return Value as {@link Alg}
	 */
	public Alg getAsAlg(String key) {
		if (!containsKey(key))
			return null;
		
		if (isAlgValue(key))
			return (Alg) get(key);
		else
			return null;
	}

	
	/**
	 * Getting value associated with the specified key as a list of algorithms.
	 * @param key Specified key
	 * @return List of algorithms, represented by {@link AlgList}
	 */
	public AlgList getAsAlgList(String key) {
		if (!containsKey(key))
			return null;
		
		if (isAlgListValue(key))
			return (AlgList) get(key);
		else
			return null;
	}

	
	/**
	 * Getting value associated with the specified key as description of an algorithm.
	 * @param key Specified key
	 * @return Description of an algorithm, represented by {@link AlgDesc}
	 */
	public AlgDesc getAsAlgDesc(String key) {
		if (!containsKey(key))
			return null;
		
		if (isAlgDescValue(key))
			return (AlgDesc) get(key);
		else
			return null;
	}

	
	/**
	 * Getting value associated with the specified key as a list of descriptions of algorithms.
	 * @param key Specified key
	 * @return List of descriptions of algorithms, represented by {@link AlgDescList}
	 */
	public AlgDescList getAsAlgDescList(String key) {
		if (!containsKey(key))
			return null;
		
		if (isAlgDescListValue(key))
			return (AlgDescList) get(key);
		else
			return null;
	}

	
	/**
	 * Testing whether value associated with the specified key is string.
	 * @param key Specified key
	 * @return true if the value associated with the specified key is string; otherwise false
	 */
	public boolean isStringValue(String key) {
		if (!containsKey(key))
			return false;
		Serializable value = get(key);
		return (value instanceof String);
	}
	
	
	/**
	 * Testing whether value associated with the specified key is integer.
	 * @param key Specified key
	 * @return whether is integer value
	 */
	public boolean isIntValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return  (value instanceof Integer) || 
				(value instanceof Short) || 
				(value instanceof Byte);
	}

	
	/**
	 * Testing whether value associated with the specified key is time.
	 * @param key Specified key
	 * @return whether is time value
	 */
	public boolean isTimeValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return value instanceof Long;
	}

	
	/**
	 * Testing whether value associated with the specified key is long number. In Hudup framework, long number is also time in mili-seconds.
	 * @param key Specified key
	 * @return whether is long value.
	 */
	public boolean isLongValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return value instanceof Long;
	}

	
	/**
	 * Testing whether value associated with the specified key is real number.
	 * @param key Specified key
	 * @return whether is real value
	 */
	public boolean isRealValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof Double) || (value instanceof Float);
	}

	
	/**
	 * Testing whether value associated with the specified key is boolean value.
	 * @param key Specified key
	 * @return whether is boolean value
	 */
	public boolean isBooleanValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof Boolean);
	}

	
	/**
	 * Testing whether value associated with the specified key is hidden text.
	 * Hidden text is represented by {@link HiddenText} class, which wraps the real content by a mask.
	 * @param key Specified key
	 * @return whether is hidden value
	 */
	public boolean isHiddenValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof HiddenText);
	}

	
	/**
	 * Testing whether value associated with the specified key is CDATA.
	 * CDATA is represented by {@link CData} class, which is a fragment of XML code that is not processed by browser.
	 * @param key Specified key
	 * @return whether is {@link CData}
	 */
	public boolean isCDataValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof CData);
	}

	
	/**
	 * Testing whether value associated with the specified key is properties list.
	 * @param key Specified key
	 * @return whether is {@link PropList}
	 */
	public boolean isPropListValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof PropList);
	}

	
	/**
	 * Testing whether value associated with the specified key is algorithm.
	 * {@link Alg} is the most abstract interface for all algorithms.
	 * @param key Specified key
	 * @return whether is {@link Alg}
	 */
	public boolean isAlgValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof Alg);
	}

	
	/**
	 * Testing whether value associated with the specified key is a list of algorithms.
	 * {@link AlgList} represents a list of algorithms, which is used to store many algorithms.
	 * 
	 * @param key Specified key
	 * @return whether is {@link AlgList}
	 */
	public boolean isAlgListValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof AlgList);
	}

	
	/**
	 * Testing whether value associated with the specified key is description of an algorithm.
	 * {@link AlgDesc} represents description of an algorithm.
	 * @param key Specified key
	 * @return whether is {@link AlgDesc}
	 */
	public boolean isAlgDescValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof AlgDesc);
	}

	
	/**
	 * Testing whether value associated with the specified key is a list of algorithm descriptions.
	 * {@link AlgDescList} represents a list of algorithm descriptions.
	 * @param key Specified key
	 * @return whether is {@link AlgDescList}
	 */
	public boolean isAlgDescListValue(String key) {
		if (!containsKey(key))
			return false;
		
		Serializable value = get(key);
		return (value instanceof AlgDescList);
	}

	
	/**
	 * Marking a key as read-only key. The internal variable {@link #readOnlyKeys} contains all read-only keys.
	 * @param key Specified key
	 */
	public void addReadOnly(String key) {
		readOnlyKeys.add(key);
	}
	
	
	/**
	 * Removing read-only mark on the specified key.
	 * @param key Specified key
	 */
	public void removeReadOnly(String key) {
		readOnlyKeys.remove(key);
	}
	
	
	/**
	 * Checking whether this property list contains ready-only key specified by the input parameter.
	 * @param key Specified key
	 * @return whether this property list contains the ready-only key specified by the input parameter.
	 */
	public boolean containsReadOnly(String key) {
		return readOnlyKeys.contains(key);
	}
	
	
	/**
	 * After this method is called, there is no read-only entry.
	 * Exactly, read-only entries become normal entries.
	 * In other words, collection variable {@link #readOnlyKeys} becomes empty.
	 */
	public void clearReadOnly() {
		readOnlyKeys.clear();
	}
	
	
	/**
	 * Marking a key as invisible key.
	 * @param key Specified key
	 */
	public void addInvisible(String key) {
		invisibleKeys.add(key);
	}
	
	
	/**
	 * Removing invisible mark on the specified key.
	 * @param key Specified key
	 */
	public void removeInvisible(String key) {
		invisibleKeys.remove(key);
	}
	
	
	/**
	 * Checking whether this property list contains invisible key specified by the input parameter.
	 * @param key Specified key
	 * @return whether this property list contains the invisible key specified by the input parameter.
	 */
	public boolean containsInvisible(String key) {
		return invisibleKeys.contains(key);
	}
	
	
	/**
	 * After this method is called, there is no invisible entry.
	 * Exactly, invisible entries become normal entries.
	 * In other words, collection variable {@link #invisibleKeys} becomes empty.
	 */
	public void clearInvisible() {
		invisibleKeys.clear();
	}


//	/**
//	 * Marking a key as unsaved key.
//	 * @param key Specified key
//	 */
//	public void addUnsaved(String key) {
//		unsavedKeys.add(key);
//	}
//	
//	
//	/**
//	 * Removing unsaved mark on the specified key.
//	 * @param key Specified key
//	 */
//	public void removeUnsaved(String key) {
//		unsavedKeys.remove(key);
//	}
//	
//	
//	/**
//	 * Checking whether this property list contains unsaved key specified by the input parameter.
//	 * @param key Specified key
//	 * @return whether this property list contains the unsaved key specified by the input parameter.
//	 */
//	public boolean containsUnsaved(String key) {
//		return unsavedKeys.contains(key);
//	}
//	
//	
//	/**
//	 * After this method is called, there is no unsaved entry.
//	 * Exactly, unsaved entries become normal entries.
//	 * In other words, collection variable {@link #unsavedKeys} becomes empty.
//	 */
//	public void clearUnsaved() {
//		unsavedKeys.clear();
//	}

	
	/**
	 * Loading configuration settings from specified URI.
	 * @param uri Specified URI. URI is represented by {@link xURI}.
	 * @return whether loading successfully
	 */
	public boolean load(xURI uri) {
		if (uri == null)
			return false;
		
		UriAdapter adapter = new UriAdapter(uri);
		if (!adapter.exists(uri)) {
			adapter.close();
			return false;
		}
		
		boolean result = true;
		Reader reader = adapter.getReader(uri);
		if (reader == null) {
			adapter.close();
			return false;
		}
		
		try {
			String ext = uri.getLastNameExtension();
			
			if (ext != null && ext.equals("xml"))
				result = loadXml(reader);
			else
				result = loadJson(reader);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			result = false;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return result;
	}
	 
	
	/**
	 * Saving configuration settings to URI.
	 * @param uri Specified URI. URI is represented by {@link xURI}
	 * @return whether loading successfully
	 */
	public boolean save(xURI uri) {
		if (uri == null)
			return false;

		UriAdapter adapter = new UriAdapter(uri);
		boolean result = true;
		Writer writer = null;
		try {
			writer = adapter.getWriter(uri, false);
			String ext = uri.getLastNameExtension();
			
			if (ext != null && ext.equals("xml"))
				result = saveXml(writer);
			else
				result = saveJson(writer);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			result = false;
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return result;
    }

	
	/**
	 * Loading (reading) configuration settings from storage by {@link Reader}. The format of archive (file) is JSON.
	 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 * @param reader Specified {@link Reader} of JSON archive (file).
	 * @return whether loading successfully
	 */
	public boolean loadJson(Reader reader) {
		PropList propList = (PropList) Util.getJsonParser().parseJson(reader);
		this.putAll(propList.convertAfterLoad());
		
		return true;
	}
	
	
	/**
	 * Saving (writing) configuration settings to storage by {@link Writer}. The format of archive (file) is JSON.
	 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 * @param writer Specified {@link Writer} of JSON archive (file).
	 * @return whether saving successfully
	 */
	public boolean saveJson(Writer writer) {
		return Util.getJsonParser().toJson(convertBeforeSave(), writer);
	}

	
	/**
	 * Loading (reading) configuration settings from XML content by {@link Reader}.
	 * XML is a popular data format used for interchange among many protocols. Please visit <a href="https://www.w3.org/XML">https://www.w3.org/XML</a> for more details about XML.
	 * @param reader Specified {@link Reader} of XML content.
	 * @return whether loading successfully.
	 */
	public boolean loadXml(Reader reader) {
		
		PropList configTempList = new PropList();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = factory.newDocumentBuilder().parse(new InputSource(reader));
			
			Element rootElement = doc.getDocumentElement();
			Object obj = Util.newInstance(rootElement.getAttribute(ELEMENT_CLASSNAME));
			if (obj != null && obj instanceof PropList)
				configTempList = (PropList)obj;
			
			parserXml(rootElement, configTempList);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		this.putAll(configTempList.convertAfterLoad());
		
		return true;
	}

	
	/**
	 * Loading (reading) configuration settings from the specified root element according to XML format.
	 * XML is a popular data format used for interchange among many protocols. Please visit <a href="https://www.w3.org/XML">https://www.w3.org/XML</a> for more details about XML.
	 * Such configuration settings are stored to the specified parameter {@code rootPropList}.
	 * Method {@link #loadXml(Reader)} calls this method.
	 * @param rootElement Specified root element according to XML format.
	 * @param rootPropList The root {@link PropList} to store parsed configuration settings.
	 */
	private void parserXml(Element rootElement, PropList rootPropList) {
		NodeList children = rootElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element param = (Element) children.item(i);
			String key = param.getAttribute(ELEMENT_KEY);
			String type = param.getAttribute(ELEMENT_TYPE);
			String classname = param.getAttribute(ELEMENT_CLASSNAME);
			String value = param.getAttribute(ELEMENT_VALUE);
			if (type == null || type.isEmpty())
				return;
			
			if (key.equals(READ_ONLY_KEYS)) {
				List<String> list = TextParserUtil.parseListByClass(value, String.class, ",");
				rootPropList.readOnlyKeys.addAll(list);
			}
			else if (key.equals(INVISIBLE_KEYS)) {
				List<String> list = TextParserUtil.parseListByClass(value, String.class, ",");
				rootPropList.invisibleKeys.addAll(list);
			}
//			else if (key.equals(UNSAVED_KEYS)) {
//				List<String> list = TextParserUtil.parseListByClass(value, String.class, ",");
//				rootPropList.unsavedKeys.addAll(list);
//			}
			else if(type.equals(Attribute.toTypeString(Type.bit)))
				rootPropList.put(key, Boolean.parseBoolean(value));
			else if(type.equals(Attribute.toTypeString(Type.integer)))
				rootPropList.put(key, Integer.parseInt(value));
			else if(type.equals(Attribute.toTypeString(Type.time)))
				rootPropList.put(key, Long.parseLong(value));
			else if(type.equals(Attribute.toTypeString(Type.real)))
				rootPropList.put(key, Double.parseDouble(value));
			else if(type.equals(Attribute.toTypeString(Type.string)))
				rootPropList.put(key, value);
			else if(type.equals(toTypeString(ExtendedType.hidden)))
				rootPropList.put(key, new HiddenText(value));
			else if(type.equals(toTypeString(ExtendedType.cdata))) {
				Text cdata = null;
				NodeList list = param.getChildNodes();
				for (int j = 0; j < list.getLength(); j++) {
					Node node = list.item(j);
					if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
						cdata = (CDATASection) node;
						break;
					}
				}
				if (cdata != null)
					rootPropList.put(key, cdata.getData());
			}
			else if( type.equals(toTypeString(ExtendedType.proplist)) ) {
				Object obj = Util.newInstance(classname);
				if (obj == null || !(obj instanceof PropList))
					obj = new PropList();
				
				PropList newPropList = (PropList) obj;
				rootPropList.put(key, newPropList);
				parserXml(param, newPropList);
			}
			else if( type.equals(toTypeString(ExtendedType.alg)) || 
					type.equals(toTypeString(ExtendedType.alg_desc)) ) {
				
				Object obj = Util.newInstance(classname);
				if (obj != null && obj instanceof Alg) {
					rootPropList.put(key, (Alg)obj);
					parserXml(param, ((Alg)obj).getConfig());
				}
			}
			else if( type.equals(toTypeString(ExtendedType.alg_list)) || 
					type.equals(toTypeString(ExtendedType.alg_desc_list)) ) {
				AlgList algList = new AlgList();
				rootPropList.put(key, algList);
				
				NodeList list = param.getChildNodes();
				for (int j = 0; j < list.getLength(); j++) {
					if (list.item(j).getNodeType() != Node.ELEMENT_NODE)
						continue;

					Element element = (Element) list.item(j);
					String elementType = element.getAttribute(ELEMENT_TYPE);
					if( !elementType.equals(toTypeString(ExtendedType.alg)) && 
							!elementType.equals(toTypeString(ExtendedType.alg_desc)) )
						continue;
					
					Object obj = Util.newInstance(element.getAttribute(ELEMENT_CLASSNAME));
					if (obj != null && obj instanceof Alg) {
						algList.add((Alg)obj);
						parserXml( element, ((Alg)obj).getConfig());
					}
				}
					
			} // end if
			
		}
		
	}
	
	
	/**
	 * Saving (writing) configuration settings to XML content by {@link Writer}.
	 * XML is a popular data format used for interchange among many protocols. Please visit <a href="https://www.w3.org/XML">https://www.w3.org/XML</a> for more details about XML.
	 * @param writer Specified {@link Writer} of XML content.
	 * @return whether saving successfully.
	 */
	public boolean saveXml(Writer writer) {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = factory.newDocumentBuilder().newDocument();
			Element rootElement = doc.createElement(ELEMENT_CONFIG);
			rootElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.proplist));
			rootElement.setAttribute(ELEMENT_CLASSNAME, this.getClass().getName());
			doc.appendChild(rootElement);
			
        	PropList rootPropList = convertBeforeSave();
        	createXml(doc, doc.getDocumentElement(), rootPropList);
        	
        	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	DOMSource source = new DOMSource(doc);
        	StreamResult result = new StreamResult(writer);
        	transformer.transform(source, result);
        	
        	return true;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
    }

	
	/**
	 * Completing the XML root element specified by the parameter {@code rootElement} by reading the properties list specified by the parameter {@code rootPropList}.
	 * XML is a popular data format used for interchange among many protocols. Please visit <a href="https://www.w3.org/XML">https://www.w3.org/XML</a> for more details about XML.
	 * Note, configuration settings are stored to the specified parameter {@code rootPropList}.
	 * Method {@link #saveXml(Writer)} calls this method.
	 * @param doc The XML document corresponding to the properties list specified by the parameter {@code rootPropList}.
	 * @param rootElement The XML root element to be completed. It is the output parameter but it is also the input parameter. When it is put in this method as an input parameter, it is empty.
	 * @param rootPropList Specified properties list from which the XML root element is created.
	 */
	private static void createXml(Document doc, Element rootElement, PropList rootPropList) {
		
		Set<String> keys = rootPropList.keySet();
		for (String key : keys) {
			Element newElement = doc.createElement(ELEMENT_PARAM);
			newElement.setAttribute(ELEMENT_KEY, key);
			
			if (rootPropList.isBooleanValue(key)) {
				boolean value = rootPropList.getAsBoolean(key);
				newElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.bit));
				newElement.setAttribute(ELEMENT_VALUE, value + "");
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isIntValue(key)) {
				int value = rootPropList.getAsInt(key);
				newElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.integer));
				newElement.setAttribute(ELEMENT_VALUE, value + "");
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isTimeValue(key)) {
				int value = rootPropList.getAsInt(key);
				newElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.time));
				newElement.setAttribute(ELEMENT_VALUE, value + "");
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isRealValue(key)) {
				double value = rootPropList.getAsReal(key);
				newElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.real));
				newElement.setAttribute(ELEMENT_VALUE, value + "");
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isStringValue(key)) {
				String value = rootPropList.getAsString(key);
				newElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.string));
				newElement.setAttribute(ELEMENT_VALUE, value);
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isHiddenValue(key)) {
				String value = rootPropList.getAsHidden(key).getText();
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.hidden));
				newElement.setAttribute(ELEMENT_VALUE, value);
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isCDataValue(key)) {
				String value = rootPropList.getAsCData(key).getData();
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.cdata));
				newElement.appendChild(doc.createCDATASection(value));
				
				rootElement.appendChild(newElement);
			}
			else if (rootPropList.isPropListValue(key)) {
				PropList propList = rootPropList.getAsPropList(key);
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.proplist));
				newElement.setAttribute(ELEMENT_CLASSNAME, propList.getClass().getName());
				
				rootElement.appendChild(newElement);
				createXml(doc, newElement, propList);
			}
			else if (rootPropList.isAlgValue(key)) {
				Alg alg = rootPropList.getAsAlg(key);
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg));
				newElement.setAttribute(ELEMENT_CLASSNAME, alg.getClass().getName());
				
				rootElement.appendChild(newElement);
				
				createXml(doc, newElement, alg.getConfig());
			}
			else if (rootPropList.isAlgDescValue(key)) {
				AlgDesc algDesc = rootPropList.getAsAlgDesc(key);
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg_desc));
				newElement.setAttribute(ELEMENT_CLASSNAME, algDesc.getAlgClassName());
				
				rootElement.appendChild(newElement);
				
				createXml(doc, newElement, algDesc.getConfig());
			}
			else if (rootPropList.isAlgListValue(key)) {
				AlgList algList = rootPropList.getAsAlgList(key);
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg_list));
				
				rootElement.appendChild(newElement);
				
				for (int i = 0; i < algList.size(); i++) {
					Alg alg = algList.get(i);
					Element element = doc.createElement(ELEMENT_PARAM);
					element.setAttribute(ELEMENT_KEY, ELEMENT_ALGORITHM + i);
					element.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg));
					element.setAttribute(ELEMENT_CLASSNAME, alg.getClass().getName());
					
					newElement.appendChild(element);
					createXml(doc, element, alg.getConfig());
				}
				
			}
			else if (rootPropList.isAlgDescListValue(key)) {
				AlgDescList algDescList = rootPropList.getAsAlgDescList(key);
				newElement.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg_desc_list));
				
				rootElement.appendChild(newElement);
				
				for (int i = 0; i < algDescList.size(); i++) {
					AlgDesc algDesc = algDescList.get(i);
					Element element = doc.createElement(ELEMENT_PARAM);
					element.setAttribute(ELEMENT_KEY, ELEMENT_ALGORITHM + i);
					element.setAttribute(ELEMENT_TYPE, toTypeString(ExtendedType.alg_desc));
					element.setAttribute(ELEMENT_CLASSNAME, algDesc.getAlgClassName());
					
					newElement.appendChild(element);
					createXml(doc, element, algDesc.getConfig());
				}
				
			}
			
				
		} // end for
		
		
		Element readOnlyKeysElement =createSpecialElement(doc, READ_ONLY_KEYS, rootPropList.readOnlyKeys);
		rootElement.appendChild(readOnlyKeysElement);
		
		Element invisibleKeysElement =createSpecialElement(doc, INVISIBLE_KEYS, rootPropList.invisibleKeys);
		rootElement.appendChild(invisibleKeysElement);
		
//		Element unsavedKeysElement =createSpecialElement(doc, UNSAVED_KEYS, rootPropList.unsavedKeys);
//		rootElement.appendChild(unsavedKeysElement);
	}
	
	
	/**
	 * Create special element for special keys like read-only keys, invisible keys, unsaved keys.
	 * @param doc XML document.
	 * @param keySetName special key set name.
	 * @param keySet special key set.
	 * @return special element for special keys.
	 */
	private static Element createSpecialElement(Document doc, String keySetName, Set<String> keySet) {
		Element specialElement = doc.createElement(ELEMENT_PARAM);
		specialElement.setAttribute(ELEMENT_KEY, keySetName);
		specialElement.setAttribute(ELEMENT_TYPE, Attribute.toTypeString(Type.string));
		specialElement.setAttribute(ELEMENT_VALUE, TextParserUtil.toText(keySet, ","));
		
		return specialElement;
	}
	
	
	/**
	 * Loading (reading) configuration settings from Java {@link Properties} by {@link Reader}.
	 * {@link Properties} stores a set of key-value pairs. When saving to file, each pair is on a line and has the format &quot;key=value&quot;.
	 * Please see Java document for more details about {@link Properties}.
	 * This method calls {@link #convertAfterLoad()} after doing loading task.
	 * @param reader {@link Reader} to read {@link Properties}.
	 * @return whether loading successfully.
	 */
	public boolean loadProperties(Reader reader) {
		Properties properties = new Properties();
		try {
			properties.load(reader);
			PropList propList = new PropList();
			propList.putAll(properties);
			
			this.putAll(propList.convertAfterLoad());
			return true;
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Converting this {@link PropList} into Java {@link Properties}. {@link Properties} stores a set of key-value pairs.
	 * Please see Java document for more details about {@link Properties}.
	 * @return {@link Properties} converted from this list.
	 */
	public Properties toProperties() {
		Properties properties = new Properties();
		Set<String> keys = keySet();
		for (String key : keys) {
			properties.put(key, get(key));
		}
		
		properties.put(READ_ONLY_KEYS, TextParserUtil.toText(readOnlyKeys, ","));
		properties.put(INVISIBLE_KEYS, TextParserUtil.toText(invisibleKeys, ","));
//		properties.put(UNSAVED_KEYS, TextParserUtil.toText(unsavedKeys, ","));
		return properties;
	}
	
	
	/**
	 * Saving (writing) configuration settings to Java {@link Properties} storage by {@link Writer}.
	 * {@link Properties} stores a set of key-value pairs. When saving to file, each pair is on a line and has the format &quot;key=value&quot;.
	 * Please see Java document for more details about {@link Properties}.
	 * This method calls {@link #convertBeforeSave()} before doing saving task.
	 * @param writer {@link Writer} to write this list to {@link Properties} storage.
	 * @return whether save {@link Properties} successfully.
	 */
	public boolean saveProperties(Writer writer) {
		
		try {
			Properties properties = convertBeforeSave().toProperties();
			properties.store(writer, null);
			return true;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Conversing this list into new converted list before saving this list so that the new converted list is saved simply and correctly later.
	 * This method calls {@link #browseBeforeSave(PropList, PropList)}. 
	 * @return Converted {@link PropList}
	 */
	protected PropList convertBeforeSave() {
		PropList newPropList = (PropList) clone();
		newPropList.clear();
		browseBeforeSave(this, newPropList);
		
		return newPropList;
	}
	
	
	/**
	 * Browsing all entries and doing some specific task on each entry before saving properties list.
	 * In this current implementation, the specific task focuses on conversion. The input parameter {@code src} is the original list and the output parameter {@code dst} is the converted list.
	 * Such conversion asserts that the converted list {@code dst} is saved simply and correctly later.
	 * For each entry, if the {@code value} is {@link HiddenText}, the internal text of {@link HiddenText} is encrypted.
	 * If {@code value} is {@link Alg}, it is converted into {@link AlgDesc}.
	 * This method is called by {@link #convertBeforeSave()}.
	 * 
	 * @param src Original properties list.
	 * @param dst Converted properties list after browsing all entries and doing some specific task on each entry.
	 */
	private static void browseBeforeSave(PropList src, PropList dst) {
		Set<String> keys = src.keySet();
		for (String key : keys) {
			Serializable value = src.get(key);
			if (value == null)
				continue;
//			else if (src.containsUnsaved(key))
//				continue;
			else if (value instanceof HiddenText) {
				HiddenText hidden = new HiddenText();
				hidden.setText(src.encrypt((HiddenText)value));
				
				dst.put(key, hidden);
			}
			else if (value instanceof Alg) {
				Alg alg = (Alg) value;
				DataConfig config = (DataConfig) alg.getConfig().clone();
				config.clear();

				AlgDesc algDesc = new AlgDesc();
				algDesc.setAlgClassName(alg.getClass().getName());
				algDesc.setConfig(config);
				dst.put(key, algDesc);
				
				browseBeforeSave(alg.getConfig(), algDesc.getConfig());
			}
			else if (value instanceof AlgList) {
				AlgList algList = (AlgList) value;
				AlgDescList algDescList = new AlgDescList();
				dst.put(key, algDescList);
				
				for (int i = 0; i < algList.size(); i++) {
					Alg alg = algList.get(i);
					DataConfig config = (DataConfig) alg.getConfig().clone();
					config.clear();
					
					AlgDesc algDesc = new AlgDesc();
					algDesc.setAlgClassName(alg.getClass().getName());
					algDesc.setConfig(config);
					algDescList.add(algDesc);
					
					browseBeforeSave(alg.getConfig(), algDesc.getConfig());
				}
			}
			else if (value instanceof PropList) {
				PropList propList = (PropList) ((PropList)value).clone();
				propList.clear();
				dst.put(key, propList);
				
				browseBeforeSave((PropList)value, propList);
			}
			else 
				dst.put(key, value);
		}
		
		dst.readOnlyKeys.addAll(src.readOnlyKeys);
		dst.invisibleKeys.addAll(src.invisibleKeys);
	}
	
	
	/**
	 * Conversing this list into new converted list after loading this list so that the new converted list is corrected.
	 * This method calls {@link #browseAfterLoad(PropList, PropList)}. 
	 * @return Converted {@link PropList}.
	 */
	protected PropList convertAfterLoad() {

		PropList newPropList = (PropList) clone();
		newPropList.clear();
		browseAfterLoad(this, newPropList);
		
		return newPropList;
	}

	
	/**
	 * Browsing all entries and doing some specific task on each entry after loading properties list.
	 * In this current implementation, the specific task focuses on conversion. The input parameter {@code src} is the list loaded before and the output parameter {@code dst} is the converted list.
	 * Such conversion asserts that the converted list {@code dst} is corrected.
	 * For each entry, if the {@code value} is {@link HiddenText}, the internal text of {@link HiddenText} is decrypted.
	 * If {@code value} is {@link AlgDesc}, it is converted into {@link Alg}.
	 * This method is called by {@link #convertAfterLoad()}.
	 * 
	 * @param src The list loaded before.
	 * @param dst The converted list.
	 */
	private static void browseAfterLoad(PropList src, PropList dst) {
		String readOnlyKeyText = null;
		String invisibleKeyText = null;
//		String unsavedKeyText = null;
		
		Set<String> keys = src.keySet();
		for (String key : keys) {
			Serializable value = src.get(key);
			if (value == null)
				continue;
			else if (key.equals(READ_ONLY_KEYS))
				readOnlyKeyText = value.toString();
			else if (key.equals(INVISIBLE_KEYS))
				invisibleKeyText = value.toString();
//			else if (key.equals(UNSAVED_KEYS))
//				unsavedKeyText = value.toString();
			else if (value instanceof HiddenText) {
				HiddenText hidden = src.decrypt( ((HiddenText)value).getText() ) ;
				dst.put(key, hidden);
			}
			else if (value instanceof AlgDesc) {
				AlgDesc algDesc = (AlgDesc) value;
				Alg alg = algDesc.createAlg();
				if (alg == null)
					continue;
				dst.put(key, alg);
				
				browseAfterLoad(algDesc.getConfig(), alg.getConfig());
			}
			else if (value instanceof AlgDescList) {
				AlgDescList algDescList = (AlgDescList) value;
				AlgList algList = new AlgList();
				dst.put(key, algList);
				
				for (int i = 0; i < algDescList.size(); i++) {
					AlgDesc algDesc = algDescList.get(i);
					Alg alg = algDesc.createAlg();
					alg.resetConfig();
					algList.add(alg);
					
					browseAfterLoad(algDesc.getConfig(), alg.getConfig());
				}
			}
			else if (value instanceof PropList) {
				PropList propList = (PropList) ((PropList)value).clone();
				propList.clear();
				dst.put(key, propList);
				
				browseAfterLoad((PropList)value, propList);
			}
			else 
				dst.put(key, value);
		}
		
		dst.readOnlyKeys.addAll(src.readOnlyKeys);
		dst.invisibleKeys.addAll(src.invisibleKeys);
//		dst.unsavedKeys.addAll(src.unsavedKeys);
		
		if (readOnlyKeyText != null)
			dst.addSpecialKeys(readOnlyKeyText, dst.readOnlyKeys);
		if (invisibleKeyText != null)
			dst.addSpecialKeys(invisibleKeyText, dst.invisibleKeys);
//		if (unsavedKeyText != null)
//			dst.addSpecialKeys(unsavedKeyText, dst.unsavedKeys);
	}

	
	/**
	 * Validating {@code key} and {@code value} of each entry.
	 * @param key Specified key to be validated.
	 * @param value Specified value to be validated.
	 * @return whether key and value valid.
	 */
	public boolean validate(String key, Serializable value) {
		if (key == null || value == null)
			return false;
		else
			return true;
	}

	
	/**
	 * Pre-processing the specified value of the specified key before putting it into this list.
	 * By default, this method of {@link PropList} does nothing, which returns the specified value.
	 * @param key Specified key.
	 * @param value Specified value.
	 * @return Processed value before putting it into this list. Such processed value is a result of pre-processing on the specified value.
	 */
	protected Serializable preprocessValue(String key, Serializable value) {
		return value;
	}
	
	
	/**
	 * This method allows user to edit (modify, customize) value of the specified key. The {@code defaultValue} is initial value of such key.
	 * This method returns the value as a result that user modified the {@code defaultValue}.
	 * As usual, this method shows a graphic user interface (GUI) assist user to make modification.
	 * By default, this method of {@link PropList} does nothing, which returns {@code null}.
	 * @param comp Graphic user interface (GUI) component works as the parent component of the GUI assistant that helps user to make modification.
	 * @param key Specified key.
	 * @param defaultValue Initial value of the specified key.
	 * @return Customized value after user made modification.
	 */
	public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
		
		return null;
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		PropList propList = new PropList();
		propList.putAll(this);
		return propList;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> list = TextParserUtil.parseTextList(spec, TextParserUtil.EXTRA_SEP);
		if (list.size() == 0) return;
		
		
		List<String> list2 = TextParserUtil.parseTextList(list.get(0), ",");
		for (String string : list2) {
			List<String> pair = TextParserUtil.split(string, "=", null);
			if (pair.size() < 2)
				continue;
			
			put(TextParserUtil.decryptReservedChars(pair.get(0)), TextParserUtil.decryptReservedChars(pair.get(1)));
		}
		if (list.size() < 2) return;
		
		
		List<String> readOnlyList = TextParserUtil.parseTextList(list.get(1), ",");
		readOnlyKeys.addAll(TextParserUtil.decryptReservedChars(readOnlyList));
		if (list.size() < 3) return;
		
		
		List<String> invisibleList = TextParserUtil.parseTextList(list.get(2), ",");
		invisibleKeys.addAll(TextParserUtil.decryptReservedChars(invisibleList));
//		if (list.size() < 4) return;
//		
//		List<String> unsavedList = TextParserUtil.parseTextList(list.get(3), ",");
//		unsavedKeys.addAll(TextParserUtil.decryptReservedChars(unsavedList));
	}
	
	
	@Override
	public String toText() {
		StringBuffer buffer = new StringBuffer();
		
		List<String> keys = Util.newList();
		keys.addAll(keySet());
		int k = 0;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Serializable value = get(key);
			if (value instanceof HiddenText)
				continue;
			
			if (k > 0)
				buffer.append("," + " ");
			String line = TextParserUtil.encryptReservedChars(key) + "=" + 
				TextParserUtil.encryptReservedChars(value.toString());
			buffer.append(line);
			k++;
		}
		
		
		buffer.append(TextParserUtil.EXTRA_SEP);
		List<String> readOnlyList = Util.newList();
		for (String key : readOnlyKeys) {
			readOnlyList.add(key);
		}
		buffer.append(TextParserUtil.toText(TextParserUtil.encryptReservedChars(readOnlyList), ","));
		
		
		buffer.append(TextParserUtil.EXTRA_SEP);
		List<String> invisibleList = Util.newList();
		for (String key : invisibleKeys) {
			invisibleList.add(key);
		}
		buffer.append(TextParserUtil.toText(TextParserUtil.encryptReservedChars(invisibleList), ","));

		
//		buffer.append(TextParserUtil.EXTRA_SEP);
//		List<String> unsavedList = Util.newList();
//		for (String key : unsavedKeys) {
//			unsavedList.add(key);
//		}
//		buffer.append(TextParserUtil.toText(TextParserUtil.encryptReservedChars(unsavedList), ","));

		
		return buffer.toString();
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	/**
	 * Extracting the plain internal text of specified {@link HiddenText}.
	 * Such plain text needs to be encrypted. This is the reason that the method is called &quot;encrypt&quot;.
	 * However, in the current implementation, such returned plain text it is not encrypted yet.
	 * @param hidden Specified {@link HiddenText}.
	 * @return The plain internal text of specified {@link HiddenText}. Such plain text is encrypted if possible.
	 */
	protected String encrypt(HiddenText hidden) {
		return hidden.getText();
	}
	
	
	/**
	 * Wrapping the specified text by {@link HiddenText}. The specified text needs to be decrypted if it was encrypted before.
	 * This is the reason that the method is called &quot;decrypt&quot;.
	 * @param text Specified text. It needs to be decrypted if it was encrypted before.
	 * @return {@link HiddenText} that wraps the specified text.
	 */
	protected HiddenText decrypt(String text) {
		return new HiddenText(text);
	}
	
	
	/**
	 * Getting extended type {@link ExtendedType} from specified object.
	 * For example, {@link HiddenText} has extended type {@link ExtendedType#hidden}.
	 * @param obj Specified Java object.
	 * @return Attribute {@link ExtendedType} from object.
	 */
	public static ExtendedType fromObject(Object obj) {
		if (obj instanceof HiddenText)
			return ExtendedType.hidden;
		else if (obj instanceof CData)
			return ExtendedType.cdata;
		else if (obj instanceof PropList)
			return ExtendedType.proplist;
		else if (obj instanceof Alg)
			return ExtendedType.alg;
		else if (obj instanceof AlgList)
			return ExtendedType.alg_list;
		else if (obj instanceof AlgDesc)
			return ExtendedType.alg_desc;
		else if (obj instanceof AlgDescList)
			return ExtendedType.alg_desc_list;
		else
			return ExtendedType.unknown;
	}


	/**
	 * Getting extended type {@link ExtendedType} from class of specified object.
	 * @param objClass Class of specified object.
	 * @return {@link ExtendedType} from class of specified object.
	 */
	public static ExtendedType fromObjectClass(Class<?> objClass) {
		if (objClass.equals(HiddenText.class))
			return ExtendedType.hidden;
		else if (objClass.equals(CData.class))
			return ExtendedType.cdata;
		else if (objClass.equals(PropList.class))
			return ExtendedType.proplist;
		else if (objClass.equals(Alg.class))
			return ExtendedType.alg;
		else if (objClass.equals(AlgList.class))
			return ExtendedType.alg_list;
		else if (objClass.equals(AlgDesc.class))
			return ExtendedType.alg_desc;
		else if (objClass.equals(AlgDescList.class))
			return ExtendedType.alg_desc_list;
		else
			return ExtendedType.unknown;
	}

	
	/**
	 * Getting {@link ExtendedType} from an integer. In other words, this methods returns the index of given extended type.
	 * For example, {@link ExtendedType#hidden} has index 0 and so the calling {@code fromInt(0)} returns {@code ExtendedType.hidden}.
	 * @param itype Specified integer
	 * @return {@link ExtendedType} from integer
	 */
	public static ExtendedType fromInt(int itype) {
		
		switch (itype) {
		
		case 0:
			return ExtendedType.hidden;
		case 1:
			return ExtendedType.cdata;
		case 2:
			return ExtendedType.proplist;
		case 3:
			return ExtendedType.alg;
		case 4:
			return ExtendedType.alg_list;
		case 5:
			return ExtendedType.alg_desc;
		case 6:
			return ExtendedType.alg_desc_list;
		case 7:
			return ExtendedType.unknown;
		default:
			return ExtendedType.unknown;
		}
		
	}

	
	/**
	 * Getting {@link ExtendedType} from a specified string. In other words, this methods returns the name of given extended type.
	 * For example, {@link ExtendedType#hidden} has name &quot;hidden&quot; and so the calling <i>fromString(&quot;hidden&quot;)</i> returns {@code ExtendedType.hidden}.
	 * @param stype specified string.
	 * @return {@link ExtendedType} from specified string.
	 */
	public static ExtendedType fromString(String stype) {
		stype = stype.toLowerCase();
		if (stype.equals("hidden"))
			return ExtendedType.hidden;
		else if (stype.equals("cdata"))
			return ExtendedType.cdata;
		else if (stype.equals("proplist"))
			return ExtendedType.proplist;
		else if (stype.equals("alg"))
			return ExtendedType.alg;
		else if (stype.equals("alg_list"))
			return ExtendedType.alg_list;
		else if (stype.equals("alg_desc"))
			return ExtendedType.alg_desc;
		else if (stype.equals("alg_desc_list"))
			return ExtendedType.alg_desc_list;
		else
			return ExtendedType.unknown;
	}

	
	/**
	 * Getting the index (integer form) of specified extended type. For example, the index of {@link ExtendedType#hidden} is 0.
	 * @param type Specified extended type.
	 * @return Integer form (index) of specified extended type.
	 */
	public static int toInt(ExtendedType type) {
		
		switch (type) {
		
		case hidden:
			return 0;
		case cdata:
			return 1;
		case proplist:
			return 2;
		case alg:
			return 3;
		case alg_list:
			return 4;
		case alg_desc:
			return 5;
		case alg_desc_list:
			return 6;
		case unknown:
			return 7;
		default:
			return 7;
		}
		
	}

	
	/**
	 * Getting the name (string form) of specified extended type. For example, the name of {@link ExtendedType#hidden} is &quot;hidden&quot;.
	 * @param type Specified extended type.
	 * @return String form of specified extended type.
	 */
	public static String toTypeString(ExtendedType type) {
		
		switch (type) {
		
		case hidden:
			return "hidden";
		case cdata:
			return "cdata";
		case proplist:
			return "proplist";
		case alg:
			return "alg";
		case alg_list:
			return "alg_list";
		case alg_desc:
			return "alg_desc";
		case alg_desc_list:
			return "alg_desc_list";
		case unknown:
			return "unknown";
		default:
			return "unknown";
		}
		
	}

	
	/**
	 * Getting the class of specified extended type. For example, the class of {@link ExtendedType#hidden} is &quot;HiddenText.class&quot; which is the class of {@link HiddenText}.
	 * @param type Specified extended type.
	 * @return {@link Class} from specified extended type.
	 */
	public static Class<?> toObjectClass(ExtendedType type) {
		
		switch (type) {
		
		case hidden:
			return HiddenText.class;
		case cdata:
			return CData.class;
		case proplist:
			return PropList.class;
		case alg:
			return Alg.class;
		case alg_list:
			return AlgList.class;
		case alg_desc:
			return AlgDesc.class;
		case alg_desc_list:
			return AlgDescList.class;
		case unknown:
			return Object.class;
		default:
			return Object.class;
		}
		
	}
	
	
}


