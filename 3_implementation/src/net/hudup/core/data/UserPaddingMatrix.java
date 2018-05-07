package net.hudup.core.data;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.MathUtil;


/**
 * This class represents a matrix where the first row is list of item id (s). The first column is list of user id (s)
 * @author Loc Nguyen
 * @version 10.0
 */
public class UserPaddingMatrix extends PaddingMatrix {

	
	/**
	 * Constructor with specified dataset and rating vector.
	 * @param dataset specified dataset.
	 * @param vRating specified rating vector.
	 */
	public UserPaddingMatrix(Dataset dataset, RatingVector vRating) {
		super(dataset, vRating);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void setup(Dataset dataset, RatingVector vRating) {
		// TODO Auto-generated method stub

		RatingMatrix rMatrix = dataset.createUserMatrix();
		List<Integer> userIdList = rMatrix.rowIdList;
		List<Integer> itemIdList = rMatrix.columnIdList;
		double[][] matrix = rMatrix.matrix;

		List<double[]> arrList = Util.newList();
		
		double[] row0 = new double[itemIdList.size() + 1];
		row0[0] = -1;
		for (int j = 0; j < itemIdList.size(); j++)
			row0[j + 1] = itemIdList.get(j);
		arrList.add(row0);
		
		for (int i = 0; i < userIdList.size(); i++) {
			double[] row = new double[itemIdList.size() + 1];
			row[0] = userIdList.get(i);
			for (int j = 0; j < itemIdList.size(); j++) {
				row[j + 1] = matrix[i][j];
			}
			
			arrList.add(row);
		}
		
		
		if (vRating == null || vRating.size() == 0) {
			this.matrix = arrList.toArray(new double[0][]);
			return;
		}
			
		int userId = vRating.id();
		int idx = userIdList.indexOf(userId);
		double[] row = DSUtil.toDoubleArray(vRating.toValueList(itemIdList));
		double[] thisrow = null;
		if (idx != -1)
			thisrow = arrList.get(idx + 1);
		else {
			thisrow = new double[itemIdList.size() + 1];
			arrList.add(thisrow);
		}
	
		for (int j = 0; j < itemIdList.size(); j++)
			thisrow[j + 1] = row[j];
		
		this.matrix = arrList.toArray(new double[0][]);
	}

	
	@Override
	public List<Integer> getUserIdList() {
		// TODO Auto-generated method stub
		List<Integer> userIdList = Util.newList();
		
		for (int i = 1; i < matrix.length; i++) {
			userIdList.add((int)matrix[i][0]);
		}
		
		return userIdList;
	}

	
	@Override
	public List<Integer> getItemIdList() {
		// TODO Auto-generated method stub
		List<Integer> itemIdList = Util.newList();
		
		double[] row0 = matrix[0];
		for (int i = 1; i < row0.length; i++)
			itemIdList.add((int)row0[i]);
		
		return itemIdList;
	}
	
	
	@Override
	public double getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		double[] userRow = getUserRatingVector(userId);
		
		List<Integer> itemIdList = getItemIdList();
		int idx = itemIdList.indexOf(itemId);
		
		return userRow[idx];
	}

	
	@Override
	public double[] getUserRatingVector(int userId) {
		int n = matrix.length;
		for (int i = 1; i < n; i++) {
			double[] row = matrix[i];
			if (row[0] == userId)
				return Arrays.copyOfRange(row, 1, row.length);
		}
		
		return null;
	}
	
	
	@Override
	public double[] getUserRatingVectorByIndex(int idx) {
		double[] row = matrix[idx + 1];
		return Arrays.copyOfRange(row, 1, row.length);
	}
	
	
	@Override
	public double[] getItemRatingVector(int itemId) {
		int n = matrix[0].length;
		for (int i = 1; i < n; i++) {
			double[] column = MathUtil.getColumnVector(matrix, i);
			if (column[0] == itemId)
				return Arrays.copyOfRange(column, 1, column.length);
		}
		
		return null;
	}
	
	
	@Override
	public double[] getItemRatingVectorByIndex(int idx) {
		double[] column = MathUtil.getColumnVector(matrix, idx);
		return Arrays.copyOfRange(column, 1, column.length);
		
	}

	
	@Override
	public int numberOfUsers() {
		// TODO Auto-generated method stub
		return matrix.length - 1;
	}

	
	@Override
	public int numberOfItems() {
		// TODO Auto-generated method stub
		return matrix[0].length - 1;
	}
	
	
	@Override
	public Point getRowCol(int userId, int itemId) {
		List<Integer> userIdList = getUserIdList();
		List<Integer> itemIdList = getItemIdList();
		
		int row = userIdList.indexOf(userId);
		int col = itemIdList.indexOf(itemId);
		
		return new Point(col, row);
	}
	
	
}
