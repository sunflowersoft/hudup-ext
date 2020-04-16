/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents a vector of double values. The Java built-in {@code java.util.Vector} is the more general one.
 * So the use of this class is restricted but it provides some useful statistical methods for calculating mean, standard error, correlation coefficient, etc.
 * Some other arithmetic methods for calculating add, subtract, multiply, divide, product, cosine, etc. are also useful.
 * This class contains an internal array of double values {@link #data}.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Vector2 implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal array of double values.
	 */
	protected List<Double> data = Util.newList();
	
	
	/**
	 * Constructor with specified dimension and specified initial value.
	 * Dimension is the number of values in this vector. All values in this vector are assigned by the specified initial value.
	 * @param dim specified dimension.
	 * @param initValue specified initial value.
	 */
	public Vector2(int dim, double initValue) {
		data = DSUtil.initDoubleList(dim, initValue);
	}
	
	
	/**
	 * Constructor with specified array of double values.
	 * @param data specified array of double values.
	 */
	public Vector2(double[] data) {
		this.data = DSUtil.toDoubleList(data);
	}

	
	/**
	 * Constructor with specified list of double values.
	 * @param data specified collection of double values.
	 */
	public Vector2(Collection<Double> data) {
		this.data = DSUtil.toDoubleList(data);
	}
	
	
	/**
	 * Getting the dimension of this vector. Note, dimension is the number of values in this vector.
	 * @return dimension of this vector.
	 */
	public int dim() {
		return data.size();
	}
	
	
	/**
	 * Getting the value at specified index. 
	 * @param idx specified index.
	 * @return value at specified index.
	 */
	public double get(int idx) {
		return data.get(idx);
	}
	
	
	/**
	 * Setting a value at specified index.
	 * @param idx specified index.
	 * @param value to be set at specified index.
	 * @return the previous value that is replaced by current value.
	 */
	public double set(int idx, double value) {
		return data.set(idx, value);
	}
	
	
	/**
	 * Adding specified value.
	 * @param value specified value.
	 * @return true if adding is successful.
	 */
	public boolean add(double value) {
		return data.add(value);
	}
	
	
	/**
	 * Removing value at specified index.
	 * @param idx specified index.
	 * @return removed value.
	 */
	public double remove(int idx) {
		return data.remove(idx);
	}
	
	
	/**
	 * Calculating the module (length) of this vector.
	 * For example, the module of vector (3, 4) is Sqrt(3^2 + 4^2) = 5. 
	 * @return module of this vector.
	 */
	public double module() {
		double module = 0;
		for (double value : data) {
			module += value * value;
		}
		
		return Math.sqrt(module);
	}
	
	
	/**
	 * Calculating mean (average value) of this vector.
	 * @return mean of this vector.
	 */
	public double mean() {
		if (data.size() == 0)
			return Constants.UNUSED;
		
		double sum = 0;
		for (double value : data) {
			sum += value;
		}
		return sum / data.size();
	}

	
	/**
	 * Calculating variance of this vector.
	 * @return variance of this vector.
	 */
	public double var() {
		if (data.size() < 2)
			return Constants.UNUSED;

		double mean = mean();
		double sum = 0;
		for (double value : data) {
			double deviation = value - mean;
			sum += deviation * deviation;
		}
		return sum / (double)(data.size() - 1);
	}
	
	
	/**
	 * Calculating variance of this vector according to maximum likelihood estimation (MLE) method.
	 * @return variance of this vector according to maximum likelihood estimation (MLE) method.
	 */
	public double mleVar() {
		if (data.size() == 0)
			return Constants.UNUSED;

		double mean = mean();
		double sum = 0;
		for (double value : data) {
			double deviation = value - mean;
			sum += deviation * deviation;
		}
		return sum / (double)data.size();
	}

	
	/**
	 * Calculating standard deviation of this vector.
	 * @return standard deviation of this vector.
	 */
	public double sd() {
		return Math.sqrt(var());
	}

	
	/**
	 * Calculating standard error of this vector.
	 * @return standard error of this vector.
	 */
	public double se() {
		if (data.size() == 0)
			return Constants.UNUSED;
		else
			return Math.sqrt(var() / data.size());
	}
	
	
	/**
	 * Calculating Euclidean distance between this vector and the other vector.
	 * @param other other vector.
	 * @return Euclidean distance between this vector and the other vector.
	 */
	public double distance(Vector2 other) {
		double dis = 0;
		
		int n = Math.min(this.data.size(), other.data.size());
		for (int i = 0; i < n; i++) {
			double deviate =  this.data.get(i) - other.data.get(i);
			dis += deviate * deviate;
		}
		return Math.sqrt(dis);
	}
	
	
	/**
	 * Calculating the dot product (scalar product) of this vector and the other vector.
	 * @param other other vector
	 * @return dot product (scalar product) of this vector and the other vector.
	 */
	public double product(Vector2 other) {
		double product = 0;
		int n = Math.min(this.data.size(), other.data.size());
		for (int i = 0; i < n; i++) {
			product += this.data.get(i) * other.data.get(i);
		}
		
		return product;
	}
	
	
	/**
	 * Calculating the cosine of this vector and the other vector.
	 * @param other other vector.
	 * @return cosine of this vector and the other vector.
	 */
	public double cosine(Vector2 other) {
		double module1 = module();
		double module2 = other.module();
		if (module1 == 0 || module2 == 0)
			return Constants.UNUSED;
		else
			return product(other) / (module1 * module2);
	}
	
	
	/**
	 * Calculating the normalized cosine of this vector and the other vector.
	 * @param other other vector.
	 * @param average averaged value.
	 * @return normalized cosine of this vector and the other vector.
	 */
	public double cosine(Vector2 other, double average) {
		double product = 0;
		double length1 = 0;
		double length2 = 0;
		int n = Math.min(this.data.size(), other.data.size());
		for (int i = 0; i < n; i++) {
			double value1 = this.data.get(i) - average;
			double value2 = other.data.get(i) - average;
			
			length1 += value1 * value1;
			length2 += value2 * value2;
			product += value1 * value2;
		}
		
		if (length1 == 0 || length2 == 0)
			return Constants.UNUSED;
		else
			return product / Math.sqrt(length1 * length2);
	}

	
	/**
	 * Calculating the correlation coefficient of this vector and the other vector.
	 * @param other other vector.
	 * @return correlation coefficient of this vector and the other vector.
	 */
	public double corr(Vector2 other) {
		double mean1 = mean();
		double mean2 = other.mean();
		
		int n = Math.min(this.data.size(), other.data.size());
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int i = 0; i < n; i++) {
			double deviate1 = data.get(i) - mean1;
			double deviate2 = other.data.get(i) - mean2;
			
			VX += deviate1 * deviate1;
			VY += deviate2 * deviate2;
			VXY += deviate1 * deviate2;
		}
		
		if (VX == 0 || VY == 0)
			return Constants.UNUSED;
		else
			return VXY / Math.sqrt(VX * VY);
	}
	
	
	/**
	 * Concatenating this vector with the specified vector so that this vector contains values of specified vector.
	 * @param vector specified vector.
	 */
	public void concat(Vector2 vector) {
		List<Double> newdata = Util.newList(this.dim() + vector.dim());
		newdata.addAll(this.data);
		newdata.addAll(vector.data);
		this.data = newdata;
	}
	
	
	/**
	 * Subtracting this vector by specified vector.
	 * @param that specified vector.
	 * @return resulted vector from subtracting this vector by specified vector.
	 */
	public Vector2 subtract(Vector2 that) {
		int n = Math.min(this.dim(), that.dim());
		Vector2 result = new Vector2(n, 0);
		for (int i = 0; i < n; i++) {
			result.data.set(i, this.data.get(i) - that.data.get(i));
		}
		
		return result;
	}
	
	
	/**
	 * Adding this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from adding this vector and specified vector.
	 */
	public Vector2 add(Vector2 that) {
		int n = Math.min(this.dim(), that.dim());
		Vector2 result = new Vector2(n, 0);
		for (int i = 0; i < n; i++) {
			result.data.set(i, this.data.get(i) + that.data.get(i));
		}
		
		return result;
	}

	
	/**
	 * Multiplying this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public Vector2 multiply(double alpha) {
		Vector2 result = new Vector2(this.dim(), 0);
		for (int i = 0; i < this.dim(); i++) {
			result.data.set(i, alpha * this.data.get(i));
		}
		
		return result;
	}

	
	/**
	 * Dividing this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from dividing this vector by specified number.
	 */
	public Vector2 divide(double alpha) {
		Vector2 result = new Vector2(this.dim(), 0);
		for (int i = 0; i < this.dim(); i++) {
			result.data.set(i, this.data.get(i) / alpha);
		}
		
		return result;
	}

	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.toText(data, ",");
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> list = TextParserUtil.parseTextList(spec, ",");
		data = Util.newList(list.size());
		for (int i = 0; i < list.size(); i++) {
			data.set(i, Double.parseDouble(list.get(i)));
		}
			
	}


	@Override
    public Object clone() {
    	return new Vector2(data);
    }


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}
    
    
}
