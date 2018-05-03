/**
 * 
 */
package net.hudup.logistic.mining;

import java.util.List;

import net.hudup.core.Util;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Cluster {
	
	/**
	 * 
	 */
	protected List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * 
	 */
	protected List<double[]> instances = Util.newList();
	
	
	/**
	 * 
	 */
	public Cluster() {
	
	}
	
	
	/**
	 * 
	 * @param idx
	 * @return instance
	 */
	public double[] getInstance(int idx) {
		return instances.get(idx);
	}
	
	
	/**
	 * 
	 * @return number of instance
	 */
	public int numInstance() {
		return instances.size();
	}
	
	
	/**
	 * 
	 * @param instance
	 */
	public void addInstance(double[] instance) {
		instances.add(instance);
	}
	
	
	/**
	 * 
	 * @return list of id (s)
	 */
	public List<Integer> getIdList() {
		return rowIdList;
	}
	
	
	/**
	 * 
	 * @return matrix
	 */
	public double[][] toMatrix() {
		double[][] matrix = new double[instances.size()][];
		
		for (int i = 0; i < instances.size(); i++) {
			double[] row = instances.get(i);
			matrix[i] = row;
		}
		
		return matrix;
	}
	
	
}


