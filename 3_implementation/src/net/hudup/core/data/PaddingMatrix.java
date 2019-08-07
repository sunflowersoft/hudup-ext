package net.hudup.core.data;

import java.awt.Point;
import java.util.List;

import net.hudup.core.logistic.MathUtil;


/**
 * This class represents a matrix where the first row is list of item or user identifiers. The first column is list of user or item identifiers.
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public abstract class PaddingMatrix {
	
	
	/**
	 * Internal rating matrix.
	 */
	protected double[][] matrix = new double[0][];
	
	
	/**
	 * Constructor with specified dataset and rating vector.
	 * @param dataset specified dataset.
	 * @param vRating specified rating vector.
	 */
	public PaddingMatrix(Dataset dataset, RatingVector vRating) {
		setup(dataset, vRating);
	}
	
	
	/**
	 * Setting up this matrix with specified dataset and rating vector.
	 * @param dataset specified dataset.
	 * @param vRating specified rating vector.
	 */
	public abstract void setup(Dataset dataset, RatingVector vRating);
	
	
	/**
	 * Getting list of user id (s).
	 * @return list of user id (s).
	 */
	public abstract List<Integer> getUserIdList();

	
	/**
	 * Getting list of item id (s).
	 * @return list of item id (s).
	 */
	public abstract List<Integer> getItemIdList();

	
	/**
	 * Sorting rows of this matrix according to aligned row.
	 * @param align aligned row.
	 */
	public void sortRows(double[] align) {
		sortRows0(align);
	}
	
	
	/**
	 * Sorting rows of this matrix according to aligned column.
	 * @param align aligned column.
	 */
	public void sortColumns(double[] align) {
		matrix = MathUtil.transpose(matrix);
		sortRows(align);
		matrix = MathUtil.transpose(matrix);
	}
	
	
	/**
	 * Getting rating value at specified row and column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return rating value at specified row and column.
	 */
	public double getRatingByIndex(int row, int column) {
		return matrix[row + 1][column + 1];
	}

	
	/**
	 * Calculating the distance between the specified padding row and aligned row.
	 * @param vPadding specified padding row
	 * @param align specified aligned row.
	 * @return distance between two rows.
	 */
	private double distance(double[] vPadding, double[] align) {
		double[] vector = new double[vPadding.length - 1];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vPadding[i + 1];
		}
		
		return MathUtil.manhattanDistance(vector, align);
	}

	
	/**
	 * Sorting rows of this matrix according to aligned row.
	 * @param align aligned row.
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
	 * Getting rating value with specified user identifier and item identifier.
	 * @param userId specified user identifier.
	 * @param itemId specified item identifier.
	 * @return rating value with specified user identifier and item identifier.
	 */
	public abstract double getRating(int userId, int itemId);
	
	
	
	/**
	 * Getting rating vector of specified user identifier.
	 * @param userId specified user identifier.
	 * @return user rating vector of specified user identifier.
	 */
	public abstract double[] getUserRatingVector(int userId);
	

	/**
	 * Getting user rating vector by specified index.
	 * @param idx specified index.
	 * @return user rating vector at specified index.
	 */
	public abstract double[] getUserRatingVectorByIndex(int idx);
	

	/**
	 * Getting rating vector of specified item identifier.
	 * @param itemId specified item identifier.
	 * @return item rating vector of specified item identifier.
	 */
	public abstract double[] getItemRatingVector(int itemId);
	
	
	/**
	 * Getting item rating vector by specified index.
	 * @param idx specified index.
	 * @return item rating vector at specified index.
	 */
	public abstract double[] getItemRatingVectorByIndex(int idx);

	
	/**
	 * Getting row and column of specified user identifier and item identifier.
	 * @param userId specified user identifier.
	 * @param itemId specified item identifier.
	 * @return {@link Point} as row and column of specified user identifier and item identifier.
	 */
	public abstract Point getRowCol(int userId, int itemId);
	
	
	/**
	 * Getting the number of users.
	 * @return number of users.
	 */
	public abstract int numberOfUsers();
		

	/**
	 * Getting the number of items.
	 * @return number of items.
	 */
	public abstract int numberOfItems();
	
	
}
