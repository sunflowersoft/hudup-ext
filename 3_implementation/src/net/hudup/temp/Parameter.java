package net.hudup.temp;

import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Profile;


/**
 * This abstract represents a (vector) parameter in any distribution.
 * It inherits directly from {@link Profile} class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Parameter extends Profile {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public Parameter() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list as {@link AttributeList}.
	 */
	public Parameter(AttributeList attRef) {
		super(attRef);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Calculating the distance of this parameter and the specified parameter. 
	 * @param other the specified parameter.
	 * @return the distance of this parameter and the specified parameter.
	 */
	public double distance(Parameter other) {
		int count = getAttCount();
		double sum = 0;
		for (int i = 0; i < count; i++) {
			double thisvalue = this.getValueAsReal(i);
			double value = other.getValueAsReal(i);
			double d = thisvalue - value;
			sum += d * d;
		}
		return Math.sqrt(sum);
	}
	
	
}
