/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class is used to store many algorithms.
 * It contains the main variable {@link #list} that is a list of {@link Alg}.
 * Note that {@link Alg} interface represents any algorithm.
 * It provides methods to retrieve algorithms and process on the {@link #list}.
 * As a convention, this class can be called algorithm list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgList implements Serializable, net.hudup.core.Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The main variable is a {@link List} of algorithms {@link Alg}.
	 */
	protected List<Alg> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public AlgList() {
		
	}

	
	/**
	 * Constructor with a collection of algorithms.
	 * @param list Collection of {@link Alg}
	 */
	public AlgList(Collection<Alg> list) {
		this.list.addAll(list);
	}
	
	
	/**
	 * Constructor with one specified algorithm.
	 * 
	 * @param alg Specified algorithm {@link Alg}
	 */
	public AlgList(Alg alg) {
		this.list.add(alg);
	}
	
	
	/**
	 * Getting an algorithm by specified index.
	 * @param index Specified index
	 * @return Algorithm at specified index with note that any algorithm is represented by interface {@link Alg}
	 */
	public Alg get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Adding a new specified algorithm.
	 * @param alg Specified algorithm
	 * @return true if adding algorithm successfully; otherwise false
	 */
	public boolean add(Alg alg) {
		return list.add(alg);
	}
	
	
	/**
	 * Adding a whole algorithm list into this algorithm list.
	 * @param algList Specified algorithm list.
	 * @return whether adding algorithms successfully.
	 */
	public boolean addAll(AlgList algList) {
		return list.addAll(algList.list);
	}
	
	
	/**
	 * Adding a whole algorithm collection into this algorithm list.
	 * @param algs Specified algorithm collection.
	 * @return whether adding algorithms successfully.
	 */
	public boolean addAll(Collection<Alg> algs) {
		return list.addAll(algs);
	}

	
	/**
	 * Removing the algorithm from this list at specified index.
	 * @param index Specified index.
	 * @return removed algorithm.
	 */
	public Alg remove(int index) {
		return list.remove(index);
	}
	
	
	/**
	 * Removing the specified algorithm from this list.
	 * @param alg Specified algorithm.
	 * @return whether remove successfully
	 */
	public boolean remove(Alg alg) {
		return list.remove(alg);
	}

	
	/**
	 * Setting the specified algorithm to this list at specified index.
	 * @param index Specified index.
	 * @param alg Specified algorithm.
	 * @return replaced algorithm.
	 */
	public Alg set(int index, Alg alg) {
		return list.set(index, alg);
	}
	
	
	/**
	 * Retrieving index of the algorithm that has specified name.
	 * @param algName specified algorithm name.
	 * @return index of the algorithm that has specified name. Return -1 if there is no such algorithm.
	 */
	public int indexOf(String algName) {
		for (int i = 0; i < list.size(); i++) {
			Alg alg = list.get(i);
			if (alg.getName().equals(algName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Getting the size of this list.
	 * @return size of algorithm list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Clearning this list which means that remoiving all algorithms from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Converting this list to {@link List} of algorithms.
	 * @return {@link List} of algorithm.
	 */
	public List<Alg> toList() {
		List<Alg> newList = Util.newList();
		newList.addAll(list);
		
		return newList;
	}


	/**
	 * Extracting names of all algorithms in this list. 
	 * @return {@link List} of names of all algorithms in this list.
	 */
	public List<String> getAlgNameList() {
		return getAlgNameList(list);
	}
	
	
	/**
	 * Getting list of algorithm names.
	 * @return list of algorithm names.
	 */
	public static List<String> getAlgNameList(List<Alg> algList) {
		List<String> algNameList = Util.newList(algList.size());
		for (Alg alg : algList) {
			algNameList.add(alg.getName());
		}
		
		return algNameList;
	}

	
	/**
	 * Extracting class names of all algorithms in this list.
	 * @return {@link List} of class names of all algorithms in this list.
	 */
	public List<String> getAlgClassNameList() {
		List<String> algClassNameList = Util.newList();
		for (Alg alg : list) {
			algClassNameList.add(alg.getClass().getName());
		}
		
		return algClassNameList;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return TextParserUtil.toText(getAlgNameList(), ",");
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		AlgList newAlgList = new AlgList();
		newAlgList.list = clone(this.list);
		return newAlgList;
	}
	
	
	/**
	 * Cloning list of algorithms.
	 * @param algList specified list of algorithms.
	 * @return list of cloned algorithms.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Alg> List<T> clone(Collection<T> algList) {
		if (algList == null) return Util.newList();
		List<T> newAlgList = Util.newList(algList.size());
		
		for (T alg : algList) {
			T newAlg = (T) alg.newInstance();
			newAlg.getConfig().putAll(alg.getConfig());
			newAlgList.add(newAlg);
		}
		
		return newAlgList;
	}
	
	
	/**
	 * Creating {@link AlgList} from a string of class names. Class names in the string are separated by commas &quot;,&quot;.
	 * This is static method.
	 * @param classNameList String of class names. Of course, every class name refers exactly to some {@link Alg}.
	 * @return Algorithm list represented by {@link AlgList}
	 */
	public static AlgList create(String classNameList) {
		AlgList algList = new AlgList();
		
		List<String> textList = TextParserUtil.parseTextList(classNameList, ",");
		for (String text : textList) {
			Object obj = Util.newInstance(text);
			if (obj != null && obj instanceof Alg)
				algList.add((Alg) obj);
		}
		
		return algList;
	}
	
	
	
}
