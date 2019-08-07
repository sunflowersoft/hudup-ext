package net.hudup.core.data;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.MathUtil;


/**
 * The first row is list of user id (s). The first column is list of item id (s).
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class ItemPaddingMatrix extends PaddingMatrix {

	public ItemPaddingMatrix(Dataset dataset, RatingVector vRat) {
		super(dataset, vRat);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup(Dataset dataset, RatingVector vRating) {
		// TODO Auto-generated method stub

		
		RatingMatrix rMatrix = dataset.createItemMatrix();
		List<Integer> itemIdList = rMatrix.rowIdList;
		List<Integer> userIdList = rMatrix.columnIdList;
		double[][] matrix = rMatrix.matrix;
		
		List<double[]> arrList = Util.newList();
		
		double[] row0 = new double[userIdList.size() + 1];
		row0[0] = -1;
		for (int j = 0; j < userIdList.size(); j++)
			row0[j + 1] = userIdList.get(j);
		arrList.add(row0);
		
		for (int i = 0; i < itemIdList.size(); i++) {
			double[] row = new double[userIdList.size() + 1];
			row[0] = itemIdList.get(i);
			for (int j = 0; j < userIdList.size(); j++) {
				row[j + 1] = matrix[i][j];
			}
			
			arrList.add(row);
		}
		
		
		if (vRating == null || vRating.size() == 0) {
			this.matrix = arrList.toArray(new double[0][]);
			return;
		}
			
		int itemId = vRating.id();
		int idx = itemIdList.indexOf(itemId);
		double[] row = DSUtil.toDoubleArray(vRating.toValueList(userIdList));
		double[] thisrow = null;
		if (idx != -1)
			thisrow = arrList.get(idx + 1);
		else {
			thisrow = new double[userIdList.size() + 1];
			arrList.add(thisrow);
		}
	
		for (int j = 0; j < userIdList.size(); j++)
			thisrow[j + 1] = row[j];
		
		this.matrix = arrList.toArray(new double[0][]);
	}

	
	@Override
	public List<Integer> getUserIdList() {
		// TODO Auto-generated method stub
		List<Integer> userIdList = Util.newList();
		
		double[] row0 = matrix[0];
		for (int i = 1; i < row0.length; i++)
			userIdList.add((int)row0[i]);
		
		return userIdList;
	}

	
	@Override
	public List<Integer> getItemIdList() {
		// TODO Auto-generated method stub
		List<Integer> itemIdList = Util.newList();
		
		for (int i = 1; i < matrix.length; i++) {
			itemIdList.add((int)matrix[i][0]);
		}
		
		return itemIdList;
	}


	@Override
	public double getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		double[] itemRow = getItemRatingVector(itemId);
		
		List<Integer> userIdList = getUserIdList();
		int idx = userIdList.indexOf(userId);
		
		return itemRow[idx];
	}

	
	@Override
	public double[] getUserRatingVector(int userId) {
		int n = matrix[0].length;
		for (int i = 1; i < n; i++) {
			double[] column = MathUtil.getColumnVector(matrix, i);
			if (column[0] == userId)
				return Arrays.copyOfRange(column, 1, column.length);
		}
		
		return null;
	}
	
	
	@Override
	public double[] getUserRatingVectorByIndex(int idx) {
		double[] column = MathUtil.getColumnVector(matrix, idx + 1);
		return Arrays.copyOfRange(column, 1, column.length);
	}
	

	@Override
	public double[] getItemRatingVector(int itemId) {
		int n = matrix.length;
		for (int i = 1; i < n; i++) {
			double[] row = matrix[i];
			if (row[0] == itemId)
				return Arrays.copyOfRange(row, 1, row.length);
		}
		
		return null;
	}
	
	
	@Override
	public double[] getItemRatingVectorByIndex(int idx) {
		double[] row = matrix[idx + 1];
		return Arrays.copyOfRange(row, 1, row.length);
		
	}


	@Override
	public int numberOfUsers() {
		// TODO Auto-generated method stub
		return matrix[0].length - 1;
	}

	@Override
	public int numberOfItems() {
		// TODO Auto-generated method stub
		return matrix.length - 1;
	}

	
	@Override
	public Point getRowCol(int userId, int itemId) {
		List<Integer> itemIdList = getItemIdList();
		List<Integer> userIdList = getUserIdList();
		
		int row = itemIdList.indexOf(itemId);
		int col = userIdList.indexOf(userId);
		
		return new Point(col, row);
	}

	
	
	
}
