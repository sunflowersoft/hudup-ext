/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Pair;

/**
 * This class is matrix of probabilities of users, in which each cell is probability array of a user with many items.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class ProbUserMatrix implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Number values per user such as 5 with values (1, 2, 3, 4, 5).
	 */
	protected int numberValuesPerUser = 0;
	
	
	/**
	 * Matrix of probabilities of users, in which each cell is probability array of a user with many items.
	 */
	protected Map<Integer, Map<Integer, double[]>> matrix = Util.newMap();
	
	
	/**
	 * Setting probability array at specified row ID and column ID.
	 * Note, both row ID and column ID are user id.
	 * This method is only called by method {@link #setup(DatasetStatsProcessor, Dataset)}.
	 * @param rowId row ID.
	 * @param colId column ID.
	 * @param probs probability array at specified row ID and column ID.
	 */
	private void set(int rowId, int colId, double[] probs) {
		Map<Integer, double[]> row = null;
		if (matrix.containsKey(rowId))
			row = matrix.get(rowId);
		else {
			row = Util.newMap();
			matrix.put(rowId, row);
		}
		row.put(colId, probs);
		
		if (rowId != colId) {
			double[] newprobs = new double[probs.length];
			
			for (int i = 0; i < probs.length; i++) {
				int r = i / numberValuesPerUser;
				int c = i % numberValuesPerUser;
				newprobs[c * numberValuesPerUser + r] = probs[i];
			}
			
			Map<Integer, double[]> col = null;
			if (matrix.containsKey(colId))
				col = matrix.get(colId);
			else {
				col = Util.newMap();
				matrix.put(colId, col);
			}
			col.put(rowId, newprobs);
		}
		
	}
	
	
	/**
	 * Getting probability array at specified row ID and column ID.
	 * Note, both row ID and column ID are user id.
	 * @param rowId row ID.
	 * @param colId column ID.
	 * @return probability array at specified row ID and column ID.
	 */
	public double[] get(int rowId, int colId) {
		return matrix.get(rowId).get(colId);
	}

	
	/**
	 * Getting probability array of specified user ID.
	 * @param id user ID.
	 * @return probability array of specified user ID.
	 */
	public double[] get(int id) {
		return matrix.get(id).get(id);
	}
	
	
	/**
	 * Checking whether probability matrix contains row id and column id.
	 * Note, both row ID and column ID are user id.
	 * @param rowId row ID.
	 * @param colId column ID.
	 * @return whether contains row id and column id.
	 */
	public boolean contains(int rowId, int colId) {
		if (!matrix.containsKey(rowId))
			return false;
		
		Map<Integer, double[]> row = matrix.get(rowId);
		return row.containsKey(colId);
		
	}
	
	
	/**
	 * Setting up (creating) this probabilities matrix from dataset.
	 * @param dataset specified dataset.
	 */
	public void setup(Dataset dataset) {
		this.matrix.clear();
		
		List<Integer> userIdList = Util.newList();
		userIdList.addAll(DatasetUtil.getRatingUserIds(dataset));
		
		this.numberValuesPerUser = dataset.getConfig().getNumberRatingsPerItem(); //The same to user.
		int minRating = (int)dataset.getConfig().getMinRating();
		
		DatasetStatsProcessor processor = new DatasetStatsProcessor(dataset);
		for (int u = 0; u < userIdList.size(); u++) {
			int id1 = userIdList.get(u);
			
			double[] probs0 = new double[numberValuesPerUser];
			for (int i = 0; i < numberValuesPerUser; i++) {
				probs0[i] = processor.probUser(id1, 
						DatasetUtil2.realRatingValueOf(i, minRating));
			}
			set(id1, id1, probs0);
			
			for (int v = u + 1; v < userIdList.size(); v++) {
				int id2 = userIdList.get(v);
				
				double[] probs = new double[numberValuesPerUser * numberValuesPerUser];
				for (int i = 0; i < numberValuesPerUser; i++) {
					Pair pair1 = new Pair(id1, 
							DatasetUtil2.realRatingValueOf(i, minRating));
					
					for (int j = 0; j < numberValuesPerUser; j++) {
						Pair pair2 = new Pair(id2, 
								DatasetUtil2.realRatingValueOf(j, minRating));
						
						List<Pair> pairs = Util.newList();
						pairs.add(pair1);
						pairs.add(pair2);
						
						probs[i * numberValuesPerUser + j] = processor.probUser(pairs);
					}
				}
				
				set(id1, id2, probs);
				
			} // End v
			
		} // End u
		
	}

	
	/**
	 * Getting number of values per user.
	 * @return the number of values per user.
	 */
	public int getNumberValuesPerUser() {
		return numberValuesPerUser;
	}
	
	
}
