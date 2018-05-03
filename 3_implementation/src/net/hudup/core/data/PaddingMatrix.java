package net.hudup.core.data;

import java.awt.Point;
import java.util.List;

import net.hudup.core.logistic.MathUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class PaddingMatrix {
	
	
	/**
	 * 
	 */
	protected double[][] matrix = new double[0][];
	
	
	/**
	 * 
	 * @param dataset
	 * @param vRating
	 */
	public PaddingMatrix(Dataset dataset, RatingVector vRating) {
		setup(dataset, vRating);
	}
	
	/**
	 * 
	 * @param dataset
	 * @param vRating
	 */
	public abstract void setup(Dataset dataset, RatingVector vRating);
	
	
	/**
	 * 
	 * @return list of user id (s)
	 */
	public abstract List<Integer> getUserIdList();

	
	/**
	 * 
	 * @return list of item id (s)
	 */
	public abstract List<Integer> getItemIdList();

	
	/**
	 * 
	 * @param align
	 */
	public void sortRows(double[] align) {
		sortRows0(align);
	}
	
	
	/**
	 * 
	 * @param align
	 */
	public void sortColumns(double[] align) {
		matrix = MathUtil.transpose(matrix);
		sortRows(align);
		matrix = MathUtil.transpose(matrix);
	}
	
	
	/**
	 * 
	 * @param row
	 * @param column
	 * @return rating value
	 */
	public double getRatingByIndex(int row, int column) {
		return matrix[row + 1][column + 1];
	}

	
	/**
	 * 
	 * @param vPadding
	 * @param align
	 * @return distance between two rows 
	 */
	private double distance(double[] vPadding, double[] align) {
		double[] vector = new double[vPadding.length - 1];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vPadding[i + 1];
		}
		
		return MathUtil.manhattanDistance(vector, align);
	}

	
	/**
	 * 
	 * @param align
	 */
	private void sortRows0(double[] align) {
		
		int n = matrix.length;
		
		for (int i = 2; i < n; i++) { 
			double[] vector = matrix[i];
			double dis = distance(vector, align);

			int j = i;

			while (j > 1 && 
					distance(matrix[j - 1], align) > dis) {

				matrix[j] = matrix[j - 1];

				j--; 

			}

			matrix[j] = vector;

		}
		
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return rating value
	 */
	public abstract double getRating(int userId, int itemId);
	
	
	
	/**
	 * 
	 * @param userId
	 * @return user rating vector
	 */
	public abstract double[] getUserRatingVector(int userId);
	

	/**
	 * 
	 * @param idx
	 * @return user rating vector
	 */
	public abstract double[] getUserRatingVectorByIndex(int idx);
	

	/**
	 * 
	 * @param itemId
	 * @return item rating vector
	 */
	public abstract double[] getItemRatingVector(int itemId);
	
	
	/**
	 * 
	 * @param idx
	 * @return item rating vector
	 */
	public abstract double[] getItemRatingVectorByIndex(int idx);

	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return {@link Point}
	 */
	public abstract Point getRowCol(int userId, int itemId);
	
	
	/**
	 * 
	 * @return number of users
	 */
	public abstract int numberOfUsers();
		

	/**
	 * 
	 * @return number of items
	 */
	public abstract int numberOfItems();
	
	
}
