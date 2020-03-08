/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingTriple;

/**
 * This final class provides utility static methods for processing data structures such as asserting, conversion, parsing, splitting, processing, etc.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DSUtil {

	
	/**
	 * Testing whether or not the specified array contains not-a-number (NaN) value.
	 * 
	 * @param array specified array of double values.
	 * @return whether array contains NaN number
	 */
	public static boolean assertNotNaN(double[] array) {
		for (double v : array) {
			if (Double.isNaN(v))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Converting a specified array of byte values into array of integer values.
	 * @param b specified array of byte values.
	 * @return array of integer values.
	 */
	public final static int[] byteToInt(byte[] b) {
		int[] array = new int[b.length];
		
		for (int i = 0; i < b.length; i++)
			array[i] = b[i];
		
		return array;
	}
	
	
	/**
	 * Converting a specified matrix of byte values into matrix of integer values.
	 * @param b specified matrix of byte values.
	 * @return matrix of integer values.
	 */
	public final static int[][] byteToInt(byte[][] b) {
		int[][] array = new int[b.length][];
		
		for (int i = 0; i < b.length; i++)
			array[i] = byteToInt(b[i]);
		
		return array;
	}

	
	/**
	 * Converting a specified array of integer values into set of integer values.
	 * @param array specified array of integer values.
	 * @return set of integer values.
	 */
	public final static Set<Integer> intToSet(int[] array) {
		Set<Integer> set = Util.newSet();
		for (int v : array) {
			set.add(v);
		}
		return set;
	}
	
	
	/**
	 * Converting a specified array of elements (with template E) into list of elements (template E).
	 * @param <E> type of elements in array and list.
	 * @param array array of elements (with template E).
	 * @return {@link List} of elements (with template E).
	 */
	public static <E> List<E> toList(E[] array) {
		List<E> list = Util.newList(array.length);
		for (E e : array) {
			list.add(e);
		}
		
		return list;
	}
	
	
	/**
	 * Converting a specified array of real numbers into list of real numbers.
	 * @param array array of real numbers.
	 * @return list of real numbers.
	 */
	public static List<Double> toDoubleList(double[] array) {
		List<Double> list = Util.newList(array.length);
		for (double e : array) {
			list.add(e);
		}
		
		return list;
	}
	
	
	/**
	 * Cloning a specified collection of real numbers.
	 * @param collection the specified collection of real numbers.
	 * @return the cloned list of real numbers.
	 */
	public final static List<Double> toDoubleList(Collection<Double> collection) {
		List<Double> newList = Util.newList(collection.size());
		for (Double item : collection) {
			newList.add(item.doubleValue());
		}
		return newList;
	}

	
	/**
	 * Converting specified object into real number.
	 * @param object specified object.
	 * @return real number.
	 */
	public final static double toDouble(Object object) {
		if (object == null)
			return Constants.UNUSED;
		else if (object instanceof Double)
			return (double)object;
		else if (object instanceof Number)
			return ((Number)object).doubleValue();
		else if (object instanceof Boolean)
			return ((boolean)object) ? 1.0 : 0.0;
		else if (object instanceof Character)
			return Character.getNumericValue(((Character)object));
		else if (object instanceof Date)
			return ((Date)object).getTime();
		else {
			try {
				return Double.parseDouble(object.toString());
			}
			catch (Exception e) {
				
			}
			return Constants.UNUSED;
		}
	}
	
	
	/**
	 * Converting any object of a list of real numbers.
	 * @param object specified object.
	 * @param removeUnusedValues if true, unused real numbers are removed from list;
	 * @return a list of real numbers.
	 */
	public final static List<Double> toDoubleList(Object object, boolean removeUnusedValues) {
		if(object == null)
			return Util.newList();
		
		List<Double> newList = Util.newList();
		if (object instanceof Collection) {
			Collection<?> collections = (Collection<?>)object;
			for (Object el : collections)
				newList.add(toDouble(el));
		}
		else if (object instanceof Double[]) {
			Double[] array = (Double[])object;
			for (Double el : array)
				newList.add(el);
		}
		else if (object instanceof Object[]) {
			Object[] array = (Object[])object;
			for (Object el : array)
				newList.add(toDouble(el));
		}
		else if (object instanceof double[]) {
			double[] array = (double[])object;
			for (double el : array)
				newList.add(el);
		}
		else if (object instanceof float[]) {
			float[] array = (float[])object;
			for (float el : array)
				newList.add((double)el);
		}
		else if (object instanceof long[]) {
			long[] array = (long[])object;
			for (long el : array)
				newList.add((double)el);
		}
		else if (object instanceof int[]) {
			int[] array = (int[])object;
			for (int el : array)
				newList.add((double)el);
		}
		else if (object instanceof short[]) {
			short[] array = (short[])object;
			for (short el : array)
				newList.add((double)el);
		}
		else if (object instanceof byte[]) {
			byte[] array = (byte[])object;
			for (byte el : array)
				newList.add((double)el);
		}
		else if (object instanceof boolean[]) {
			boolean[] array = (boolean[])object;
			for (boolean el : array)
				newList.add(el ? 1.0 : 0.0);
		}
		else if (object instanceof char[]) {
			char[] array = (char[])object;
			for (char el : array)
				newList.add((double)Character.getNumericValue(el));
		}
		else if (object instanceof Profile) {
			Profile profile = (Profile)object;
			int n = profile.getAttCount();
			for (int i = 0; i < n; i++)
				newList.add(profile.getValueAsReal(i));
		}
		else {
			double value = toDouble(object);
			newList.add(value);
		}
		
		if (removeUnusedValues) {
			List<Double> tempList = Util.newList(newList.size());
			for (Double v : newList) {
				if (Util.isUsed(v))
					tempList.add(v);
			}
			newList = tempList;
		}
		
		return newList;
	}
	
	
	/**
	 * Converting a collection of double arrays to a list of double array.
	 * @param collection collection of double arrays.
	 * @return a list of double array.
	 */
	public final static List<double[]> cloneDoubleArrayList(Collection<double[]> collection) {
		List<double[]> list = Util.newList(collection.size());
		for (double[] array : collection) {
			double[] newArray = Arrays.copyOf(array, array.length);
			list.add(newArray);
		}
		
		return list;
	}
	
	
	/**
	 * Converting a specified collection of double values into array of double values.
	 * @param collection specified collection of double values.
	 * @return array of double values.
	 */
	public final static double[] toDoubleArray(Collection<Double> collection) {
		double[] array = new double[collection.size()];
		
		int i = 0;
		for (Double item : collection) {
			array[i] = item.doubleValue();
			i++;
		}
		
		return array;
	}

	
	/**
	 * Initialize a list of real numbers that has exact size contains filled values.
	 * @param size exact size.
	 * @param filledValue filled value.
	 * @return a list of real numbers that has exact size contains filled values.
	 */
	public final static List<Double> initDoubleList(int size, double filledValue) {
		List<Double> newList = Util.newList(size);
		for (int i = 0; i < size; i++) {
			newList.add(filledValue);
		}
		
		return newList;
	}
	
	
	/**
	 * Converting a specified collection of double values into array of integer values.
	 * @param collection specified collection of double values.
	 * @return array of integer values.
	 */
	public final static int[] toIntArray(Collection<Integer> collection) {
		int[] array = new int[collection.size()];
		
		int i = 0;
		for (int item : collection) {
			array[i] = item;
			i++;
		}
		
		return array;
	}


	/**
	 * Converting a specified collection of double values into array of integer values.
	 * Moreover such integer values are rounded from double values.
	 * @param collection specified collection of integer values.
	 * @return array of integer values.
	 */
	public final static int[] toRoundIntArray(Collection<Double> collection) {
		int[] array = new int[collection.size()];
		
		int i = 0;
		for (double item : collection) {
			array[i] = (int)(item + 0.5f);
			i++;
		}
		
		return array;
	}


	/**
	 * Deep cloning the specified map of rating triples.
	 * @param map specified map of rating triples.
	 * @return cloned map of rating triples.
	 */
	public final static Map<Integer, RatingTriple> clone(Map<Integer, RatingTriple> map) {
		Map<Integer, RatingTriple> newMap = Util.newMap();
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			Integer newKey = new Integer(key);
			RatingTriple newRat = (RatingTriple) map.get(key).clone();
			newMap.put(newKey, newRat);
		}
		
		return newMap;
	}
	
	
	/**
	 * Deep cloning a specified list of bit sets.
	 * @param bsList specified list of bit sets.
	 * @return {@link List} of {@link BitSet} (s) cloned from the specified list of bit sets. 
	 */
	public final static List<BitSet> clone(List<BitSet> bsList) {
		List<BitSet> result = Util.newList(bsList.size());
		
		for (BitSet bs : bsList) {
			result.add((BitSet)bs.clone());
		}
		return result;
	}
	
	
	/**
	 * Clone a collection of profiles.
	 * @param profiles specified collection of profiles
	 * @return a list of cloned profiles.
	 */
	public static List<Profile> clone(Collection<Profile> profiles) {
		List<Profile> profileList = Util.newList(profiles.size());
		for(Profile profile : profiles) {
			Profile clonedProfile = (Profile)profile.clone();
			profileList.add(clonedProfile);
		}
		return profileList;
	}
	
	
	/**
	 * Transfer all profiles of the specified collection to the returned list.
	 * @param profiles specified collection of profiles.
	 * @return a list of transferred profiles.
	 */
	public static List<Profile> transfer(Collection<Profile> profiles) {
		List<Profile> profileList = Util.newList(profiles.size());
		for(Profile profile : profiles) {
			profileList.add(profile);
		}
		return profileList;
	}

	
	/**
	 * Splitting the specified string (source string) into a list of words (tokens). The character (string) that is used for separation is specified by the parameter {@code sep}.
	 * The returned list can be empty.
	 * For each returned word (token), the string that specified by the parameter {@code remove} is removed from such word.
	 * @param source specified string to be split.
	 * @param sep character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return {@link List} of words (tokens) from splitting the specified string. This list can be empty.
	 */
	public final static List<String> splitAllowEmpty(String source, String sep, String remove) {
		List<String> result = Util.newList();
		
		if (source.isEmpty() || sep.length() > source.length())
			return result;
		
		int begin = 0;
		while (begin < source.length()) {
			int next = source.indexOf(sep, begin);
			
			if (next == -1) {
				String word = source.substring(begin).trim();
				result.add(word);
			}
			else if (next == begin) {
				result.add("");
			}
			else {
				String word = source.substring(begin, next).trim();
				result.add(word);
			}
			
			if (next == -1)
				begin = source.length();
			else {
				begin = next + sep.length();
				if (begin == source.length())
					result.add("");
			}
			
		}
		
		return result;
	}
	
	
	/**
	 * Counting the number of bits in the specified bit set.
	 * 
	 * @param bs specified bit set.
	 * @return the number of bits in the specified bit set.
	 */
	public static int countSetBit(BitSet bs) {
		int count = 0;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			count ++;
		}
		return count;
	}
	
	
	/**
	 * Testing whether the bit set specified the first parameter {@code container} contains the bit set specified by the second parameter {@code item}.
	 * @param container first bit set as a container.
	 * @param item second bit set as an element.
	 * @return whether the bit set specified the first parameter {@code container} contains the bit set specified by the second parameter {@code item}.
	 */
	public static boolean containsSetBit(BitSet container, BitSet item) {
		BitSet and = (BitSet)container.clone();
		and.and(item);
		return countSetBit(and) >= countSetBit(item);
	}
	
	
	/**
	 * Processing data read from specified {@link Reader} line by line. How to process each line is specified by the input parameter {@code processor}.
	 * @param reader {@link Reader} to read data from archive (file).
	 * @param processor class implementing the interface {@link LineProcessor} specifying how to process data line by line.
	 */
	public static void lineProcess(BufferedReader reader, LineProcessor processor) {
		try {
			String line = null;
			while ( (line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				
				processor.process(line);
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * In the specified list, moving elements in specified range to specified position.
	 * @param <T> type of elements of the specified list.
	 * @param list specified list.
	 * @param start start position of the specified range.
	 * @param end end position of the specified range.
	 * @param to specified position where elements in specified range are moved to.
	 * @author Someone on Internet
	 */
	public static <T> void moveRow(List<T> list, int start, int end, int to) {
		
		if (start < 0 || start >= list.size() || 
				end < 0 || end >= list.size() ||
				to < 0 || to >= list.size())
			return;
		
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		if (start <= to && to <= end)
			return;
		
		List<T> newList = Util.newList();
		for (int i = 0; i < list.size() && (start < to ? i <= to : i < to); i++) {
			if (i < start || end < i)
				newList.add(list.get(i));
		}
		
		for (int i = start; i <= end; i++) {
			newList.add(list.get(i));
		}
		
		for (int i = (start < to ? to + 1 : to); i < list.size(); i++) {
			if (i < start || end < i)
				newList.add(list.get(i));
		}
		
		list.clear();
		list.addAll(newList);
		newList.clear();
	}
	
	
	/**
	 * Shorten verbal name.
	 * @param name verbal name.
	 * @return verbal name shortened.
	 */
	public static String shortenVerbalName(String name) {
		if (name == null) return "";
		
		if (name.length() > Constants.MAX_VERBAL_NAME_LENGTH)
			name = name.substring(0, Constants.MAX_VERBAL_NAME_LENGTH) + "...";
		return name;
		
	}
	
	
}
