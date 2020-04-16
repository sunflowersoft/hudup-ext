/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitItem;
import net.hudup.core.logistic.DSUtil;

/**
 * This class is matrix of AND and AND-NOT probabilities of binary items,
 * in which each cell is a pair of AND probability and AND-NOT probability between two binary items.
 * Such pair is called dual probability.
 * This matrix is called binary dual probability matrix or bit dual probability matrix.
 *  
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BitAndNotProbItemMatrix implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This class is a pair of AND probability and AND-NOT probability between two binary items.
	 * In other words, this is dual probability.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public class Prob implements Serializable {
		
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * AND probability.
		 */
		public double and = 0;
		
		/**
		 * AND-NOT probability.
		 */
		public double andNot = 0;
		
		/**
		 * Constructor with AND probability and AND-NOT probability.
		 * @param and AND probability.
		 * @param andNot AND-NOT probability.
		 */
		public Prob(double and, double andNot) {
			this.and = and;
			this.andNot = andNot;
		}
		
	}
	
	
	/**
	 * Items are translated into bit items. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key (like 9) is the bit item id and each value is a pair of real item id (like 1) and real rating value (like 4).
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();
	
	
	/**
	 * Main matrix of AND and AND-NOT probabilities of binary items.
	 * In other words, this is matrix of dual probabilities.
	 */
	protected Map<Integer, Map<Integer, Prob>> matrix = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public BitAndNotProbItemMatrix() {
		
	}
	
	
	/**
	 * Setting dual probability (AND and AND-NOT) at bit row ID and bit column ID.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * This method is only called by method {@link #set(int, int, Prob)}.
	 * @param bitRowId bit row ID.
	 * @param bitColId bit column ID.
	 * @param prob dual probability (AND and AND-NOT) at bit row ID and bit column ID.
	 */
	private void set(int bitRowId, int bitColId, Prob prob) {
		Map<Integer, Prob> row = null;
		if (matrix.containsKey(bitRowId))
			row = matrix.get(bitRowId);
		else {
			row = Util.newMap();
			matrix.put(bitRowId, row);
		}
		row.put(bitColId, prob);
		
		
	}
	
	
	/**
	 * Getting dual probability (AND and AND-NOT) at bit row ID and bit column ID.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitRowId bit row ID.
	 * @param bitColId bit column ID.
	 * @return dual probability (AND and AND-NOT) at bit row ID and bit column ID.
	 */
	public Prob get(int bitRowId, int bitColId) {
		return matrix.get(bitRowId).get(bitColId);
	}

	
	/**
	 * Getting AND probability at bit row ID and bit column ID.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitRowId bit row ID.
	 * @param bitColId bit column ID.
	 * @return AND probability at bit row ID and bit column ID.
	 */
	public double getAndProb(int bitRowId, int bitColId) {
		return get(bitRowId, bitColId).and;
	}
	
	
	/**
	 * Getting AND-NOT probability at bit row ID and bit column ID.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitRowId bit row ID.
	 * @param bitColId bit column ID.
	 * @return AND-NOT probability at bit row ID and bit column ID.
	 */
	public double getAndNotProb(int bitRowId, int bitColId) {
		return get(bitRowId, bitColId).andNot;
	}

	
	/**
	 * Getting dual probability (AND and AND-NOT) at bit item ID.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitId bit item ID.
	 * @return dual probability (AND and AND-NOT) at bit ID.
	 */
	public Prob get(int bitId) {
		return matrix.get(bitId).get(bitId);
	}
	
	
	/**
	 * Getting probability (AND probability) at bit item ID.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitId bit item ID.
	 * @return probability (AND probability) at bit ID.
	 */
	public double getProb(int bitId) {
		return get(bitId).and;
	}
	
	
	/**
	 * Checking whether dual probability matrix contains bit row id and bit column id.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * @param bitRowId bit row id.
	 * @param bitColId bit column id.
	 * @return whether dual probability matrix contains bit row id and bit column id.
	 */
	public boolean contains(int bitRowId, int bitColId) {
		return (matrix.containsKey(bitRowId) && 
				matrix.get(bitRowId).containsKey(bitColId));
	}
	
	
	/**
	 * Setting up (creating) this dual probabilities matrix from binary data.
	 * @param bitData binary data.
	 */
	public void setup(BitData bitData) {
		this.clear();
		
		List<Integer> bitIdList = Util.newList();
		bitIdList.addAll(bitData.bitItemIds());
		
		int n = bitIdList.size();
		int sessionSize = bitData.realSessionIds().size();
		for (int i = 0; i < n; i++) {
			int bitId1 = bitIdList.get(i);
			BitItem item1 = bitData.get(bitId1);
			BitSet bs1 = item1.bitItem().getBitSet();
			double prob = item1.getSupport();
			if (!Util.isUsed(prob)) continue;
			
			this.set(bitId1, bitId1, new Prob(prob, 0));
			
			for (int j = 0; j < n; j++) {
				if (j == i) continue;
				
				int bitId2 = bitIdList.get(j);
				BitItem item2 = bitData.get(bitId2);
				BitSet bs2 = item2.bitItem().getBitSet();
				
				double andProb = 0;
				if (contains(bitId2, bitId1)) {
					andProb = getAndProb(bitId2, bitId1);
				}
				else {
					BitSet bs = (BitSet)bs1.clone();
					bs.and(bs2);
					andProb = (double)DSUtil.countSetBit(bs) / (double)sessionSize;
				}
				if (!Util.isUsed(andProb)) continue;
				
				BitSet bs = (BitSet)bs1.clone();
				bs.andNot(bs2);
				double andNotProb = (double)DSUtil.countSetBit(bs) / (double)sessionSize;
				
				this.set(bitId1, bitId2, new Prob(andProb, andNotProb));
				
			}
			
			// Setting up bitItemMap_
			this.bitItemMap.put(bitId1, item1.pair());
		}
		
		
	}

	
	/**
	 * Clearing this matrix.
	 */
	public void clear() {
		this.bitItemMap.clear();
		this.matrix.clear();
	}
	
	
	/**
	 * Getting binary item identifiers.
	 * @return set of bit id (s).
	 */
	public Set<Integer> bitIds() {
		return bitItemMap.keySet();
	}
	
	
	/**
	 * Getting the original pair (real item id and real rating value) of a bit item id. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key (like 9) is the bit item id and each value is a pair of real item id (like 1) and real rating value (like 4).
	 * @param bitId bit item id.
	 * @return original pair (real item id and real rating value) of a bit item id.
	 */
	public Pair getPair(int bitId) {
		return bitItemMap.get(bitId);
	}
	
	
	/**
	 * Transferring (getting) map of bit item identifiers.
	 * @return map of map of bit item identifiers (map of pairs).
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key (like 9) is the bit item id and each value is a pair of real item id (like 1) and real rating value (like 4).
	 */
	public Map<Integer, Pair> transferBitItemMap() {
		Map<Integer, Pair> bitItemMap = Util.newMap();
		bitItemMap.putAll(this.bitItemMap);
		
		return bitItemMap;
	}
	
	
}
