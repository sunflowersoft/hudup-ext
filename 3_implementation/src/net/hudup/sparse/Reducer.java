package net.hudup.sparse;

import java.util.Arrays;
import java.util.List;

import Jama.SingularValueDecomposition;
import flanagan.math.Matrix;
import net.hudup.core.Util;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingMatrixMetadata;
import net.hudup.core.data.RatingVector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class Reducer {
	
	
	/**
	 * 
	 */
	protected double reduceRatio = 0.5;
	
	
	/**
	 * 
	 */
	public Reducer(double reduceRatio) {
		this.reduceRatio = reduceRatio;
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param updateMetadata
	 * @return reduced matrix
	 */
	public RatingMatrix simplyReduce(RatingMatrix matrix, boolean updateMetadata) {
		
		List<double[]> newMatrix = Util.newList();
		List<Integer> newRowIdList = Util.newList(); 
		
		for (int i = 0; i < matrix.matrix.length; i++) {
			
			double[] row = matrix.matrix[i];
			
			int count = RatingVector.count(row, true);
			double cRatio = (double)count / (double)matrix.columnIdList.size();
			
			if (cRatio >= 1.0 - reduceRatio) {
				newMatrix.add(row);
				newRowIdList.add(matrix.rowIdList.get(i));
			}
		}
		
		List<Integer> newColIdList = Util.newList();
		newColIdList.addAll(matrix.columnIdList);
		
		RatingMatrix reducedMatrix = RatingMatrix.assign(
				newMatrix.toArray(new double[0][]), 
				newRowIdList, 
				newColIdList,
				(RatingMatrixMetadata) matrix.metadata.clone());
		
		if (updateMetadata)
			reducedMatrix.updateMetadata();
		return reducedMatrix;
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param updateMetadata
	 * @return reduced matrix
	 */
	public RatingMatrix svdReduce(RatingMatrix matrix, boolean updateMetadata) {
		
		List<Integer> rowIdList = matrix.rowIdList;
		List<Integer> columnIdList = matrix.columnIdList;

		if (rowIdList.size() == 0 || columnIdList.size() == 0)
			return null;
		
		new SparseProcessor().columnMeanComplete(matrix.matrix);
		
		Jama.Matrix X = new Jama.Matrix(matrix.matrix);
		SingularValueDecomposition svd = new SingularValueDecomposition(X);
		double[] singularValues = svd.getSingularValues();
		int reduceLength = Math.min(
					singularValues.length,  
					(int) ( (1.0 - reduceRatio) * Math.max(rowIdList.size(), columnIdList.size())) 
				);
		singularValues = Arrays.copyOf(singularValues, reduceLength);
		
		Matrix U = new Matrix(svd.getU().getArray());
		U = U.getSubMatrix(0, 0, reduceLength -1, reduceLength - 1);
		//
		Matrix S = Matrix.diagonalMatrix(reduceLength, singularValues);
		//
		Matrix V = new Matrix(svd.getV().getArray());
		V = V.getSubMatrix(0, 0, reduceLength -1, reduceLength -1);
		
		Matrix Y = U.times(S).times(V.transpose());
		//
		List<Integer> newRowIdList = Util.newList();
		newRowIdList.addAll(rowIdList.subList(0, reduceLength));
		//
		List<Integer> newColumnIds = Util.newList();
		newColumnIds.addAll(columnIdList.subList(0, reduceLength));
		
		RatingMatrix reducedMatrix = RatingMatrix.assign(
				Y.getArrayPointer(), 
				newRowIdList, 
				newColumnIds,
				(RatingMatrixMetadata) matrix.metadata.clone());
		
		if (updateMetadata)
			reducedMatrix.updateMetadata();
		return reducedMatrix;
	}

	
	
	

}
