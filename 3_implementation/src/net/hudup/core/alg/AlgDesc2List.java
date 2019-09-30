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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Util;

/**
 * This class represents a list of extended algorithm descriptions.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgDesc2List implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Built-in list of extended algorithm descriptions.
	 */
	protected List<AlgDesc2> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public AlgDesc2List() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with a collection of extended algorithm descriptions.
	 * @param algDescList specified collection of extended algorithm descriptions.
	 */
	public AlgDesc2List(Collection<AlgDesc2> algDescList) {
		this.list.addAll(algDescList);
	}
	
	
	/**
	 * Constructor with other list of extended algorithm descriptions.
	 * @param algList other list of extended algorithm descriptions.
	 */
	public AlgDesc2List(AlgList algList) {
		addAll(algList);
	}

	
	/**
	 * Constructor with an extended algorithm description and so the list has only one extended algorithm description.
	 * @param algDesc specified extended algorithm description.
	 */
	public AlgDesc2List(AlgDesc2 algDesc) {
		this.list.add(algDesc);
	}
	
	
	/**
	 * Constructor with an algorithm and so the list has only one extended algorithm description.
	 * @param alg specified algorithm.
	 */
	public AlgDesc2List(Alg alg) {
		this(new AlgDesc2(alg));
	}

	
	/**
	 * Getting an extended algorithm description at specified index.
	 * @param index specified index.
	 * @return Extended algorithm description at specified index.
	 */
	public AlgDesc2 get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Adding an extended algorithm description at the end of the list.
	 * @param algDesc specified extended algorithm description.
	 * @return whether adding extended algorithm description successfully
	 */
	public boolean add(AlgDesc2 algDesc) {
		return list.add(algDesc);
	}
	
	
	/**
	 * Given an algorithm, adding the extended description of such algorithm at the end of the list.
	 * @param alg Specified algorithm
	 * @return whether adding extended algorithm description successfully
	 */
	public boolean add(Alg alg) {
		return list.add(new AlgDesc2(alg));
	}

	
	/**
	 * Adding other list of extended algorithm descriptions at the end of this list.
	 * @param algDescList Specified list of extended algorithm descriptions
	 * @return whether adding extended algorithm descriptions successfully
	 */
	public boolean addAll(AlgDesc2List algDescList) {
		return list.addAll(algDescList.list);
	}
	
	
	/**
	 * Adding extended descriptions of specified list of algorithms and adding such extended description at the end of this list.
	 * @param algList Specified list of algorithms.
	 * @return whether adding extended algorithm descriptions successfully
	 */
	public boolean addAll(AlgList algList) {
		for (int i = 0; i < algList.size(); i++) {
			add(algList.get(i));
		}
		
		return true;
	}

	
	/**
	 * Adding collection of extended algorithm descriptions at the end of this list.
	 * @param algDescs Collection of extended algorithm descriptions
	 * @return whether adding extended algorithm descriptions successfully
	 */
	public boolean addAll(Collection<AlgDesc2> algDescs) {
		return list.addAll(algDescs);
	}

	
	/**
	 * Adding extended descriptions of specified list of algorithms and adding such extended description at the end of this list.
	 * @param algList Specified list of algorithms.
	 * @return whether adding extended algorithm descriptions successfully
	 */
	public boolean addAll2(Collection<Alg> algList) {
		for (Alg alg : algList) {
			add(alg);
		}
		
		return true;
	}

	
	/**
	 * Removing a extended algorithm description at specified index.
	 * @param index Specified index
	 * @return removed extended algorithm description
	 */
	public AlgDesc2 remove(int index) {
		return list.remove(index);
	}
	
	
	/**
	 * Setting the specified extended algorithm description at specified index.
	 * @param index Specified index
	 * @param algDesc Specified extended algorithm description
	 * @return replaced extended algorithm description
	 */
	public AlgDesc2 set(int index, AlgDesc2 algDesc) {
		return list.set(index, algDesc);
	}
	
	
	/**
	 * Given an algorithm, setting the extended description of such algorithm at specified index.
	 * @param index Specified index
	 * @param alg Specified algorithm
	 * @return replaced extended algorithm description
	 */
	public AlgDesc2 set(int index, Alg alg) {
		return set(index, new AlgDesc2(alg));
	}

	
	/**
	 * Getting the size of this list.
	 * @return size of extended algorithm description list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Clearing this list, which means that removing all extended algorithm descriptions from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Converting this list to the list of extended algorithm descriptions.
	 * @return list of extended algorithm descriptions
	 */
	public List<AlgDesc2> toList() {
		List<AlgDesc2> newList = Util.newList();
		newList.addAll(list);
		
		return newList;
	}

	
	/**
	 * Sorting this list with specified comparator.
	 * @param comparator specified comparator.
	 */
	public void sort(Comparator<AlgDesc2> comparator) {
		Collections.sort(list, comparator);
	}
	

}
