/**
 * 
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
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
public class Vector implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal array of double values.
	 */
	protected double[] data = new double[0];
	
	
	/**
	 * Constructor with specified dimension and specified initial value.
	 * Dimension is the number of values in this vector. All values in this vector are assigned by the specified initial value.
	 * @param dim specified dimension.
	 * @param initValue specified initial value.
	 */
	public Vector(int dim, double initValue) {
		data = new double[dim];
		
		Arrays.fill(data, initValue);
	}
	
	
	/**
	 * Constructor with specified array of double values.
	 * @param data specified array of double values.
	 */
	public Vector(double[] data) {
		this.data = Arrays.copyOf(data, data.length);
	}

	
	/**
	 * Constructor with specified list of double values.
	 * @param data specified {@link List} of double values.
	 */
	public Vector(List<Double> data) {
		this.data = DSUtil.toDoubleArray(data);
	}
	
	
	/**
	 * Getting the dimension of this vector. Note, dimension is the number of values in this vector.
	 * @return dimension of this vector.
	 */
	public int dim() {
		return data.length;
	}
	
	
	/**
	 * Getting the value at specified index. 
	 * @param idx specified index.
	 * @return value at specified index.
	 */
	public double get(int idx) {
		return data[idx];
	}
	
	
	/**
	 * Setting a value at specified index.
	 * @param idx specified index.
	 * @param value to be set at specified index.
	 */
	public void set(int idx, double value) {
		data[idx] = value;
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
		if (data.length == 0)
			return 0;
		
		double sum = 0;
		for (double value : data) {
			sum += value;
		}
		return sum / data.length;
	}

	
	/**
	 * Calculating standard deviation of this vector.
	 * @return standard deviation of this vector.
	 */
	public double sd() {
		if (data.length < 2)
			return 0;

		double mean = mean();
		double sum = 0;
		for (double value : data) {
			double deviation = value - mean;
			sum += deviation * deviation;
		}
		return Math.sqrt(sum / data.length - 1);
	}
	
	
	/**
	 * Calculating standard error of this vector.
	 * @return standard error of this vector.
	 */
	public double se() {
		if (data.length == 0)
			return 0;

		return sd() / Math.sqrt(data.length);
	}
	
	
	/**
	 * Calculating Euclidean distance between this vector and the other vector.
	 * @param other other vector.
	 * @return Euclidean distance between this vector and the other vector.
	 */
	public double distance(Vector other) {
		double dis = 0;
		
		int n = data.length;
		for (int i = 0; i < n; i++) {
			double deviate =  data[i] - other.data[i];
			dis += deviate * deviate;
		}
		return Math.sqrt(dis);
	}
	
	
	/**
	 * Calculating the dot product (scalar product) of this vector and the other vector.
	 * @param other other vector
	 * @return dot product (scalar product) of this vector and the other vector.
	 */
	public double product(Vector other) {
		double product = 0;
		int n = data.length;
		for (int i = 0; i < n; i++) {
			product += data[i] * other.data[i];
		}
		
		return product;
	}
	
	
	/**
	 * Calculating the cosine of this vector and the other vector.
	 * @param other other vector.
	 * @return cosine of this vector and the other vector.
	 */
	public double cosine(Vector other) {
		double module1 = module();
		double module2 = other.module();
		if (module1 == 0 || module2 == 0)
			return Constants.UNUSED;
		
		return product(other) / ( module1 * module2);
	}
	
	
	/**
	 * Calculating the correlation coefficient of this vector and the other vector.
	 * @param other other vector.
	 * @return correlation coefficient of this vector and the other vector.
	 */
	public double corr(Vector other) {
		double mean1 = mean();
		double mean2 = other.mean();
		
		int n = data.length;
		double VX = 0, VY = 0;
		double VXY = 0;
		for (int i = 0; i < n; i++) {
			double deviate1 = data[i] - mean1;
			double deviate2 = other.data[i] - mean2;
			
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
	public void concat(Vector vector) {
		int n = this.dim();
		int m = n + vector.dim();
		double[] newdata = Arrays.copyOf(data, m);
		
		for (int i = n; i < m; i++)
			newdata[i] = vector.data[i - n];
		
		this.data = newdata;
	}
	
	
	/**
	 * Subtracting this vector by specified vector.
	 * @param that specified vector.
	 * @return resulted vector from subtracting this vector by specified vector.
	 */
	public Vector subtract(Vector that) {
		int n = Math.min(this.dim(), that.dim());
		Vector result = new Vector(n, 0);
		for (int i = 0; i < n; i++) {
			result.data[i] = this.data[i] - that.data[i];
		}
		
		return result;
	}
	
	
	/**
	 * Adding this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from adding this vector and specified vector.
	 */
	public Vector add(Vector that) {
		int n = Math.min(this.dim(), that.dim());
		Vector result = new Vector(n, 0);
		for (int i = 0; i < n; i++) {
			result.data[i] = this.data[i] + that.data[i];
		}
		
		return result;
	}

	
	/**
	 * Multiplying this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public Vector multiply(double alpha) {
		Vector result = new Vector(this.dim(), 0);
		for (int i = 0; i < this.dim(); i++) {
			result.data[i] = alpha * this.data[i];
		}
		
		return result;
	}

	
	/**
	 * Dividing this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from dividing this vector by specified number.
	 */
	public Vector divide(double alpha) {
		Vector result = new Vector(this.dim(), 0);
		for (int i = 0; i < this.dim(); i++) {
			result.data[i] = this.data[i] / alpha;
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
		data = new double[list.size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = Double.parseDouble(list.get(i));
		}
			
	}


	@Override
    public Object clone() {
    	return new Vector(data);
    }


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}
    
    
}
