/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.logistic.math.BitAndNotProbItemMatrix.Prob;

/**
 * This class is matrix of AND probabilities of binary items, in which each cell is a pair of AND probability between two binary items.
 * This matrix is called binary probability matrix or bit probability matrix.
 *  
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BitProbItemMatrix implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Items are translated into bit items. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key (like 9) is the bit item id and each value is a pair of real item id (like 1) and real rating value (like 4).
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();
	
	
	/**
	 * Main matrix of AND probabilities of binary items.
	 */
	protected Map<Integer, Map<Integer, Double>> matrix = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public BitProbItemMatrix() {
		
	}
	
	
	/**
	 * Setting AND probability at bit row ID and bit column ID.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * This method is only called by method {@link #set(int, int, Prob)}.
	 * @param bitRowId bit row ID.
	 * @param bitColId bit column ID.
	 * @param prob AND probability at bit row ID and bit column ID.
	 */
	private void set(int bitRowId, int bitColId, double prob) {
		Map<Integer, Double> row = null;
		if (matrix.containsKey(bitRowId))
			row = matrix.get(bitRowId);
		else {
			row = Util.newMap();
			matrix.put(bitRowId, row);
		}
		row.put(bitColId, prob);
		
		if (bitRowId != bitColId) {
			Map<Integer, Double> col = null;
			if (matrix.containsKey(bitColId))
				col = matrix.get(bitColId);
			else {
				col = Util.newMap();
				matrix.put(bitColId, col);
			}
			col.put(bitRowId, prob);
		}
		
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
	public double get(int bitRowId, int bitColId) {
		return matrix.get(bitRowId).get(bitColId);
	}

	
	/**
	 * Getting AND probability at bit item ID.
	 * Please call method {@link #contains(int, int)} before calling this method.
	 * If method {@link #contains(int, int)} returns false, the probability does not exist.
	 * @param bitId bit item ID.
	 * @return probability (AND probability) at bit ID.
	 */
	public double get(int bitId) {
		return matrix.get(bitId).get(bitId);
	}
	
	
	/**
	 * Checking whether AND probability matrix contains bit row id and bit column id.
	 * Note, both bit row ID and bit column ID are bit item id.
	 * @param bitRowId bit row id.
	 * @param bitColId bit column id.
	 * @return whether AND probability matrix contains bit row id and bit column id.
	 */
	public boolean contains(int bitRowId, int bitColId) {
		return (matrix.containsKey(bitRowId) && 
				matrix.get(bitRowId).containsKey(bitColId));
	}
	
	
	/**
	 * Setting up (creating) this probabilities matrix from binary data.
	 * @param bitData binary data.
	 */
	public void setup(BitData bitData) {
		this.clear();
		
		List<Integer> bitIdList = Util.newList();
		bitIdList.addAll(bitData.bitItemIds());
		
		BitDatasetStatsProcessor processor = 
				new BitDatasetStatsProcessor(bitData);
		int n = bitIdList.size();
		for (int i = 0; i < n; i++) {
			int bitId1 = bitIdList.get(i);
			double prob = processor.prob(bitId1);
			if (!Util.isUsed(prob)) continue;
			
			this.set(bitId1, bitId1, prob);
			
			for (int j = i + 1; j < n; j++) {
				int bitId2 = bitIdList.get(j);
				double probAnd = processor.probAnd(bitId1, bitId2);
				if (!Util.isUsed(probAnd)) continue;
				
				this.set(bitId1, bitId2, probAnd);
				
			}
			
			// Setting up bitItemMap_
			this.bitItemMap.put(bitId1, bitData.get(bitId1).pair());
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
	
	
	/**
	 * Finding bit item id of given pair of real item id and real rating value.
	 * @param realItemId real item id.
	 * @param rating real rating value.
	 * @return bit item id of given pair of real item id and real rating value.
	 */
	public int findBitItemIdOf(int realItemId, double rating) {
		return BitDataUtil.findBitItemIdOf(bitItemMap, realItemId, rating);
	}
	
	
}



